package com.headmostlab.quickbarbell.views.recyclerview.weightslist.holders;

import android.view.View;
import android.widget.TextView;

import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.views.HLBarView;
import com.headmostlab.quickbarbell.views.recyclerview.holder.IDetailsLookupViewHolder;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.model.BarCard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BarViewHolder extends RecyclerView.ViewHolder implements WeightsLIstViewHolder, IDetailsLookupViewHolder {

    @BindView(R.id.textView)
    TextView textView;

    @BindView(R.id.bar)
    HLBarView bar;

    @BindView(R.id.flagView)
    View flag;

    public BarViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setCard(BarCard card) {
        textView.setText(card.toString());
        bar.setBarType(card.getBarType());
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

    public void setChecked(boolean checked) {
        flag.setVisibility(checked ? View.VISIBLE : View.INVISIBLE);
    }
}
