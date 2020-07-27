package com.headmostlab.quickbarbell.views.recyclerview.memoryview;

import com.headmostlab.quickbarbell.BasePresenter;
import com.headmostlab.quickbarbell.BaseView;
import com.headmostlab.quickbarbell.model.database.entities.WeightTemplate;

import java.util.List;

public interface MemoryListContract {

    interface View extends BaseView<Presenter> {
        void setPresenter(MemoryListContract.Presenter presenter);

        MemoryListContract.Presenter getPresenter();

        void showTemplates(List<WeightTemplate> templates);

        void showStatus(MemoryListStatus status);

        void syncTemplate(WeightTemplate template);

        void selectAll();
    }

    interface Presenter extends BasePresenter<View> {
        void selectTemplates(List<WeightTemplate> templates);

        void setTemplates(List<WeightTemplate> templates);

        boolean isInEditMode();

        boolean isInHistoryMode();

        void update(WeightTemplate template);

        List<WeightTemplate> getSelectedTemplates();

        MemoryListStatus getStatus();

        void setStatus(MemoryListStatus status);

        void selectAll();
    }

}
