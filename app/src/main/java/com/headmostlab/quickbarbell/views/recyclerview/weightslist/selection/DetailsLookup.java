package com.headmostlab.quickbarbell.views.recyclerview.weightslist.selection;

import android.view.MotionEvent;
import android.view.View;

import com.headmostlab.quickbarbell.views.recyclerview.holder.IDetailsLookupViewHolder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

public class DetailsLookup extends ItemDetailsLookup<Long> {

    private RecyclerView mRecyclerView;

    public DetailsLookup(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
    }

    @Nullable
    @Override
    public ItemDetails<Long> getItemDetails(@NonNull MotionEvent motionEvent) {
        final View view = mRecyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        if (view != null) {
            final RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(view);
            if (holder instanceof IDetailsLookupViewHolder) {
                return ((IDetailsLookupViewHolder) holder).getItemDetails();
            }
        }
        return null;
    }
}
