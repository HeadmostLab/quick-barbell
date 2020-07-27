package com.headmostlab.quickbarbell.views.recyclerview.memoryview;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.model.database.entities.WeightTemplate;
import com.headmostlab.quickbarbell.views.recyclerview.memoryview.holders.AddViewHolder;
import com.headmostlab.quickbarbell.views.recyclerview.memoryview.holders.MemoryViewHolder;
import com.headmostlab.quickbarbell.views.recyclerview.memoryview.views.HLAddWeightView;
import com.headmostlab.quickbarbell.views.recyclerview.memoryview.views.HLWeightMemoryView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

public class HLMemoryViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    enum ViewHolderType {
        ADD,
        TEMPLATE
    }

    private List<MemoryViewHolder> memoryViewHolders;
    private List<WeightTemplate> templates;
    private SelectionTracker<Long> tracker;
    private HLWeightMemoryListView memoryList;

    public HLMemoryViewAdapter(HLWeightMemoryListView memoryList) {
        this.memoryList = memoryList;
        setHasStableIds(true);
        templates = new ArrayList<>();
        memoryViewHolders = new ArrayList<>();
    }

    public void setData(List<WeightTemplate> templates) {
        this.templates.clear();
        this.templates.addAll(templates);
        this.notifyDataSetChanged();
    }

    public void notifyDataSetChanged(WeightTemplate template) {
        final int index = templates.indexOf(template);
        if (index != -1) {
            notifyItemChanged(positionByIndex(index));
        }
    }

    private int positionByIndex(int index) {
        return index + 1;
    }

    public boolean isMemoryViewHolder(int position) {
        return getItemViewType(position) == ViewHolderType.TEMPLATE.ordinal();
    }

    public void setTracker(SelectionTracker<Long> tracker) {
        this.tracker = tracker;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == ViewHolderType.ADD.ordinal()) {
            return new AddViewHolder(
                    (HLAddWeightView) inflater.inflate(R.layout.recycle_view_addmemoryview_item, parent, false),
                    () -> memoryList.getEvents().onAddClick()
            );
        } else {
            final MemoryViewHolder memoryViewHolder = new MemoryViewHolder(
                    (HLWeightMemoryView) inflater.inflate(R.layout.recycle_view_weightmemoryview_item, parent, false),
                    template -> memoryList.getEvents().onMemoryViewClick(template));
            memoryViewHolders.add(memoryViewHolder);
            return memoryViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position > 0) {
            final MemoryViewHolder memoryHolder = (MemoryViewHolder) holder;
            memoryHolder.setData(templates.get(position - 1));
            memoryHolder.setStatus(memoryList.getPresenter().getStatus(), tracker.isSelected((long) position));
        }
    }

    @Override
    public int getItemCount() {
        return 1 + templates.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? ViewHolderType.ADD.ordinal() : ViewHolderType.TEMPLATE.ordinal();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

//    public List<MemoryViewHolder> getMemoryViewHolders() {
//        return memoryViewHolders;
//    }

    public void setViewHoldersByListStatus(MemoryListStatus status) {
        for (MemoryViewHolder memoryViewHolder : memoryViewHolders) {
            memoryViewHolder.setStatus(status, false);
        }
    }

    public WeightTemplate getTemplate(int position) {
        return (position>0 && position <= templates.size()) ? templates.get(position-1) : null;
    }

    public List<WeightTemplate> getData() {
        return templates;
    }

}