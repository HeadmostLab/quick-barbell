package com.headmostlab.quickbarbell.views.recyclerview.memoryview;

import com.headmostlab.quickbarbell.model.database.entities.WeightTemplate;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;

import static androidx.core.util.Preconditions.checkNotNull;

public class MemoryListPresenter implements MemoryListContract.Presenter {

    private MemoryListStatus status;
    private WeakReference<MemoryListContract.View> view;
    private List<WeightTemplate> templates;
    private List<WeightTemplate> selectedTemplates;

    @Inject
    public MemoryListPresenter() {
        templates = new ArrayList<>();
        selectedTemplates = new ArrayList<>();
        status = MemoryListStatus.NORMAL;
        this.view = new WeakReference<>(null);
    }

    @Override
    public void takeView(@NonNull MemoryListContract.View view) {
        checkNotNull(view, "view cannot be null!");
        this.view = new WeakReference<>(view);
        this.view().showTemplates(templates);
    }

    @Override
    public void selectTemplates(List<WeightTemplate> templates) {
        final int oldSize = selectedTemplates.size();
        final int newSize = templates.size();

        boolean isSelectionStarted = oldSize == 0 && newSize != 0;
        boolean isSelectionFinished = oldSize != 0 && newSize == 0;

        selectedTemplates.clear();
        selectedTemplates.addAll(templates);

        if (isSelectionStarted) {
            setStatus(MemoryListStatus.EDIT);

        } else if (isSelectionFinished) {
            setStatus(MemoryListStatus.NORMAL);
        }

    }

    @Override
    public void setTemplates(List<WeightTemplate> templates) {
        this.templates.clear();
        this.templates.addAll(templates);
        if (isView()) {
            view().showTemplates(templates);
        }
    }

    @Override
    public MemoryListStatus getStatus() {
        return status;
    }

    @Override
    public boolean isInEditMode() {
        return status == MemoryListStatus.EDIT;
    }

    @Override
    public boolean isInHistoryMode() {
        return status == MemoryListStatus.HISTORY;
    }

    @Override
    public void update(WeightTemplate template) {
        if (isView()) {
            view().syncTemplate(template);
        }
    }

    @Override
    public List<WeightTemplate> getSelectedTemplates() {
        return selectedTemplates;
    }

    @Override
    public void setStatus(MemoryListStatus status) {
        if (this.status == status) {
            return;
        }
        this.status = status;

        if (isView()) {
            view().showStatus(status);
        }
    }

    @Override
    public void selectAll() {
        if (!isInEditMode()) {
            setStatus(MemoryListStatus.EDIT);
        }
        if (isView()) {
            view().selectAll();
        }
    }

    public MemoryListContract.View view() {
        return view.get();
    }

    public boolean isView() {
        return view() != null;
    }
}
