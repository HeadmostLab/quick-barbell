package com.headmostlab.quickbarbell.screens.selectweight;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.snackbar.Snackbar;
import com.headmostlab.quickbarbell.App;
import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.model.SettingsModel;
import com.headmostlab.quickbarbell.screens.selectassembledbar.SelectAssembledBarActivity;
import com.headmostlab.quickbarbell.screens.showweights.ShowWeightsActivity;
import com.headmostlab.quickbarbell.screens.weighthistory.WeightHistoryActivity;
import com.headmostlab.quickbarbell.utils.DialogUtils;
import com.headmostlab.quickbarbell.utils.LogMethod;
import com.headmostlab.quickbarbell.views.HLDigitalFlywheel;
import com.headmostlab.quickbarbell.views.HLSpinnerProgressBar;
import com.headmostlab.quickbarbell.views.dialog.TemplateDialog;
import com.headmostlab.quickbarbell.views.recyclerview.memoryview.HLWeightMemoryListView;
import com.headmostlab.quickbarbell.views.recyclerview.memoryview.MemoryListContract;
import com.headmostlab.quickbarbell.views.recyclerview.memoryview.MemoryListStatus;

import java.math.BigDecimal;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectWeightActivity extends AppCompatActivity
        implements SelectWeightContract.View, TemplateDialog.TemplateDialogListener {

    private static final String DIALOG_TAG_TEMPLATE = "TEMPLATE_DIALOG";

    @BindView(R.id.digitalFlywheel)
    HLDigitalFlywheel mDigitalFlywheel;

    @BindView(R.id.digitalFlywheel2)
    HLDigitalFlywheel mDigitalFlywheel2;

    @BindView(R.id.digitalFlywheel3)
    HLDigitalFlywheel mDigitalFlywheel3;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.progressBarWeightPercent)
    HLSpinnerProgressBar mProgressBarWeightPercent;

    @BindView(R.id.memoryList)
    HLWeightMemoryListView memoryList;

    @BindView(R.id.balancedCheckBox)
    MaterialCheckBox balancedCheckBox;

    @BindView(R.id.coordinatorLayout)
    View coordinatorLayout;

    @Inject
    SettingsModel settingsModel;

    private SelectWeightContract.Presenter presenter;

    private ActionMode mActionMode;

    private MemoryListContract.Presenter memoryListPresenter;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        App.getInstance().getAppComponent().inject(this);
        setTheme(settingsModel.getTheme().themeId);

        setContentView(R.layout.activity_select_weight_old);
        ButterKnife.bind(this);

        presenter = ViewModelProviders.of(this, getPresenterFactory()).get(SelectWeightPresenter.class);
        memoryListPresenter = presenter.getMemoryList();

        setUpWidgets();

        restore(state);

        memoryListPresenter.takeView(memoryList);
        presenter.takeView(this);
    }

    private ViewModelProvider.Factory getPresenterFactory() {
        return App.getInstance().getAppComponent().selectWeightPresenterFactory().create().presenterFactory();
    }

    private void restore(@Nullable Bundle state) {
        if (state != null) {
            presenter.restoreState(state.getString("presenter"));
            memoryList.getTracker().onRestoreInstanceState(state);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle state) {
        super.onSaveInstanceState(state);

        state.putString("presenter", presenter.serialize());
        memoryList.getTracker().onSaveInstanceState(state);
    }

    private void setUpWidgets() {
        setSupportActionBar(mToolbar);
        setUpProgressBar();
        setUpDigitalFlywheels();
        setUpMemoryList();
        setUpBalanceCheckBox();
    }

    private void setUpBalanceCheckBox() {
        balancedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> presenter.setBalanced(isChecked));
    }

    private void setUpMemoryList() {
        memoryList.setPresenter(memoryListPresenter);
        memoryList.setOnSelectionChanged(() -> presenter.onSelectionTemplatesChanged());
        memoryList.setOnAddClickListener(() -> presenter.startToAddOrUpdateTemplate());
        memoryList.setOnMemoryViewClickListener(template -> presenter.setWeightTemplate(template));
        memoryList.setOnMemoryViewInHistoryStatusClickListener(template -> presenter.showHistory(template));
    }

    private void setUpProgressBar() {
        mProgressBarWeightPercent.setClientOnProgressChanged(progress -> {
            presenter.setPercent(getPercent());
            LogMethod.e();
        });
    }

    private void setUpDigitalFlywheels() {
        final HLDigitalFlywheel.OnCurNumAnimatedChanged flywheelOnCurNumAnimatedChanged = flywheel -> {
            presenter.setWeight(getFlywheelAnimatedNum());
        };

        mDigitalFlywheel.setOnCurNumAnimatedChangedListener(flywheelOnCurNumAnimatedChanged);
        mDigitalFlywheel2.setOnCurNumAnimatedChangedListener(flywheelOnCurNumAnimatedChanged);
        mDigitalFlywheel3.setOnCurNumAnimatedChangedListener(flywheelOnCurNumAnimatedChanged);
    }

    private void setWeight(BigDecimal weight) {
        int w = weight.intValue();
        int first = w / 100;
        int second = w / 10 % 10;
        int third = w % 10;
        mDigitalFlywheel.setCurNum(first);
        mDigitalFlywheel2.setCurNum(second);
        mDigitalFlywheel3.setCurNum(third);
    }

    private BigDecimal getFlywheelAnimatedNum() {
        return new BigDecimal(mDigitalFlywheel.getAnimatedCurNum() + "" + mDigitalFlywheel2.getAnimatedCurNum() +
                "" + mDigitalFlywheel3.getAnimatedCurNum());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_select_weight, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_edit_weights:
                memoryListPresenter.setStatus(MemoryListStatus.EDIT);
                return true;
            case R.id.action_show_weight_history:
                presenter.showWeightHistoryMode();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(String comment, BigDecimal weight, int percent, boolean balanced) {
        presenter.updateOrAddTemplate(comment, weight, percent, balanced);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }

    private int getPercent() {
        return Math.round(mProgressBarWeightPercent.getProgress());
    }

    @OnClick(R.id.findButton)
    public void findWeights(View view) {
        presenter.findWeights();
    }

    @Override
    public void showSelectAssembledBarScreen() {
        startActivity(new Intent(this, SelectAssembledBarActivity.class));
    }

    @Override
    public void showShowWeightsScreen() {
        startActivity(new Intent(this, ShowWeightsActivity.class));
    }

    @Override
    public LifecycleOwner getLifeCycleOwner() {
        return this;
    }

    @Override
    public void showWeight(BigDecimal weight) {
        setWeight(weight);
    }

    @Override
    public void showPercent(int percent) {
        mProgressBarWeightPercent.setProgress(percent);
    }

    @Override
    public void showTemplateCommentDialog(String comment, BigDecimal weight, int percent, boolean balanced, boolean hideBalanced) {
        if (getSupportFragmentManager().findFragmentByTag(DIALOG_TAG_TEMPLATE) != null) {
            return;
        }

        final Bundle bundle = new Bundle();
        bundle.putString("weightTitle", comment);
        bundle.putString("weight", weight.toString());
        bundle.putString("percent", Integer.toString(percent));
        bundle.putBoolean("balanced", balanced);
        bundle.putBoolean("hideBalanced", hideBalanced);

        final TemplateDialog dialog = new TemplateDialog();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), DIALOG_TAG_TEMPLATE);
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
    public void enableActionModeChangeItem(boolean enable) {
        if (mActionMode != null) {
            mActionMode.getMenu().findItem(R.id.item_change).setVisible(enable);
        }
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
    public void showMessage(int resourceId) {
        Snackbar.make(coordinatorLayout, resourceId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void hideBalancedCheckBox() {
        balancedCheckBox.setVisibility(View.INVISIBLE);
    }

    @Override
    public void checkBalancedCheckBox(boolean on) {
        balancedCheckBox.setChecked(on);
    }

    @Override
    public void showWeightHistory() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(new Intent(this, WeightHistoryActivity.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(new Intent(this, WeightHistoryActivity.class));
        }
    }

    private class MyActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.menu_actionmode_selectweight, menu);
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
                    presenter.startToAddOrUpdateTemplate();
                    return true;
                case R.id.item_select_all:
                    memoryListPresenter.selectAll();
                    return true;
                case R.id.item_delete:
                    final int count = memoryListPresenter.getSelectedTemplates().size();
                    int msgResId = count==1 ? R.string.asw_dialog_msg_delete_one : R.string.asw_dialog_msg_delete_many;
                    DialogUtils.showDeleteDialog(SelectWeightActivity.this, msgResId, (d, w) -> presenter.deleteTemplates());
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            memoryListPresenter.setStatus(MemoryListStatus.NORMAL);
            mActionMode = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (memoryListPresenter.getStatus() == MemoryListStatus.HISTORY) {
            memoryListPresenter.setStatus(MemoryListStatus.NORMAL);
        } else {
            super.onBackPressed();
        }
    }
}
