package com.headmostlab.quickbarbell.views.recyclerview.weightslist;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.holders.BarViewHolder;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.holders.DiskViewHolder;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.holders.WeightsLIstViewHolder;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.model.BarCard;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.model.Card;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

public class WeightsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String ENTER_SELECTION_MODE = "ENTER_SELECTION_MODE";
    private final WeightsListView weightsListView;

    enum ViewHolderType {
        BAR,
        DISK;

        boolean equals(int type) {
            return this.ordinal() == type;
        }

    }
    private SelectionTracker<Long> tracker;

    private List<Card> cards;

    public WeightsListAdapter(WeightsListView weightsListView) {
        setHasStableIds(true);
        this.weightsListView = weightsListView;
        this.cards = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (ViewHolderType.BAR.equals(viewType)) {
            return new BarViewHolder(inflater.inflate(R.layout.weightslist_item_bar, parent, false));
        } else {
            return new DiskViewHolder(inflater.inflate(R.layout.weightslist_item_disk, parent, false), (card) -> weightsListView.getEvents().onClick(card));
        }
    }

    public void setTracker(SelectionTracker<Long> tracker) {
        this.tracker = tracker;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (ViewHolderType.BAR.equals(getItemViewType(position))) {
            BarViewHolder barHolder = (BarViewHolder) holder;
            barHolder.setCard((BarCard) cards.get(position));
        } else {
            DiskViewHolder diskHolder = (DiskViewHolder) holder;
            diskHolder.setCard(cards.get(position));
        }

        WeightsLIstViewHolder weightsHolder = (WeightsLIstViewHolder)holder;
        if (tracker != null) {
            weightsHolder.setChecked(tracker.isSelected(getItemId(position)));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        for (Object o : payloads) {
            if (o.equals(ENTER_SELECTION_MODE)) {
                ((WeightsLIstViewHolder)holder).setChecked(tracker.isSelected(getItemId(position)));
                return;
            }
        }
        super.onBindViewHolder(holder, position, payloads);
    }

    public Card getCard(int key) {
        if (key>=0 && key<cards.size()) {
            return cards.get(key);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    @Override
    public int getItemViewType(int position) {
        return cards.get(position) instanceof BarCard ? ViewHolderType.BAR.ordinal() : ViewHolderType.DISK.ordinal();
    }

    public void setCards(List<Card> cards) {
        this.cards.clear();
        this.cards.addAll(cards);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void notifyDataSetChanged(Card card) {
        final int index = cards.indexOf(card);
        if (index != -1) {
            notifyItemChanged(index);
        }
    }
}
