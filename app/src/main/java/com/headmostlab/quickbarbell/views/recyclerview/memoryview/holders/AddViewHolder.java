package com.headmostlab.quickbarbell.views.recyclerview.memoryview.holders;

import android.view.View;

import com.headmostlab.quickbarbell.views.recyclerview.holder.IDetailsLookupViewHolder;
import com.headmostlab.quickbarbell.views.recyclerview.memoryview.views.HLAddWeightView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

public class AddViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, IDetailsLookupViewHolder {

    private HLAddWeightView addWeightView;
    private IViewHolderListener listener;

    public AddViewHolder(@NonNull HLAddWeightView addWeightView, IViewHolderListener listener) {
        super(addWeightView);
        this.addWeightView = addWeightView;
        this.listener = listener;
        this.addWeightView.setOnClickListener(this);
    }

    public ItemDetailsLookup.ItemDetails<Long> getItemDetails() {
        return new ItemDetailsLookup.ItemDetails<Long>() {
            @Override
            public int getPosition() {
                return getAdapterPosition();
            }

            @Nullable
            @Override
            public Long getSelectionKey() {
                return getItemId();
            }
        };
    }

    @Override
    public void onClick(View v) {
        listener.onClick();
    }

    public interface IViewHolderListener {
        void onClick();
    }
}
