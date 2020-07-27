package com.headmostlab.quickbarbell.screens.showweights;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.headmostlab.quickbarbell.App;
import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.business.assembledbar.AssembledBar;
import com.headmostlab.quickbarbell.model.SettingsModel;
import com.headmostlab.quickbarbell.utils.AppBarLayoutUtils;
import com.headmostlab.quickbarbell.views.opengl.barpreview.AssembledBarPreviewView;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.WeightsListView;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowWeightsActivity extends AppCompatActivity implements ShowWeightsContract.View {

    @BindView(R.id.infoContainer)
    View infoContainer;

    @BindView(R.id.barName)
    TextView barNameTextView;

    @BindView(R.id.subTitle)
    TextView targetWeightTextView;

    @BindView(R.id.assembledBarPreviewView)
    AssembledBarPreviewView mAssembledBarView;

    @BindView(R.id.recyclerView)
    WeightsListView recycleView;

    @BindView(R.id.mainAppbar)
    AppBarLayout mainAppbar;

    @Inject
    SettingsModel settingsModel;

    private ShowWeightsPresenter presenter;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        App.getInstance().getAppComponent().inject(this);
        setTheme(settingsModel.getTheme().themeId);

        setContentView(R.layout.activity_show_weights);
        ButterKnife.bind(this);

        presenter = ViewModelProviders.of(this, getPresenterFactory()).get(ShowWeightsPresenter.class);

        setUpWidgets();

        restore(state);

        presenter.weightsListPresenter().takeView(recycleView);
        presenter.takeView(this);
    }

    private ViewModelProvider.Factory getPresenterFactory() {
        return App.getInstance().getAppComponent().showWeightsPresenterFactory().create().presenterFactory();
    }

    private void restore(@Nullable Bundle state) {
        if (state != null) {
            final String presenter = state.getString("presenter");
            this.presenter.restoreState(presenter);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle state) {
        super.onSaveInstanceState(state);

        final String serialize = presenter.serialize();
        state.putString("presenter",  serialize);
    }

    private void setUpWidgets() {
        setUpAppBar();
        setUpInfoContainer();
    }

    private void setUpAppBar() {
        int windowH = getResources().getDisplayMetrics().heightPixels;
        int windowW = getResources().getDisplayMetrics().widthPixels;
        int appBarH = (int)Math.min(windowH*0.5f, windowW);
        mainAppbar.setLayoutParams(new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, appBarH));
        AppBarLayoutUtils.enableAppBarDragging(mainAppbar, false);
    }

    private void setUpInfoContainer() {
        infoContainer.animate().alpha(0.1f).setStartDelay(3000).setDuration(500);
        infoContainer.setOnClickListener(v -> {
            infoContainer.setAlpha(1);
            infoContainer.animate().alpha(0.1f).setStartDelay(6000).setDuration(500);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAssembledBarView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAssembledBarView.onPause();
    }

    @Override
    public void showTitle(String title, String subTitle) {
        barNameTextView.setText(getString(R.string.asw_title, title));
        targetWeightTextView.setText(getString(R.string.asw_sub_title, subTitle));
    }

    @Override
    public void showAssembledBar(AssembledBar bar) {
        mAssembledBarView.setAssembledBar(bar);
    }

    @Override
    public LifecycleOwner getLifeCycleOwner() {
        return this;
    }
}