package com.headmostlab.quickbarbell.screens.selectassembledbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.headmostlab.quickbarbell.App;
import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.model.SettingsModel;
import com.headmostlab.quickbarbell.screens.showweights.ShowWeightsActivity;
import com.headmostlab.quickbarbell.views.HLDottedScrollBarView;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.CarouselView;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards.AssembledBarCard;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.scenes.assembledbarscenes.AssembledBarCardScene;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.scenes.assembledbarscenes.BarSceneFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectAssembledBarActivity extends AppCompatActivity implements SelectAssembledBarContract.View {

    @BindView(R.id.carouselView)
    CarouselView<AssembledBarCard> mCarousel;

    @BindView(R.id.dottedScrollBar)
    HLDottedScrollBarView mScrollBar;

    @BindView(R.id.barName)
    TextView barName;

    @Inject
    SettingsModel settingsModel;

    private SelectAssembledBarContract.Presenter presenter;

    private List<AssembledBarCard> cards = new ArrayList<>();

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        App.getInstance().getAppComponent().inject(this);
        setTheme(settingsModel.getTheme().themeId);

        setContentView(R.layout.activity_select_assembled_bar);
        ButterKnife.bind(this);

        presenter = ViewModelProviders.of(this, getPresenterFactory()).get(SelectAssembledBarPresenter.class);

        restore(state);

        setUpViews();

        presenter.takeView(this);
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

    private ViewModelProvider.Factory getPresenterFactory() {
        return App.getInstance().getAppComponent().selectAssembledBarPresenterFactory().create().presenterFactory();
    }

    private void setUpViews() {
        setUpCarouselView();
        setUpScrollBar();
    }

    private void setUpCarouselView() {
        AssembledBarCardScene scene = BarSceneFactory.create(presenter.getBarType(), this);
        scene.setMeasurement(presenter.getMeasurement());

        mCarousel.setCardScene(scene);
        mCarousel.setOnCardChangedListener(card -> presenter.selectCardByCarouselView(card));
        mCarousel.setOnClickListener(view -> presenter.clickCurCard());
    }

    private void setUpScrollBar() {
        mScrollBar.setOnCurrentItemChangedListener(itemNum -> presenter.selectCardByScrollBar(cards.get(itemNum-1)));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCarousel.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCarousel.onResume();
    }

    @Override
    public void showTitle(String title) {
        barName.setText(getString(R.string.asab_title, title));
    }

    @Override
    public void showShowWeightsScreen() {
        startActivity(new Intent(this, ShowWeightsActivity.class));
    }

    @Override
    public void scrollScrollBarTo(AssembledBarCard card) {
        final int position = cards.indexOf(card);
        mScrollBar.setCurrentItem(position+1);
    }

    @Override
    public void scrollCarouselViewTo(AssembledBarCard card) {
        final int position = cards.indexOf(card);
        mCarousel.setCurCard(position+1);
    }

    @Override
    public void showCards(List<AssembledBarCard> cards) {
        this.cards.clear();
        this.cards.addAll(cards);
        mCarousel.setCards(cards);
        mScrollBar.setCount(cards.size());
    }

}
