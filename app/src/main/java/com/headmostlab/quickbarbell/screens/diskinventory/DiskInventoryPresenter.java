package com.headmostlab.quickbarbell.screens.diskinventory;

import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.business.billing.BillingRepository;
import com.headmostlab.quickbarbell.business.billing.products.Product;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.database.dao.BunchDao;
import com.headmostlab.quickbarbell.model.database.dao.DiskDao;
import com.headmostlab.quickbarbell.model.database.entities.Disk;
import com.headmostlab.quickbarbell.utils.Xml;
import com.headmostlab.quickbarbell.utils.XmlParser;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.WeightsListContract;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.model.Card;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.model.DiskCard;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.xml.transform.TransformerException;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class DiskInventoryPresenter extends ViewModel implements DiskInventoryContract.Presenter {

    private static final int MAX_DISKS_COUNT = 99;

    private final DiskDao diskDao;
    private final BunchDao bunchDao;
    private final WeightsListContract.Presenter weightsListPresenter;
    private final BillingRepository billingRepository;
    private WeakReference<DiskInventoryContract.View> view;
    private XmlParser xmlParser;
    private Provider<Xml> xmlProvider;
    private LiveData<List<Disk>> disks;
    private List<Card> diskCards;
    private int selectedCardsCount;
    private BigDecimal weight = BigDecimal.ZERO;
    private int count;
    private MeasurementUnit unit;
    private Set<Product> products = Collections.emptySet();

    @Inject
    public DiskInventoryPresenter(DiskDao diskDao, BunchDao bunchDao, Provider<Xml> xmlProvider, XmlParser xmlParser,
                                  WeightsListContract.Presenter weightsListPresenter, BillingRepository billingRepository,
                                  MeasurementUnit systemUnit) {
        this.diskDao = diskDao;
        this.bunchDao = bunchDao;
        this.view = new WeakReference<>(null);
        this.xmlProvider = xmlProvider;
        this.xmlParser = xmlParser;
        this.weightsListPresenter = weightsListPresenter;
        this.billingRepository = billingRepository;
        this.unit = systemUnit;

        this.diskCards = Collections.emptyList();
    }

    @Override
    public void takeView(@NonNull DiskInventoryContract.View view) {
        this.view = new WeakReference<>(view);

        restoreWeight();
        restoreCount();
        restoreUnit();
        showDisks();
        loadProducts();

    }

    private void loadProducts() {
        billingRepository.getProducts().observe(view().getLifeCycleOwner(), prds -> products = prds);
    }

    private void restoreUnit() {
        if (isView()) {
            view().showUnit(unit);
        }
    }

    private void showDisks() {
        if (disks == null) {
            disks = diskDao.getAllLive();
        }
        if (isView()) {
            disks.observe(view().getLifeCycleOwner(), disks -> {
                diskCards = createCards(disks);
                weightsListPresenter.setCards(diskCards);
            });
        }
    }

    private List<Card> createCards(List<Disk> disks) {
        Map<BigDecimal, Map<MeasurementUnit, DiskCard>> cardsMap = new TreeMap<>(Collections.reverseOrder());
        for (Disk disk : disks) {
            BigDecimal weightInKg = disk.getWeightInKg();
            Map<MeasurementUnit, DiskCard> unitMap = cardsMap.get(weightInKg);

            if (unitMap == null) {
                unitMap = new HashMap<>();
                cardsMap.put(weightInKg, unitMap);
            }

            MeasurementUnit unit = disk.getUnit();

            DiskCard diskCard = unitMap.get(unit);

            if (diskCard == null) {
                unitMap.put(unit, new DiskCard(disk));
            } else {
                diskCard.add(disk);
            }
        }

        List<Card> cards = new ArrayList<>();
        for (Map.Entry<BigDecimal, Map<MeasurementUnit, DiskCard>> cardsEntry : cardsMap.entrySet()) {
            cards.addAll(cardsEntry.getValue().values());
        }

        return cards;
    }

    private void restoreWeight() {
        if (isView()) {
            view().shoWeight(weight);
        }
    }

    private void restoreCount() {
        if (isView()) {
            view().showCount(count);
        }
    }

    @Override
    public void restoreState(String xml) {
        try {
            final Xml x = xmlParser.parse(xml);
            weight = x.getBigDecimal("weight");
            count = x.getInt("count");
            unit = MeasurementUnit.values()[x.getInt("unit")];
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String serialize() {
        Xml xml = xmlProvider.get();
        xml.putBigDecimal("weight", weight);
        xml.putInt("count", count);
        xml.putInt("unit", unit.ordinal());
        try {
            return xml.getString();
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    @Override
    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public WeightsListContract.Presenter getWeightsListPresenter() {
        return weightsListPresenter;
    }

    @Override
    public void deleteSelectedDisks() {
        List<Disk> disks = new ArrayList<>();
        final List<Card> selectedCards = weightsListPresenter.getSelectedCards();
        for (Card card : selectedCards) {
            if (card instanceof DiskCard) {
                DiskCard barCard = (DiskCard) card;
                disks.addAll(barCard.getDisks());
            }
        }
//        diskDao.hide(disks);
        diskDao.delete(disks);
        bunchDao.deleteAll();
        view().hideActionMode();
    }

    @Override
    public void onSelectionCardsChanged() {
        if (view() == null) {
            return;
        }

        final int oldSize = selectedCardsCount;
        final int newSize = weightsListPresenter.getSelectedCards().size();
        selectedCardsCount = newSize;

        boolean isSizeChanged = oldSize != newSize;

        if (selectedCardsCount > 0) {
            view().showActionMode();
        } else {
            view().hideActionMode();
        }

        if (isSizeChanged) {
            view().enableActionModeSelectAllItem(diskCards.size() != newSize);
        }
    }

    @Override
    public void setUnit(MeasurementUnit unit) {
        this.unit = unit;
    }

    @Override
    public void diskClicked(DiskCard card) {
        if (isView()) {
            if (weightsListPresenter.getSelectedCards().size() == 0) {
                weight = card.getWeight();
                count = card.getCount();
                unit = card.getUnit();
                view().shoWeight(weight);
                view().showCount(count);
                view().showUnit(unit);
            }
        }
    }

    @Override
    public void createDisks() {
        if (products.isEmpty()) {
            if (isView()) {
                view().showShop();
            }
            return;
        }
        if (weight.compareTo(BigDecimal.ZERO) == 0) {
            showMessage(R.string.adi_specify_weight);
        } else if (weight.compareTo(unit.getMaxWeight()) > 0) {
            showMessage(R.string.adi_max_weight);
        } /*else if (count == 0) {
            showMessage(R.string.adi_specify_count);
        }*/ /*else if (availableCountToAdd(weight) < count) {
            showMessage(R.string.adi_available_to_add);
        }*/ else {
            List<Disk> disks = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                disks.add(new Disk(weight, unit));
            }
//            diskDao.hide(weight, unit);
            diskDao.delete(weight, unit);
            diskDao.insertAll(disks);
            bunchDao.deleteAll();
            weightsListPresenter.scrollTo(findPosition(weight));
            showMessage(R.string.adi_plate_set);
        }
    }

    private int availableCountToAdd(BigDecimal weight) {
        for (Card card : diskCards) {
            if (card instanceof DiskCard) {
                DiskCard diskCard = (DiskCard) card;
                if (diskCard.getWeight().compareTo(weight) == 0) {
                    return MAX_DISKS_COUNT - diskCard.getCount();
                }
            }
        }
        return MAX_DISKS_COUNT;
    }

    private int findPosition(BigDecimal weight) {
        for (int i = 0; i < diskCards.size(); i++) {
            if (weight.compareTo(diskCards.get(i).getWeight()) > 0) {
                return i;
            }
        }
        return diskCards.size() + 1;
    }

    private void showMessage(int resourceId) {
        if (isView()) {
            view().showMessage(resourceId);
        }
    }

    public boolean isView() {
        return view() != null;
    }

    public DiskInventoryContract.View view() {
        return view.get();
    }
}