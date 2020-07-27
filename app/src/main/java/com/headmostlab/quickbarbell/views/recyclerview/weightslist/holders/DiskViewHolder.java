package com.headmostlab.quickbarbell.views.recyclerview.weightslist.holders;

import android.view.View;
import android.widget.TextView;

import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.views.HLDiskView;
import com.headmostlab.quickbarbell.views.recyclerview.holder.IDetailsLookupViewHolder;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.model.Card;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DiskViewHolder extends RecyclerView.ViewHolder implements WeightsLIstViewHolder, IDetailsLookupViewHolder, View.OnClickListener {

    @BindView(R.id.textView)
    public TextView textView;

    @BindView(R.id.disk)
    public HLDiskView disk;

    @BindView(R.id.flagView)
    View flag;

    @BindView(R.id.rootLayout)
    View rootLayout;

    private Card card;

    private IViewHolderListener listener;

    public DiskViewHolder(@NonNull View itemView, IViewHolderListener listener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.listener = listener;
        rootLayout.setOnClickListener(this);
    }

    public void setCard(Card card) {
        textView.setText(card.toString());
        disk.setWeightInKg(card.getWeightInKg().floatValue());
        this.card = card;
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

    @Override
    public void onClick(View v) {
        listener.onClick(card);
    }

    public interface IViewHolderListener {
        void onClick(Card card);
    }
}
