package com.headmostlab.quickbarbell.views.recyclerview.weightslist;

import android.content.Context;
import android.util.AttributeSet;

import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.views.recyclerview.HLCustomRecyclerView;
import com.headmostlab.quickbarbell.views.recyclerview.selection.HLItemDetailsLookup;
import com.headmostlab.quickbarbell.views.recyclerview.selection.HLItemKeyProvider;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.model.Card;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;

public class WeightsListView extends HLCustomRecyclerView implements WeightsListContract.View {

    private static final String ENTER_SELECTION_MODE = "ENTER_SELECTION_MODE";
    private WeightsListAdapter adapter;
    private SelectionTracker<Long> tracker;
    private WeightsListContract.Presenter presenter;
    private OnSelectionChanged onSelectionChanged;
    private Events events;
    private OnCardClick onCardClickListener;

    public WeightsListView(@NonNull Context context) {
        this(context, null);
    }

    public WeightsListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.customRecyclerViewStyle);
    }

    public WeightsListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        adapter = new WeightsListAdapter(this);
        setAdapter(adapter);
        setLayoutManager(new LinearLayoutManager(getContext()));
        events = new MyEvents();
    }

    public void enableTracker() {
        if (tracker == null) {
            tracker = createSelectionTracker();
            tracker.addObserver(new MyObserver());
            adapter.setTracker(tracker);
        }
    }

    private SelectionTracker<Long> createSelectionTracker() {
        return new SelectionTracker.Builder<>(
                "weights-list-tracker",
                this,
                new HLItemKeyProvider(this),
                new HLItemDetailsLookup(this),
                StorageStrategy.createLongStorage())
                .withSelectionPredicate(SelectionPredicates.createSelectAnything())
                .build();
    }

    @Override
    public void showWeights(List<Card> cards) {
        adapter.setCards(cards);
    }

    public Events getEvents() {
        return events;
    }

    @Override
    public void setPresenter(WeightsListContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void selectAll() {
        for (int i = 0; i < adapter.getItemCount(); i++) {
            tracker.select((long) i);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void enterInSelectionMode() {
        if (adapter.getItemCount() > 0 && !tracker.hasSelection()) {
            smoothScrollToPosition(0);
            tracker.select(0L);
            adapter.notifyItemChanged(0, ENTER_SELECTION_MODE);
        }
    }

    @Override
    public void exitSelectionMode() {
        if (tracker.hasSelection()) {
            tracker.clearSelection();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void scrollToLast() {
        if (adapter.getItemCount() > 0) {
            smoothScrollToPosition(adapter.getItemCount() - 1);
        }
    }

    @Override
    public void scrollTo(int position) {
        smoothScrollToPosition(position);
    }

    @Nullable
    @Override
    public WeightsListAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void syncCard(Card card) {
        getAdapter().notifyDataSetChanged(card);
    }

    public SelectionTracker<Long> getTracker() {
        return tracker;
    }

    private class MyObserver extends SelectionTracker.SelectionObserver {
        @Override
        public void onSelectionChanged() {
            presenter.selectCards(getSelectedCards());
            if (onSelectionChanged != null) {
                onSelectionChanged.selectionChanged();
            }
        }
    }

    private List<Card> getSelectedCards() {
        List<Card> cards = new ArrayList<>();

        for (Long key : tracker.getSelection()) {
            final Card card = adapter.getCard(key.intValue());
            if (card != null) {
                cards.add(card);
            }
        }
        return cards;
    }


    public void setOnSelectionChanged(OnSelectionChanged listener) {
        onSelectionChanged = listener;
    }

    public void setOnCardClickListener(OnCardClick listener) {
        onCardClickListener = listener;
    }

    public interface OnSelectionChanged {
        void selectionChanged();
    }

    public interface Events {
        void onClick(Card card);
    }

    private class MyEvents implements Events {

        @Override
        public void onClick(Card card) {
            if (onCardClickListener != null) {
                onCardClickListener.onClick(card);
            }
        }
    }

    public interface OnCardClick {
        void onClick(Card card);
    }
}