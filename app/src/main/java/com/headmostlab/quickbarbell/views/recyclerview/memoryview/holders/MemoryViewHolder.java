package com.headmostlab.quickbarbell.views.recyclerview.memoryview.holders;

import android.view.View;

import com.headmostlab.quickbarbell.App;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.database.entities.WeightTemplate;
import com.headmostlab.quickbarbell.utils.WeightUtils;
import com.headmostlab.quickbarbell.views.recyclerview.holder.IDetailsLookupViewHolder;
import com.headmostlab.quickbarbell.views.recyclerview.memoryview.MemoryListStatus;
import com.headmostlab.quickbarbell.views.recyclerview.memoryview.views.HLWeightMemoryView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

public class MemoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, IDetailsLookupViewHolder {

    private final IViewHolderListener listener;
    public HLWeightMemoryView memoryView;

    public MemoryViewHolder(@NonNull HLWeightMemoryView memoryView, IViewHolderListener listener) {
        super(memoryView);
        this.memoryView = memoryView;
        this.memoryView.setOnClickListener(this);
        this.listener = listener;
    }

    public void setData(WeightTemplate template) {
        MeasurementUnit systemUnit = App.getInstance().getSettingsModel().getUnit();
        memoryView.setWeightTemplate(template);
        memoryView.setComment(template.getComment());
        memoryView.setPercent(template.getPercent());
        memoryView.setWeight(WeightUtils.convert(template.getWeight(), template.getUnit(), systemUnit).intValue()/*template.getWeight().intValue()*/);
        memoryView.setUnit(template.getUnit());
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

    public void setStatus(MemoryListStatus status, boolean isSelected) {

        switch (status) {
            case NORMAL:
                memoryView.setStatus(HLWeightMemoryView.Status.NORMAL);
                break;
            case EDIT:
                memoryView.setStatus(isSelected ? HLWeightMemoryView.Status.CHECKED : HLWeightMemoryView.Status.UNCHECKED);
                break;
            case HISTORY:
                memoryView.setStatus(HLWeightMemoryView.Status.HISTORY);
                break;
        }

//        if (memoryListIsInEditStatus) {
//            memoryView.setStatus(isSelected ? HLWeightMemoryView.Status.CHECKED : HLWeightMemoryView.Status.UNCHECKED);
//        } else {
//            memoryView.setStatus(HLWeightMemoryView.Status.NORMAL);
//        }
    }

    @Override
    public void onClick(View v) {
        listener.onClick(memoryView.getWeightTemplate());
    }

    public interface IViewHolderListener {
        void onClick(WeightTemplate template);
    }
}
