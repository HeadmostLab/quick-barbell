package com.headmostlab.quickbarbell.screens.weighthistory;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.headmostlab.quickbarbell.App;
import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.model.database.entities.WeightHistory;
import com.headmostlab.quickbarbell.utils.DialogUtils;
import com.headmostlab.quickbarbell.views.recyclerview.selection.HLItemDetailsLookup;
import com.headmostlab.quickbarbell.views.recyclerview.selection.HLItemKeyProvider;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WeightHistoryActivity extends AppCompatActivity implements WeightHistoryContract.View {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.titleView)
    TextView titleView;

    @BindView(R.id.historyList)
    RecyclerView historyList;

    private WeightHistoryContract.Presenter presenter;
    private WeightHistoryListAdapter adapter;
    private ActionMode mActionMode;
    private SelectionTracker<Long> tracker;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        setTheme(App.getInstance().getSettingsModel().getTheme().themeId);

        setContentView(R.layout.activity_weight_history);
        ButterKnife.bind(this);

        presenter = ViewModelProviders.of(this, getPresenterFactory()).get(WeightHistoryPresenter.class);

        setUpWidgets();

        restore(state);

        presenter.takeView(this);
    }

    private ViewModelProvider.Factory getPresenterFactory() {
        return App.getInstance().getAppComponent().weightHistoryFactory().create().presenterFactory();
    }

    private void setUpWidgets() {
        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        adapter = new WeightHistoryListAdapter();
        historyList.setAdapter(adapter);
        historyList.setLayoutManager(new LinearLayoutManager(this));
        tracker = createSelectionTracker(historyList);
        adapter.setTracker(tracker);
        tracker.addObserver(new SelectionTracker.SelectionObserver() {
            @Override
            public void onSelectionChanged() {
                presenter.selectCards(getSelectedCards());
            }
        });
    }

    @Override
    public void selectAll() {
        for (int i = 0; i < adapter.getItemCount(); i++) {
            tracker.select((long) i);
        }
        adapter.notifyDataSetChanged();
    }

    private SelectionTracker<Long> createSelectionTracker(RecyclerView recyclerView) {
        return new SelectionTracker.Builder<>(
                "weight-history-tracker",
                recyclerView,
                new HLItemKeyProvider(recyclerView),
                new HLItemDetailsLookup(recyclerView),
                StorageStrategy.createLongStorage())
                .build();
    }

    @Override
    public List<WeightHistory> getSelectedCards() {
        List<WeightHistory> histories = new ArrayList<>();

        for (Long key : tracker.getSelection()) {
            final WeightHistory history = adapter.getCard(key.intValue());
            if (history != null) {
                histories.add(history);
            }
        }
        return histories;
    }

    @Override
    public void enableActionModeSelectAllItem(boolean enable) {
        if (mActionMode != null) {
            mActionMode.getMenu().findItem(R.id.item_select_all).setVisible(enable);
        }
    }

    @Override
    public void enableActionModeDeleteItem(boolean enable) {
        if (mActionMode != null) {
            mActionMode.getMenu().findItem(R.id.item_delete).setEnabled(enable);
        }
    }

    @Override
    public void clearSelection() {
        tracker.clearSelection();
    }

    private void restore(@Nullable Bundle state) {
        if (state != null) {
            presenter.restoreState(state.getString("presenter"));
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle state) {
        super.onSaveInstanceState(state);

        state.putString("presenter", presenter.serialize());
    }

    @Override
    public LifecycleOwner getLifeCycleOwner() {
        return this;
    }

    @Override
    public void showTitle(String title) {
        titleView.setText(title);
    }

    @Override
    public void showHistory(List<WeightHistory> weightHistories) {
        adapter.setCards(weightHistories);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_history, menu);
        return true;
    }

    private void enterInSelectionMode() {
        if (adapter.getItemCount() > 0 && !tracker.hasSelection()) {
            historyList.smoothScrollToPosition(0);
            tracker.select(0L);
            adapter.notifyItemChanged(0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_edit:
                enterInSelectionMode();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void showActionMode() {
        if (mActionMode == null) {
            mActionMode = startSupportActionMode(new MyActionModeCallback());
        }
    }

    @Override
    public void hideActionMode() {
        if (mActionMode != null) {
            mActionMode.finish();
            mActionMode = null;
        }
    }

    private class MyActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.menu_actionmode_weighthistory, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.item_select_all:
                    presenter.selectAll();
                    return true;
                case R.id.item_delete:
                    DialogUtils.showDeleteDialog(WeightHistoryActivity.this, 0, (d, w) -> presenter.deleteHistory());
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            tracker.clearSelection();
            mActionMode = null;
        }
    }
}