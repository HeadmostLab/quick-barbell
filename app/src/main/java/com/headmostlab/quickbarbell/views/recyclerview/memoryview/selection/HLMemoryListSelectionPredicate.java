package com.headmostlab.quickbarbell.views.recyclerview.memoryview.selection;

import com.headmostlab.quickbarbell.views.recyclerview.memoryview.MemoryListContract;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.SelectionTracker;

public class HLMemoryListSelectionPredicate extends SelectionTracker.SelectionPredicate<Long> {

    private final MemoryListContract.Presenter presenter;

    public HLMemoryListSelectionPredicate(MemoryListContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public boolean canSetStateForKey(@NonNull Long key, boolean nextState) {
        return key != 0 && !presenter.isInHistoryMode();
    }

    @Override
    public boolean canSetStateAtPosition(int position, boolean nextState) {
        return position != 0 && !presenter.isInHistoryMode();
    }

    @Override
    public boolean canSelectMultiple() {
        return true;
    }
}
