package com.headmostlab.quickbarbell.screens.weighthistory;

import com.headmostlab.quickbarbell.business.TransitData;
import com.headmostlab.quickbarbell.model.database.dao.WeightHistoryDao;
import com.headmostlab.quickbarbell.model.database.dao.WeightTemplateDao;
import com.headmostlab.quickbarbell.model.database.entities.WeightHistory;
import com.headmostlab.quickbarbell.utils.Xml;
import com.headmostlab.quickbarbell.utils.XmlParser;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.xml.transform.TransformerException;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class WeightHistoryPresenter extends ViewModel implements WeightHistoryContract.Presenter {

    private WeakReference<WeightHistoryContract.View> view;
    private TransitData transitData;
    private WeightHistoryDao weightHistoryDao;
    private LiveData<List<WeightHistory>> weightHistoryLiveData;
    private XmlParser xmlParser;
    private Provider<Xml> xmlProvider;
    private WeightTemplateDao weightTemplateDao;
    private int selectedHistoriesCount;
    private List<WeightHistory> currentHistories;

    @Inject
    public WeightHistoryPresenter(TransitData transitData, WeightTemplateDao weightTemplateDao,
                                  WeightHistoryDao weightHistoryDao,
                                  Provider<Xml> xmlProvider, XmlParser xmlParser) {
        this.view = new WeakReference<>(null);
        this.currentHistories = new ArrayList<>();
        this.transitData = transitData;
        this.weightTemplateDao = weightTemplateDao;
        this.weightHistoryDao = weightHistoryDao;
        this.xmlProvider = xmlProvider;
        this.xmlParser = xmlParser;
    }

    @Override
    public void takeView(@NonNull WeightHistoryContract.View view) {
        this.view = new WeakReference<>(view);

        view().showTitle(transitData.getSelectedTemplate().getComment());

        loadWeightHistory();
    }

    private void loadWeightHistory() {
        if (weightHistoryLiveData == null) {
            weightHistoryLiveData = weightHistoryDao.getById(transitData.getSelectedTemplate().getId());
        }

        weightHistoryLiveData.observe(view().getLifeCycleOwner(), weightHistories -> {
            view().showHistory(weightHistories);
        });
    }

    @Override
    public void restoreState(String xml) {
        try {
            final Xml x = xmlParser.parse(xml);
            transitData.setSelectedTemplate(weightTemplateDao.getById(x.getLong("templateId")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String serialize() {
        Xml xml = xmlProvider.get();
        xml.putLong("templateId", transitData.getSelectedTemplate().getId());
        try {
            return xml.getString();
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void selectAll() {
        if (isView()) {
            view().selectAll();
        }
    }

    @Override
    public void deleteHistory() {
        weightHistoryDao.deleteAll(currentHistories);
        if (isView()) {
            view().hideActionMode();
            view().clearSelection();
        }
    }

    @Override
    public void selectCards(List<WeightHistory> histories) {
        if (view() == null) {
            return;
        }

        final int oldSize = selectedHistoriesCount;
        final int newSize = view().getSelectedCards().size();
        selectedHistoriesCount = newSize;

        boolean isSizeChanged = oldSize != newSize;
        boolean isSelectionStarted = oldSize == 0 && newSize != 0;
        boolean isSelectionFinished = oldSize != 0 && newSize == 0;

        currentHistories.clear();
        currentHistories.addAll(histories);

        if (isSelectionStarted) {
            view().showActionMode();

        } else if (isSelectionFinished) {
            view().hideActionMode();
        }

        if (isSizeChanged) {
            view().enableActionModeSelectAllItem(weightHistoryLiveData.getValue().size() != newSize);
            view().enableActionModeDeleteItem(newSize != 0);
        }
    }

    public boolean isView() {
        return view() != null;
    }

    public WeightHistoryContract.View view() {
        return view.get();
    }
}