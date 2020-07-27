package com.headmostlab.quickbarbell.screens.barinventory;

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
import com.headmostlab.quickbarbell.business.BarTypes;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.utils.AppBarLayoutUtils;
import com.headmostlab.quickbarbell.utils.DialogUtils;
import com.headmostlab.quickbarbell.utils.ResourcesUtils;
import com.headmostlab.quickbarbell.views.HLDigitalFlywheel;
import com.headmostlab.quickbarbell.views.HLDottedScrollBarView;
import com.headmostlab.quickbarbell.views.dialog.BarDialog;
import com.headmostlab.quickbarbell.views.dialog.ShopDialog;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.CarouselView;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards.InventoryBarCard;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.scenes.SimpleCardScene;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.WeightsListContract;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.WeightsListView;

import java.math.BigDecimal;
import java.util.List;

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

public class BarInventoryActivity extends AppCompatActivity implements BarInventoryContract.View, BarDialog.BarDialogListener {

    private static final String EDIT_BAR_DIALOG = "EDIT_BAR_DIALOG";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.digitalFlywheel1)
    HLDigitalFlywheel mDigitalFlywheel1;

    @BindView(R.id.digitalFlywheel2)
    HLDigitalFlywheel mDigitalFlywheel2;

    @BindView(R.id.digitalFlywheel3)
    HLDigitalFlywheel mDigitalFlywheel3;

    @BindView(R.id.recyclerView)
    WeightsListView mRecyclerView;

    @BindView(R.id.carouselView)
    CarouselView<InventoryBarCard> mCarouselView;

    @BindView(R.id.dottedScrollBar)
    HLDottedScrollBarView mScrollBar;

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

    private List<InventoryBarCard> carouselCards;
    private ActionMode mActionMode;
    private BarInventoryContract.Presenter presenter;
    private WeightsListContract.Presenter weightsListPresenter;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        setTheme(App.getInstance().getSettingsModel().getTheme().themeId);

        setContentView(R.layout.activity_bar_inventory);
        ButterKnife.bind(this);

        App.getInstance().getAppComponent().inject(this);

        presenter = ViewModelProviders.of(this, getPresenterFactory()).get(BarInventoryPresenter.class);
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
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle state) {
        super.onSaveInstanceState(state);

        state.putString("presenter", presenter.serialize());
        mRecyclerView.getTracker().onSaveInstanceState(state);
    }

    private ViewModelProvider.Factory getPresenterFactory() {
        return App.getInstance().getAppComponent().barInventoryFactory().create().presenterFactory();
    }

    private void setUpWidgets() {
        setSupportActionBar(mToolbar);
        setUpAppBar();
        setUpCarouselView();
        setUpScrollBar();
        setUpFlywheels();
        setUpSwitch();
    }

    private void setUpAppBar() {
        AppBarLayoutUtils.enableAppBarDragging(mainAppbar, false);
    }

    private void setUpSwitch() {
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

    private void setUpRecyclerView() {
        mRecyclerView.setPresenter(weightsListPresenter);
        mRecyclerView.enableTracker();
        mRecyclerView.setOnSelectionChanged(() -> presenter.onSelectionCardsChanged());
    }

    private void setUpCarouselView() {
        final SimpleCardScene<InventoryBarCard> cardScene = new SimpleCardScene<>(this);
        cardScene.setColorFilter(ResourcesUtils.getColor(R.attr.colorPrimaryVariant, getTheme()));
        mCarouselView.setCardScene(cardScene);
        mCarouselView.setOnCardChangedListener(card -> presenter.selectCardByCarouselView(card));
    }

    private void setUpScrollBar() {
        mScrollBar.setOnCurrentItemChangedListener(itemNum -> presenter.selectCardByScrollBar(carouselCards.get(itemNum-1)));
    }

    private void setUpFlywheels() {
        HLDigitalFlywheel.OnCurNumChanged curNumChanged = flywheel -> presenter.setWeight(getWeight());
        mDigitalFlywheel1.setOnCurNumChangedListener(curNumChanged);
        mDigitalFlywheel2.setOnCurNumChangedListener(curNumChanged);
        mDigitalFlywheel3.setOnCurNumChangedListener(curNumChanged);
    }

    @Override
    public void shoWeight(BigDecimal weight) {
        setWeight(weight);
    }

    @Override
    public void showUnit(MeasurementUnit unit) {
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

    private void setWeight(BigDecimal weight) {

        int w = weight.intValue()*10;
        int first = w / 100;
        int second = w / 10 % 10;
        int third = w % 10;
        mDigitalFlywheel1.setCurNum(first);
        mDigitalFlywheel2.setCurNum(second);
        mDigitalFlywheel3.setCurNum(third);
    }

    @Override
    public void scrollScrollBarTo(InventoryBarCard card) {
        mScrollBar.setCurrentItem(carouselCards.indexOf(card)+1);
    }

    @Override
    public void scrollCarouselViewTo(InventoryBarCard card) {
        mCarouselView.setCurCard(carouselCards.indexOf(card)+1);
    }

    @Override
    public void showCarouselCards(List<InventoryBarCard> cards) {
        carouselCards = cards;
        mScrollBar.setCount(cards.size());
        mCarouselView.setCards(cards);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCarouselView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCarouselView.onResume();
    }

    private BigDecimal getWeight() {
        return new BigDecimal(mDigitalFlywheel1.getCurNum()+""+mDigitalFlywheel2.getCurNum()+"."+mDigitalFlywheel3.getCurNum());
    }

    @Override
    public void showActionModeChangeItem(boolean show) {
        if (mActionMode != null) {
            mActionMode.getMenu().findItem(R.id.item_change).setVisible(show);
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
    public void showEditBarDialog(MeasurementUnit unit, BigDecimal weight, BarTypes barType) {
        if (getSupportFragmentManager().findFragmentByTag(EDIT_BAR_DIALOG) != null) {
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString("weight", weight.toString());
        bundle.putString("unit", unit.name());
        bundle.putString("barType", barType.name());

        final BarDialog dialog = new BarDialog();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), EDIT_BAR_DIALOG);
    }

    @OnClick(R.id.addBar)
    public void addBar(View view) {
        presenter.createBar();
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

    @Override
    public void onDialogPositiveClick(MeasurementUnit unit, BigDecimal weight, BarTypes barType) {
        presenter.updateBar(unit, weight, barType);
    }

    private class MyActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.menu_actionmode_bar_inventory, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.item_change:
                    presenter.startToUpdateBar();
                    return true;
                case R.id.item_select_all:
                    weightsListPresenter.selectAll();
                    return true;
                case R.id.item_delete:
                    final int count = weightsListPresenter.getSelectedCards().size();
                    int msgResId = count==1 ? R.string.abi_dialog_msg_delete_one : R.string.abi_dialog_msg_delete_many;
                    DialogUtils.showDeleteDialog(BarInventoryActivity.this, msgResId, (d, w) -> presenter.deleteSelectedBars());
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