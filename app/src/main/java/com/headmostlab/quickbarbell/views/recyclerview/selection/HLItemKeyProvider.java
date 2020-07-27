package com.headmostlab.quickbarbell.views.recyclerview.selection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.widget.RecyclerView;

public class HLItemKeyProvider extends ItemKeyProvider<Long> {

    private RecyclerView mRecyclerView;

    public HLItemKeyProvider(RecyclerView mRecyclerView) {
        super(ItemKeyProvider.SCOPE_MAPPED);
        this.mRecyclerView = mRecyclerView;
    }

    @Nullable
    @Override
    public Long getKey(int position) {
        return mRecyclerView.getAdapter().getItemId(position);
    }

    @Override
    public int getPosition(@NonNull Long key) {
        final RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForItemId(key);
        return holder != null? holder.getLayoutPosition() : RecyclerView.NO_POSITION;
    }
}
