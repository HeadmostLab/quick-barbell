package com.headmostlab.quickbarbell.views.recyclerview.memoryview;

import android.content.Context;
import android.util.AttributeSet;

import com.headmostlab.quickbarbell.model.database.entities.WeightTemplate;
import com.headmostlab.quickbarbell.views.recyclerview.memoryview.selection.HLMemoryListSelectionPredicate;
import com.headmostlab.quickbarbell.views.recyclerview.selection.HLItemDetailsLookup;
import com.headmostlab.quickbarbell.views.recyclerview.selection.HLItemKeyProvider;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.OperationMonitor;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HLWeightMemoryListView extends RecyclerView implements MemoryListContract.View {

    private LinearLayoutManager layoutManager;
    private HLMemoryViewAdapter adapter;
    private SelectionTracker<Long> tracker;
    private Events events;
    private OnAddClick onAddClickListener;
    private OnMemoryViewClick onMemoryViewClickListener;
    private OnMemoryViewClickInHistoryStatus onMemoryViewClickInHistoryStatus;
    private OnSelectionChanged onSelectionChanged;
    private OperationMonitor operationMonitor;
    private MemoryListContract.Presenter presenter;

    public HLWeightMemoryListView(@NonNull Context context) {
        this(context, null);
    }

    public HLWeightMemoryListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HLWeightMemoryListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        setLayoutManager(layoutManager);
        adapter = new HLMemoryViewAdapter(this);
        setAdapter(adapter);

    }

    public SelectionTracker<Long> getTracker() {
        return tracker;
    }

    @Override
    public void setPresenter(MemoryListContract.Presenter presenter) {
        this.presenter = presenter;

        tracker = createSelectionTracker();
        tracker.addObserver(new MyObserver());
        adapter.setTracker(tracker);
        events = new MyEvents();
    }

    @Override
    public MemoryListContract.Presenter getPresenter() {
        return presenter;
    }

    @Override
    public void showTemplates(List<WeightTemplate> templates) {
        getAdapter().setData(templates);
    }

    @Override
    public void showStatus(MemoryListStatus status) {
        switch (status) {
            case EDIT: {
                if (!tracker.hasSelection()) {
                    tracker.select(1L);
                }
                break;
            }
            case NORMAL:
            case HISTORY: {
                tracker.clearSelection();
                break;
            }
        }
//        updateMemoryViewStatus();
        adapter.setViewHoldersByListStatus(status);
    }

    @Override
    public void syncTemplate(WeightTemplate template) {
        getAdapter().notifyDataSetChanged(template);
    }

    public void setOnSelectionChanged(OnSelectionChanged listener) {
        onSelectionChanged = listener;
    }

    public Events getEvents() {
        return events;
    }

    @Override
    public void selectAll() {
        for (int i = 1; i < adapter.getItemCount(); i++) {
            tracker.select((long) i);
        }
        adapter.notifyDataSetChanged();
    }

    private List<WeightTemplate> getSelectedTemplates() {
        List<WeightTemplate> templates = new ArrayList<>();

        for (Long key : tracker.getSelection()) {
            final WeightTemplate template = adapter.getTemplate(key.intValue());
            if (template != null) {
                templates.add(template);
            }
        }
        return templates;
    }

    public SelectionTracker<Long> createSelectionTracker() {
        operationMonitor = new OperationMonitor();
        return new SelectionTracker.Builder<>(
                "my2-selection-id",
                this,
                new HLItemKeyProvider(this),
                new HLItemDetailsLookup(this),
                StorageStrategy.createLongStorage())
                .withSelectionPredicate(new HLMemoryListSelectionPredicate(presenter))
                .withOperationMonitor(operationMonitor)
                .build();
    }

    //
//    private void updateMemoryViewStatus() {
//        for (MemoryViewHolder memoryViewHolder : adapter.getMemoryViewHolders()) {
//            memoryViewHolder.setStatus(presenter.isInEditMode(), false);
//        }
//    }
    private class MyObserver extends SelectionTracker.SelectionObserver {

        @Override
        public void onSelectionChanged() {
            presenter.selectTemplates(getSelectedTemplates());
            if (onSelectionChanged != null) {
                onSelectionChanged.selectionChanged();
            }
        }

        @Override
        public void onItemStateChanged(@NonNull Object key, boolean selected) {
            if (operationMonitor.isStarted()) {
                HLWeightMemoryListView.this.smoothScrollToPosition(((Long) key).intValue());
            }
        }

    }

    @Nullable
    @Override
    public HLMemoryViewAdapter getAdapter() {
        return adapter;
    }

    public void setOnAddClickListener(OnAddClick onAddClickListener) {
        this.onAddClickListener = onAddClickListener;
    }

    public void setOnMemoryViewClickListener(OnMemoryViewClick onMemoryViewClickListener) {
        this.onMemoryViewClickListener = onMemoryViewClickListener;
    }

    public void setOnMemoryViewInHistoryStatusClickListener(OnMemoryViewClickInHistoryStatus onMemoryViewClickInHistoryStatusListener) {
        this.onMemoryViewClickInHistoryStatus = onMemoryViewClickInHistoryStatusListener;
    }

    public interface OnAddClick {
        void onClick();
    }

    public interface OnMemoryViewClick {
        void onClick(WeightTemplate template);
    }

    public interface OnMemoryViewClickInHistoryStatus {
        void onClick(WeightTemplate template);
    }

    public interface OnSelectionChanged {
        void selectionChanged();
    }

    public interface Events {
        void onAddClick();

        void onMemoryViewClick(WeightTemplate template);
    }

    private class MyEvents implements Events {

        @Override
        public void onAddClick() {
            if (presenter.getStatus().equals(MemoryListStatus.NORMAL)) {
                if (onAddClickListener != null) {
                    onAddClickListener.onClick();
                }
            }
        }

        @Override
        public void onMemoryViewClick(WeightTemplate template) {
            if (presenter.getStatus().equals(MemoryListStatus.NORMAL)) {
                if (onMemoryViewClickListener != null) {
                    onMemoryViewClickListener.onClick(template);
                }
            } else if (presenter.isInHistoryMode()) {
                if (onMemoryViewClickInHistoryStatus != null) {
                    onMemoryViewClickInHistoryStatus.onClick(template);
                }
            }
        }
    }

}
