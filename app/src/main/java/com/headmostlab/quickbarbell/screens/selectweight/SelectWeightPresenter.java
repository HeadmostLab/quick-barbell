package com.headmostlab.quickbarbell.screens.selectweight;

import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.business.BarTypes;
import com.headmostlab.quickbarbell.business.TransitData;
import com.headmostlab.quickbarbell.business.assembledbar.AssembledBar;
import com.headmostlab.quickbarbell.business.assembledbar.AssembledBarsProvider;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.SettingsModel;
import com.headmostlab.quickbarbell.model.database.dao.BarDao;
import com.headmostlab.quickbarbell.model.database.dao.WeightHistoryDao;
import com.headmostlab.quickbarbell.model.database.dao.WeightTemplateDao;
import com.headmostlab.quickbarbell.model.database.entities.Bar;
import com.headmostlab.quickbarbell.model.database.entities.WeightHistory;
import com.headmostlab.quickbarbell.model.database.entities.WeightTemplate;
import com.headmostlab.quickbarbell.utils.WeightUtils;
import com.headmostlab.quickbarbell.utils.Xml;
import com.headmostlab.quickbarbell.utils.XmlParser;
import com.headmostlab.quickbarbell.views.recyclerview.memoryview.MemoryListContract;
import com.headmostlab.quickbarbell.views.recyclerview.memoryview.MemoryListStatus;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class SelectWeightPresenter extends ViewModel implements SelectWeightContract.Presenter {

    private WeakReference<SelectWeightContract.View> view;
    private TransitData transitData;
    private BarDao barDao;
    private WeightTemplateDao weightTemplateDao;
    private MemoryListContract.Presenter memoryList;
    private WeightHistoryDao weightHistoryDao;
    private LiveData<List<WeightTemplate>> weightTemplatesLiveData;
    private List<WeightTemplate> weightTemplates;
    private int selectedTemplatesCount;
    private XmlParser xmlParser;
    private Provider<Xml> xmlProvider;
    private AssembledBarsProvider assembledBarsProvider;
    private final SettingsModel settingsModel;

    private BigDecimal weight = BigDecimal.ZERO;
    private MeasurementUnit systemUnit;
    private int percent = 100;
    private boolean firstLoad;
    private boolean balanced = true;

    @Inject
    public SelectWeightPresenter(
            TransitData transitData, BarDao barDao,
            WeightTemplateDao weightTemplateDao, MemoryListContract.Presenter memoryList,
            WeightHistoryDao weightHistoryDao,
            Provider<Xml> xmlProvider, XmlParser xmlParser,
            MeasurementUnit systemUnit, AssembledBarsProvider assembledBarsProvider,
            SettingsModel settingsModel) {
        this.transitData = transitData;
        this.barDao = barDao;
        this.weightTemplateDao = weightTemplateDao;
        this.memoryList = memoryList;
        this.weightHistoryDao = weightHistoryDao;
        this.view = new WeakReference<>(null);
        this.xmlProvider = xmlProvider;
        this.xmlParser = xmlParser;
        this.systemUnit = systemUnit;
        this.assembledBarsProvider = assembledBarsProvider;
        this.settingsModel = settingsModel;

        initAssembledBarsProvider();
    }

    private void initAssembledBarsProvider() {
        assembledBarsProvider.setBar(transitData.getChosenBar());
        assembledBarsProvider.setAssembledBarCount(1);
    }

    @Override
    public MemoryListContract.Presenter getMemoryList() {
        return memoryList;
    }

    @Override
    public void takeView(@NonNull SelectWeightContract.View view) {
        selectedTemplatesCount = 0;
        firstLoad = true;

        this.view = new WeakReference<>(view);

        view().showWeight(weight);
        view().showPercent(percent);

        startObservingWeightTemplates();

        if (transitData.getChosenBar().getBarType() != BarTypes.DUMBBELL) {
            view().hideBalancedCheckBox();
        }

        view().checkBalancedCheckBox(balanced);
    }

    private void startObservingWeightTemplates() {
        if (weightTemplatesLiveData == null) {
            weightTemplatesLiveData = weightTemplateDao.getByBarId(transitData.getChosenBar().getId());
        }
        weightTemplatesLiveData.observe(view().getLifeCycleOwner(), templates -> {
            memoryList.setTemplates(templates);
            weightTemplates = templates;
            if (templates.size() == 1 && firstLoad) {
                setWeightTemplate(templates.get(0));
                firstLoad = false;
            }
        });
    }

    @Override
    public void setWeightTemplate(WeightTemplate template) {
        final BigDecimal weightInSysUnit = WeightUtils.convert(template.getWeight(), template.getUnit(), systemUnit);
        this.weight = weightInSysUnit;
        this.percent = (int) template.getPercent();
        this.balanced = template.isBalanced();
        if (isView()) {
            view().showWeight(weightInSysUnit);
            view().showPercent((int) template.getPercent());
            view().checkBalancedCheckBox(template.isBalanced());
        }
    }

    @Override
    public void setPercent(int percent) {
        this.percent = percent;
    }

    @Override
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    @Override
    public void setBalanced(boolean balanced) {
        this.balanced = balanced;
    }

    @Override
    public void showWeightHistoryMode() {
        memoryList.setStatus(MemoryListStatus.HISTORY);
    }

    @Override
    public void showHistory(WeightTemplate template) {
        transitData.setSelectedTemplate(template);
        if (isView()) {
            view().showWeightHistory();
        }
    }

    @Override
    public void startToAddOrUpdateTemplate() {
        boolean isHidden = transitData.getChosenBar().getBarType() != BarTypes.DUMBBELL;

        if (memoryList.getSelectedTemplates().size() == 0) {
            if (isView()) {
                view().showTemplateCommentDialog("", weight, percent, balanced, isHidden);
            }
        } else if (memoryList.getSelectedTemplates().size() == 1) {
            if (isView()) {
                WeightTemplate t = memoryList.getSelectedTemplates().get(0);
                view().showTemplateCommentDialog(t.getComment(), t.getWeight(),
                        (int) t.getPercent(), t.isBalanced(), isHidden);
            }
        }
    }

    @Override
    public void updateOrAddTemplate(String comment, BigDecimal weight, int percent, boolean balanced) {
        if (memoryList.getSelectedTemplates().size() == 1) {
            updateTemplate(comment, weight, percent, balanced);

        } else if (memoryList.getSelectedTemplates().size() == 0) {
            addNewTemplate(weight, percent, comment);
        }
        memoryList.setStatus(MemoryListStatus.NORMAL);
    }

    private void updateTemplate(String comment, BigDecimal weight, int percent, boolean balanced) {
        final WeightTemplate template = memoryList.getSelectedTemplates().get(0);
        template.setComment(comment);
        template.setWeight(weight);
        template.setPercent(percent);
        template.setUnit(systemUnit);
        template.setBalanced(balanced);
        memoryList.update(template);
    }

    private void addNewTemplate(BigDecimal weight, int percent, String comment) {
        final WeightTemplate template = new WeightTemplate();
        template.setBarid(transitData.getChosenBar().getId());
        template.setWeight(weight);
        template.setPercent(percent);
        template.setComment(comment);
        template.setUnit(systemUnit);
        template.setBalanced(balanced);
        weightTemplateDao.insert(template);
        weightHistoryDao.insert(template);
    }

    @Override
    public void updateTemplates() {
        weightTemplateDao.update(weightTemplatesLiveData.getValue());
        saveWeightHistory();
    }

    private void saveWeightHistory() {
        if (weightTemplates != null) {
            List<WeightHistory> histories = new ArrayList<>();
            Date date = Calendar.getInstance().getTime();
            for (WeightTemplate template : weightTemplates) {
                WeightHistory lastHistory = weightHistoryDao.getLast(template.getId());
                if (lastHistory == null || lastHistory.getWeight().compareTo(template.getWeight()) != 0
                        || lastHistory.getUnit().compareTo(template.getUnit()) != 0) {
                    histories.add(new WeightHistory(template.getId(), date, template.getWeight(), template.getUnit()));
                }
            }
            weightHistoryDao.insertAll(histories);
        }
    }

    @Override
    public void deleteTemplates() {
        weightTemplates.removeAll(memoryList.getSelectedTemplates());
        weightTemplateDao.delete(memoryList.getSelectedTemplates());
        memoryList.setStatus(MemoryListStatus.NORMAL);
    }

    private void showMessage(int resourceId) {
        if (isView()) {
            view().showMessage(resourceId);
        }
    }

    @Override
    public void findWeights() {
        final BigDecimal preciseWeight = weight.multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100), MathContext.DECIMAL64);

        final BigDecimal rounded = preciseWeight.setScale(0, RoundingMode.HALF_UP);

        if (rounded.compareTo(BigDecimal.ZERO) == 0) {
            showMessage(R.string.asw_specify_weight);
            return;
        }

        transitData.setPreciseWeight(preciseWeight);
        transitData.setBalanced(balanced);

        assembledBarsProvider.setPreciseWeight(preciseWeight);
        assembledBarsProvider.setBalanced(balanced);
        final List<AssembledBar> bars = assembledBarsProvider.getBars();
        final AssembledBar assembledBar = bars.isEmpty() ? null : bars.get(0);

        if (assembledBar == null) {
            showMessage(R.string.asw_not_found);
            return;
        }

        if (isView()) {
            if (settingsModel.isSeveralBarbellSets()) {
                view().showSelectAssembledBarScreen();
            } else {
                transitData.setAssembledBar(assembledBar);
                view().showShowWeightsScreen();
            }
        }
    }

    @Override
    public void onSelectionTemplatesChanged() {
        if (view() == null) {
            return;
        }

        final int oldSize = selectedTemplatesCount;
        final int newSize = memoryList.getSelectedTemplates().size();
        selectedTemplatesCount = newSize;

        boolean isSizeChanged = oldSize != newSize;
        boolean isSelectionStarted = oldSize == 0 && newSize != 0;
        boolean isSelectionFinished = oldSize != 0 && newSize == 0;

        if (isSelectionStarted) {
            view().showActionMode();

        } else if (isSelectionFinished) {
            view().hideActionMode();
            updateTemplates();
        }

        if (isSizeChanged) {
            view().enableActionModeChangeItem(newSize == 1);
            view().enableActionModeSelectAllItem(weightTemplatesLiveData.getValue().size() != newSize);
            view().enableActionModeDeleteItem(newSize != 0);
        }
    }

    @Override
    public BigDecimal getWeight() {
        return weight;
    }

    @Override
    public int getPercent() {
        return percent;
    }

    private Bar getChosenBar() {
        return transitData.getChosenBar();
    }

    private void setChosenBar(long barId) {
        transitData.setChosenBar(barDao.getById(barId));
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        transitData.setPreciseWeight(BigDecimal.ZERO);
    }

    @Override
    public String serialize() {
        try {
            Xml xml = xmlProvider.get();
            xml.putBigDecimal("weight", weight);
            xml.putInt("percent", percent);
            xml.putBoolean("balanced", balanced);
            xml.putLong("barId", getChosenBar().getId());
            return xml.getString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void restoreState(String xml) {
        try {
            Xml x = xmlParser.parse(xml);
            weight = x.getBigDecimal("weight");
            percent = x.getInt("percent");
            balanced = x.getBoolean("balanced");
            setChosenBar(x.getLong("barId"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isView() {
        return view() != null;
    }

    public SelectWeightContract.View view() {
        return view.get();
    }
}