package com.headmostlab.quickbarbell.screens.selectbarbell;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.headmostlab.quickbarbell.App;
import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.business.billing.BillingRepository;
import com.headmostlab.quickbarbell.model.SettingsModel;
import com.headmostlab.quickbarbell.model.Themes;
import com.headmostlab.quickbarbell.model.database.entities.Bar;
import com.headmostlab.quickbarbell.screens.others.HelpActivity;
import com.headmostlab.quickbarbell.screens.selectweight.SelectWeightActivity;
import com.headmostlab.quickbarbell.screens.settings.SettingsActivity;
import com.headmostlab.quickbarbell.views.HLDottedScrollBarView;
import com.headmostlab.quickbarbell.views.dialog.ShopDialog2;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.CarouselView;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards.SelectBarCard;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.scenes.SelectBarCardScene;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static dagger.internal.Preconditions.checkNotNull;

public class SelectBarbellFragment extends Fragment implements SelectBarbellContract.View {

    @BindView(R.id.carouselView)
    CarouselView<SelectBarCard> mCarouselView;

    @BindView(R.id.dottedScrollBar)
    HLDottedScrollBarView mScrollBar;

    @BindView(R.id.helpButton)
    View helpButton;

    @BindView(R.id.shopButton)
    FloatingActionButton shopButton;

    @BindView(R.id.settingsButton)
    View settingsButton;

    @BindView(R.id.scrim)
    View scrim;

    @Inject
    BillingRepository billingRepository;

    @Inject
    SettingsModel settingsModel;

    ShopDialog2 shopDialog;

    Themes curTheme;

    private OnBackPressedCallback backPressListener;

    private List<SelectBarCard> cards = new ArrayList<>();

    private SelectBarbellContract.Presenter presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.getInstance().getAppComponent().inject(this);

        curTheme = settingsModel.getTheme();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.cloneInContext(getThemedContext()).inflate(R.layout.fragment_select_barbell, container, false);
    }

    private Context getThemedContext() {
        return new ContextThemeWrapper(getActivity(), settingsModel.getTheme().themeId);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        super.onCreate(state);
        ButterKnife.bind(this, view);

        presenter = ViewModelProviders.of(this, getPresenterFactory()).get(SelectBarbellPresenter.class);

        setUpViews();
        setupOnBackPressed();

        presenter.takeView(this);

        restore(state != null ? state : getArguments()); // вызывать после takeView !!!

        getLifecycle().addObserver(presenter);
    }

    private void setupOnBackPressed() {
        backPressListener = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (shopButton.isExpanded()) {
                    shopButton.setExpanded(false);
                } else {
                    setEnabled(false);
                    getActivity().onBackPressed();
                }
            }
        };

        getActivity().getOnBackPressedDispatcher().addCallback(backPressListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        backPressListener.setEnabled(false);
    }

    private void restore(@Nullable Bundle state) {
        if (state != null) {
            presenter.restoreState(state.getString("presenter"));
        }
    }

    private ViewModelProvider.Factory getPresenterFactory() {
        return App.getInstance().getAppComponent().selectBarbellPresenterFactory().create().presenterFactory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle state) {
        super.onSaveInstanceState(state);

        state.putString("presenter", presenter.serialize());
    }

    private void setUpViews() {
        setUpCarouselView();
        setUpScrollBar();
        setUpShopButton();
        setUpShopDialog();
    }

    private void setUpCarouselView() {
        SelectBarCardScene scene = new SelectBarCardScene(getThemedContext());
        mCarouselView.setCardScene(scene);
        mCarouselView.setOnCardChangedListener(card -> presenter.selectCardByCarouselView(card));
        mCarouselView.setOnClickListener(view -> presenter.clickBarCard());
    }

    private void setUpScrollBar() {
        mScrollBar.setOnCurrentItemChangedListener(itemNum -> presenter.selectCardByScrollBar(cards.get(itemNum - 1)));
    }

    private void setUpShopButton() {
        scrim.setOnClickListener(v -> shopButton.setExpanded(false));
    }

    private void setUpShopDialog() {
        shopDialog = new ShopDialog2((AppCompatActivity) getActivity(), billingRepository);
    }

    @Override
    public void showCards(@NonNull List<SelectBarCard> cards) {
        checkNotNull(cards, "cards cannot be null!");
        this.cards.clear();
        this.cards.addAll(cards);
        mScrollBar.setCount(this.cards.size());
        mCarouselView.setCards(this.cards);
    }

    @Override
    public void hideHelpIcon(boolean isHidden) {
        helpButton.setVisibility(isHidden ? View.GONE : View.VISIBLE);
    }

    @Override
    public void showShopButton(boolean isShow) {
//        shopButton.setVisibility(View.VISIBLE); // TO DO: 16.05.2020 удалить
        shopButton.setVisibility(isShow ? View.VISIBLE : View.GONE); // TO DO: 16.05.2020 раскоментировать
    }

    @Override
    public void onPause() {
        super.onPause();
        mCarouselView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCarouselView.onResume();

        updateTheme();
    }

    private void updateTheme() {

        final Themes theme = settingsModel.getTheme();

        if (! curTheme.equals(theme)) {

            curTheme = theme;

            final Bundle state = new Bundle();
            state.putString("presenter", presenter.serialize());
            this.setArguments(state);

            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    @Override
    public void showSelectWeightScreen() {
        startActivity(new Intent(getContext(), SelectWeightActivity.class));
    }

    @OnClick(R.id.settingsButton)
    public void openSettings(View view) {
        showSettingsScreen();
    }

    @OnClick(R.id.helpButton)
    public void openHelp(View view) {
        showHelpScreen();
    }

    @OnClick(R.id.shopButton)
    public void openShop(View view) {
        showShopScreen();
    }

    private void showShopScreen() {
        shopButton.setExpanded(true);
    }

    @Override
    public void showSettingsScreen() {
        startActivity(new Intent(getContext(), SettingsActivity.class));
    }

    @Override
    public void showHelpScreen() {
        startActivity(new Intent(getContext(), HelpActivity.class));
    }

    @Override
    public LifecycleOwner getLifeCycleOwner() {
        return this;
    }

    @Override
    public void goToBar(Bar bar) {
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getBar().getId() == bar.getId()) {
                mCarouselView.setCurCard(i + 1);
                mScrollBar.setCurrentItem(i + 1);
                break;
            }
        }
    }

    @Override
    public void scrollScrollBarTo(SelectBarCard card) {
        mScrollBar.setCurrentItem(cards.indexOf(card) + 1);
    }

    @Override
    public void scrollCarouselViewTo(SelectBarCard card) {
        mCarouselView.setCurCard(cards.indexOf(card) + 1);
    }

}
