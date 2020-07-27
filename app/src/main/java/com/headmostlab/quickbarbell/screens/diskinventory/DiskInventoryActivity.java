package com.headmostlab.quickbarbell.screens.diskinventory;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;
import com.headmostlab.quickbarbell.App;
import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.utils.AppBarLayoutUtils;
import com.headmostlab.quickbarbell.utils.DialogUtils;
import com.headmostlab.quickbarbell.views.HLDigitalFlywheel;
import com.headmostlab.quickbarbell.views.dialog.ShopDialog;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.WeightsListContract;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.WeightsListView;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.model.DiskCard;

import java.math.BigDecimal;
import java.util.Locale;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DiskInventoryActivity extends AppCompatActivity implements DiskInventoryContract.View {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recyclerView)
    WeightsListView mRecyclerView;

    @BindView(R.id.weightDigitalFlywheel1)
    HLDigitalFlywheel mWeightDigitalFlywheel1;

    @BindView(R.id.weightDigitalFlywheel2)
    HLDigitalFlywheel mWeightDigitalFlywheel2;

    @BindView(R.id.weightDigitalFlywheel3)
    HLDigitalFlywheel mWeightDigitalFlywheel3;

    @BindView(R.id.weightDigitalFlywheel4)
    HLDigitalFlywheel mWeightDigitalFlywheel4;

    @BindView(R.id.countDigitalFlywheel1)
    HLDigitalFlywheel mCountDigitalFlywheel1;

    @BindView(R.id.countDigitalFlywheel2)
    HLDigitalFlywheel mCountDigitalFlywheel2;

    @BindView(R.id.coordinatorLayout)
    View coordinatorLayout;

    @BindView(R.id.switchView)
    MaterialButtonToggleGroup switchView;

    @BindView(R.id.kgButton)
    MaterialButton kgButton;

    @BindView(R.id.lbButton)
    MaterialButton lbButton;

    @BindView(R.id.mainAppbar)
    AppBarLayout mainAppbar;

    @Inject
    ShopDialog shopDialog;

    private ActionMode mActionMode;
    private DiskInventoryContract.Presenter presenter;
    private WeightsListContract.Presenter weightsListPresenter;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        setTheme(App.getInstance().getSettingsModel().getTheme().themeId);

        setContentView(R.layout.activity_disk_inventory);
        ButterKnife.bind(this);

        App.getInstance().getAppComponent().inject(this);

        presenter = ViewModelProviders.of(this, getPresenterFactory()).get(DiskInventoryPresenter.class);
        weightsListPresenter = presenter.getWeightsListPresenter();

        setUpRecyclerView();

        restore(state);

        setUpWidgets();

        weightsListPresenter.takeView(mRecyclerView);
        presenter.takeView(this);
    }

    private void restore(@Nullable Bundle state) {
        if (state != null) {
            presenter.restoreState(state.getString("presenter"));
            mRecyclerView.getTracker().onRestoreInstanceState(state);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle state) {
        super.onSaveInstanceState(state);

        state.putString("presenter", presenter.serialize());
        mRecyclerView.getTracker().onSaveInstanceState(state);
    }

    private ViewModelProvider.Factory getPresenterFactory() {
        return App.getInstance().getAppComponent().diskInventoryFactory().create().presenterFactory();
    }

    private void setUpWidgets() {
        setSupportActionBar(mToolbar);
        setUpAppBar();
        setUpFlywheelWeight();
        setUpFlywheelCount();
        setUpSwitch();
    }

    private void setUpAppBar() {
        AppBarLayoutUtils.enableAppBarDragging(mainAppbar, false);
    }

    private void setUpSwitch() {
//        switchView.setButtons(new String[]{MeasurementUnit.KILOGRAM.getLabel(), MeasurementUnit.POUND.getLabel()});
//        switchView.setListener(current -> presenter.setUnit(MeasurementUnit.values()[current]));
        switchView.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                switch (checkedId) {
                    case R.id.kgButton:
                        presenter.setUnit(MeasurementUnit.KILOGRAM);
                        break;
                    case R.id.lbButton:
                        presenter.setUnit(MeasurementUnit.POUND);
                        break;
                }
            }
        });
    }

    private void setUpFlywheelWeight() {
        HLDigitalFlywheel.OnCurNumChanged curNumChanged = flywheel -> presenter.setWeight(getWeight());
        mWeightDigitalFlywheel1.setOnCurNumChangedListener(curNumChanged);
        mWeightDigitalFlywheel2.setOnCurNumChangedListener(curNumChanged);
        mWeightDigitalFlywheel3.setOnCurNumChangedListener(curNumChanged);
        mWeightDigitalFlywheel4.setOnCurNumChangedListener(curNumChanged);
    }

    private void setUpFlywheelCount() {
        HLDigitalFlywheel.OnCurNumChanged curNumChanged = flywheel -> presenter.setCount(getCount());
        mCountDigitalFlywheel1.setOnCurNumChangedListener(curNumChanged);
        mCountDigitalFlywheel2.setOnCurNumChangedListener(curNumChanged);
    }

    private void setUpRecyclerView() {
        mRecyclerView.setPresenter(weightsListPresenter);
        mRecyclerView.enableTracker();
        mRecyclerView.setOnSelectionChanged(() -> presenter.onSelectionCardsChanged());
        mRecyclerView.setOnCardClickListener((card) -> presenter.diskClicked(((DiskCard)card)));
    }

    private BigDecimal getWeight() {
        return new BigDecimal(mWeightDigitalFlywheel1.getCurNum()+""+mWeightDigitalFlywheel2.getCurNum()+"."+
                mWeightDigitalFlywheel3.getCurNum()+""+mWeightDigitalFlywheel4.getCurNum());
    }

    private int getCount() {
        return mCountDigitalFlywheel1.getCurNum() * 10 + mCountDigitalFlywheel2.getCurNum();
    }

    private void setWeight(BigDecimal weight) {
        final String s = String.format(Locale.ENGLISH, "%05.2f", weight.floatValue());
        mWeightDigitalFlywheel1.setCurNum(Integer.valueOf(s.substring(0,1)));
        mWeightDigitalFlywheel2.setCurNum(Integer.valueOf(s.substring(1,2)));
        mWeightDigitalFlywheel3.setCurNum(Integer.valueOf(s.substring(3,4)));
        mWeightDigitalFlywheel4.setCurNum(Integer.valueOf(s.substring(4,5)));
    }

    private void setCount(int count) {
        mCountDigitalFlywheel1.setCurNum(count/10);
        mCountDigitalFlywheel2.setCurNum(count%10);
    }

    @Override
    public void shoWeight(BigDecimal weight) {
        setWeight(weight);
    }

    @Override
    public void showUnit(MeasurementUnit unit) {
//        switchView.setCurrent(unit.ordinal());
        switch(unit) {
            case KILOGRAM:
                kgButton.toggle();
                break;
            case POUND:
                lbButton.toggle();
                break;
        }
    }

    @Override
    public void showCount(int count) {
        setCount(count);
    }

    @Override
    public LifecycleOwner getLifeCycleOwner() {
        return this;
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

    @Override
    public void enableActionModeSelectAllItem(boolean enable) {
        if (mActionMode != null) {
            mActionMode.getMenu().findItem(R.id.item_select_all).setVisible(enable);
        }
    }

    @Override
    public void showMessage(int resourceId) {
        Snackbar.make(coordinatorLayout, resourceId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showShop() {
        shopDialog.show(getSupportFragmentManager(), "shop-dialog");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_edit:
                weightsListPresenter.enterInSelectionMode();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.addDisk)
    public void addDisk(View view) {
        presenter.createDisks();
    }

    private class MyActionModeCallback implements ActionMode.Callback {


        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.menu_actionmode_inventory, menu);
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
                    weightsListPresenter.selectAll();
                    return true;
                case R.id.item_delete:
                    DialogUtils.showDeleteDialog(DiskInventoryActivity.this, R.string.adi_dialog_msg_delete_many,
                            (d, w) -> presenter.deleteSelectedDisks());
                    return true;
            }
            return false;
        }
        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            weightsListPresenter.exitSelectionMode();
            mActionMode = null;
        }

    }
}