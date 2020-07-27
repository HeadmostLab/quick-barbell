package com.headmostlab.quickbarbell.screens.weighthistory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.headmostlab.quickbarbell.App;
import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.database.entities.WeightHistory;
import com.headmostlab.quickbarbell.utils.BigDecimalUtils;
import com.headmostlab.quickbarbell.utils.WeightUtils;
import com.headmostlab.quickbarbell.views.recyclerview.holder.IDetailsLookupViewHolder;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

public class WeightHistoryListAdapter extends RecyclerView.Adapter<WeightHistoryListAdapter.MyViewHolder>  {

    private List<WeightHistory> cards;
    private MeasurementUnit systemUnit;
    private SelectionTracker<Long> tracker;

    public WeightHistoryListAdapter() {
        cards = new ArrayList<>();
        systemUnit = App.getInstance().getSettingsModel().getUnit();
        setHasStableIds(true);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements IDetailsLookupViewHolder {
        private TextView textView;
        private View flagView;

        public MyViewHolder(@NonNull View view) {
            super(view);
            textView = view.findViewById(R.id.textView);
            flagView = view.findViewById(R.id.flagView);
        }

        private void setText(String text) {
            textView.setText(text);
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

        public void setChecked(boolean selected) {
            flagView.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void setTracker(SelectionTracker<Long> tracker) {
        this.tracker = tracker;
    }

    public WeightHistory getCard(int position) {
        return position<cards.size() ? cards.get(position) : null;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new MyViewHolder(inflater.inflate(R.layout.weight_history_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final WeightHistory history = cards.get(position);
        final BigDecimal weight = WeightUtils.convert(history.getWeight(), history.getUnit(), systemUnit);
        String weightText = BigDecimalUtils.toString(weight);

        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        String dateText = df.format(history.getDate());

        holder.setText(dateText + " - " + weightText);
        holder.setChecked(tracker.isSelected(getItemId(position)));
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public void setCards(List<WeightHistory> cards) {
        this.cards.clear();
        this.cards.addAll(cards);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
