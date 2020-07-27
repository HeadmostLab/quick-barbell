package com.headmostlab.quickbarbell.views.recyclerview.selection;

import android.view.MotionEvent;
import android.view.View;

import com.headmostlab.quickbarbell.views.recyclerview.holder.IDetailsLookupViewHolder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

public class HLItemDetailsLookup extends ItemDetailsLookup<Long> {

    private final RecyclerView recyclerView;

    public HLItemDetailsLookup(@NonNull RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Nullable
    @Override
    public ItemDetails<Long> getItemDetails(@NonNull MotionEvent motionEvent) {
        final View view = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        if (view != null) {
            final RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(view);
            if (holder instanceof IDetailsLookupViewHolder) {
                return ((IDetailsLookupViewHolder) holder).getItemDetails();
            }
        }
        return null;
    }
}
