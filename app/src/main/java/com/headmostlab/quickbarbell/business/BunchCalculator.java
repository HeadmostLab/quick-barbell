package com.headmostlab.quickbarbell.business;

import com.google.common.math.BigIntegerMath;
import com.headmostlab.quickbarbell.model.database.dao.BunchDao;
import com.headmostlab.quickbarbell.model.database.dao.BunchDiskLinkDao;
import com.headmostlab.quickbarbell.model.database.dao.DiskDao;
import com.headmostlab.quickbarbell.model.database.entities.Bunch;
import com.headmostlab.quickbarbell.model.database.entities.BunchDiskLink;
import com.headmostlab.quickbarbell.model.database.entities.Disk;
import com.headmostlab.quickbarbell.utils.DiskUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class BunchCalculator {

    public enum CalcStatus {
        UNDEFINED,
        CALCULATING,
        FINISHED,
        CANCELED,
        ERROR
    }

    private final Object progressLock = new Object();
    private BigInteger taskCount = BigInteger.ONE;
    private BigInteger processedCount = BigInteger.ZERO;
    private Map<String, BigInteger> combinationsCache = new HashMap<>();
    private final DiskDao diskDao;
    private final BunchDao bunchDao;
    private final BunchDiskLinkDao bunchDiskLinkDao;
    private volatile CalcStatus calcStatus = CalcStatus.UNDEFINED;
    private CompositeDisposable disposables = new CompositeDisposable();

    private OnStatusChanged onStatusChangedListener;
    private OnProgressChanged onProgressChangedListener;

    @Inject
    public BunchCalculator(DiskDao diskDao, BunchDao bunchDao, BunchDiskLinkDao bunchDiskLinkDao) {
        this.diskDao = diskDao;
        this.bunchDao = bunchDao;
        this.bunchDiskLinkDao = bunchDiskLinkDao;
    }

    public interface OnStatusChanged {
        void change(CalcStatus newStatus);

    }

    public interface OnProgressChanged {
        void progress(float percent);

    }

    public void setOnStatusChangedListener(OnStatusChanged listener) {
        this.onStatusChangedListener = listener;
    }

    public void setOnProgressChangedListener(OnProgressChanged listener) {
        this.onProgressChangedListener = listener;
    }

    public void cancel() {
        if (isCalculating()) {
            disposables.clear();
            setCalcStatus(CalcStatus.CANCELED);
        }
    }

    public boolean isCalculating() {
        return getCalcStatus() == CalcStatus.CALCULATING;
    }

    public float getCalcProgress() {
        return new BigDecimal(processedCount).divide(new BigDecimal(taskCount), 5, BigDecimal.ROUND_UP).floatValue() * 100;
    }

    public CalcStatus getCalcStatus() {
        return calcStatus;
    }

    public void calcWeights() {

        if (isCalculating()) {
            return;
        }

        setCalcStatus(CalcStatus.CALCULATING);

        List<Disk> disks = diskDao.getAll(); // getDisksHalf(diskDao.getAll());
        int n = disks.size();

        diskDao.deleteHidden();
        bunchDao.deleteAll();

        if (n == 0) {
            setCalcStatus(CalcStatus.FINISHED);
            return;
        }

        processedCount = BigInteger.ZERO;
        taskCount = BigInteger.ZERO;
        for (int i = 1; i <= n; i++) {
            taskCount = taskCount.add(calcCombinations(i, n));
        }

        dispatchProgress();

        disposables.add(Flowable.range(1, n)
                .parallel()
                .runOn(Schedulers.computation())
                .map(i -> {
                    final Map<BigDecimal, List<List<Disk>>> map = new FindWeights(disks, n, i).call();
                    increaseProgress(calcCombinations(i, n).subtract(calcRealCombinations(map)));
                    return map;
                })
                .sequential()
                .observeOn(Schedulers.single())
                .flatMap((Function<Map<BigDecimal, List<List<Disk>>>, Flowable<Map.Entry<BigDecimal, List<List<Disk>>>>>)
                        map -> Flowable.fromIterable(map.entrySet()))
                .doOnNext(entry -> {
                    for (List<Disk> diskSet : entry.getValue()) {
                        createBunch(entry.getKey(), diskSet);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.single())
                .subscribe(entry -> {
                    increaseProgress(BigInteger.valueOf(entry.getValue().size()));
                    dispatchProgress();
                }, error -> setCalcStatus(CalcStatus.ERROR), () -> setCalcStatus(CalcStatus.FINISHED))
        );

    }

    private void createBunch(BigDecimal weight, List<Disk> disks) {
        final Bunch bunch = new Bunch(weight, disks.size(), DiskUtils.isBalanced(disks));
        bunchDao.insert(bunch);

        List<BunchDiskLink> links = new ArrayList<>();

        for (Disk disk : disks) {
            links.add(new BunchDiskLink(bunch.getBunchId(), disk.getDiskId()));
        }
        bunchDiskLinkDao.insertAll(links);
    }

    private void dispatchStatus() {
        if (onStatusChangedListener != null) {
            onStatusChangedListener.change(calcStatus);
        }
    }

    private void dispatchProgress() {
        if (onProgressChangedListener != null) {
            onProgressChangedListener.progress(getCalcProgress());
        }
    }

    private void setCalcStatus(CalcStatus calcStatus) {
        if (calcStatus != this.calcStatus) {
            this.calcStatus = calcStatus;
            dispatchStatus();
        }
    }

    /**
     * https://www.matburo.ru/tvart_sub.php?p=calc_C
     * n!/((n-k)!*k!);
     */
    private BigInteger calcCombinations(int k, int n) {
        String key = k + "-" + n;
        BigInteger result = combinationsCache.get(key);
        if (result == null) {
            result = BigIntegerMath.factorial(n).divide(BigIntegerMath.factorial(n - k).multiply(BigIntegerMath.factorial(k)));
            combinationsCache.put(key, result);
        }
        return result;
    }

    private void increaseProgress(BigInteger val) {
        synchronized (progressLock) {
            processedCount = processedCount.add(val);
        }
    }

    private BigInteger calcRealCombinations(Map<BigDecimal, List<List<Disk>>> map) {
        BigInteger result = BigInteger.ZERO;
        for (List<List<Disk>> item : map.values()) {
            result = result.add(BigInteger.valueOf(item.size()));
        }
        return result;
    }

    private static class FindWeights implements Callable<Map<BigDecimal, List<List<Disk>>>> {

        private int elementsCount;
        private int countInSet;
        private Map<BigDecimal, List<List<Disk>>> result;
        private List<Disk> disks;

        /**
         * @param disks
         * @param elementsCount общее количество элементов
         * @param countInSet    количество элементов в выборке
         */
        public FindWeights(List<Disk> disks, int elementsCount, int countInSet) {
            this.disks = disks;
            this.elementsCount = elementsCount;
            this.countInSet = countInSet;
        }

        @Override
        public Map<BigDecimal, List<List<Disk>>> call() {
            result = new HashMap<>();
            generateVariants(new int[countInSet], countInSet, elementsCount);
            return result;
        }

        private void variant(int[] a) {
            List<Disk> var = new ArrayList<>();
            BigDecimal weight = BigDecimal.ZERO;
            for (int i : a) {
                final Disk disk = disks.get(i - 1);
                var.add(disk);
                weight = weight.add(disk.getWeightInKg());
            }

            if (!findSameDiskSet(var, weight)) {
                List<List<Disk>> diskSetsWithSameWeight = result.get(weight);
                if (diskSetsWithSameWeight == null) {
                    diskSetsWithSameWeight = new ArrayList<>();
                    result.put(weight, diskSetsWithSameWeight);
                }
                diskSetsWithSameWeight.add(var);
            }
        }

        private boolean findSameDiskSet(List<Disk> var, BigDecimal weight) {
            boolean found = false;
            final List<List<Disk>> sameWeightsDiskSets = result.get(weight);
            if (sameWeightsDiskSets != null) {
                for (List<Disk> diskSet : sameWeightsDiskSets) {
                    List<Disk> varTmp = new ArrayList<>(var);
                    for (Disk disk : diskSet) {
                        boolean foundSameWeight = false;
                        for (Disk varDisk : varTmp) {
                            if (disk.getWeight().compareTo(varDisk.getWeight()) == 0
                                    && disk.getUnit().equals(varDisk.getUnit()))
                            {
                                foundSameWeight = true;
                                varTmp.remove(varDisk);
                                break;
                            }
                        }
                        if (!foundSameWeight) {
                            break;
                        }
                    }
                    if (varTmp.isEmpty()) {
                        found = true;
                        break;
                    }
                }
            }
            return found;
        }

        // генерация сочетаний n - количество элеметов, k - количество элеметов в выборке
        private void generateVariants(int[] a, int k, int n) {
            for (int i = 1; i <= k; i++) {
                a[i - 1] = i;
            }
            if (k == n) {
                variant(a);
                return;
            }
            int p = k;
            while (p >= 1) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                variant(a);
                p = (a[k - 1] == n) ? p - 1 : k;
                if (p >= 1) {
                    for (int i = k; i >= p; i--) {
                        a[i - 1] = a[p - 1] + i - p + 1;
                    }
                }
            }
        }
    }
}