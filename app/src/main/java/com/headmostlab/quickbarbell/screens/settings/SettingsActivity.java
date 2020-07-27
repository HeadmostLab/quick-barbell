package com.headmostlab.quickbarbell.screens.settings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.headmostlab.quickbarbell.App;
import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.model.Themes;
import com.headmostlab.quickbarbell.utils.ResourcesUtils;
import com.headmostlab.quickbarbell.views.HLDottedScrollBarView;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.CarouselView;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards.ActivityCard;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.scenes.SimpleCardScene;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity implements SettingsContract.View {

    @BindView(R.id.cardDescription)
    TextView mCardDescTextView;

    @BindView(R.id.carouselView)
    CarouselView<ActivityCard> mCarouselView;

    @BindView(R.id.dottedScrollBar)
    HLDottedScrollBarView mScrollBar;

    private List<ActivityCard> cards;

    private SettingsContract.Presenter presenter;

    private Themes curTheme;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        curTheme = App.getInstance().getSettingsModel().getTheme();

        setTheme(curTheme.themeId);

        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        presenter = ViewModelProviders.of(this, getPresenterFactory()).get(SettingsPresenter.class);

        setUpWidgets();

        restore(state);

        presenter.takeView(this);
    }

    private ViewModelProvider.Factory getPresenterFactory() {
        return App.getInstance().getAppComponent().settingsPresenterFactory().create().presenterFactory();
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

    private void setUpWidgets() {
        setUpCarouselView();
        setUpScrollBar();
    }

    private void setUpCarouselView() {
        final SimpleCardScene<ActivityCard> cardScene = new SimpleCardScene<>(this);
        cardScene.setColorFilter(ResourcesUtils.getColor(R.attr.colorPrimaryVariant, getTheme()));
        mCarouselView.setCardScene(cardScene);
        mCarouselView.setOnClickListener(view -> presenter.clickOnCard());
        mCarouselView.setOnCardChangedListener(card -> presenter.selectCardByCarouselView(card));
    }

    private void setUpScrollBar() {
        mScrollBar.setOnCurrentItemChangedListener(itemNum -> presenter.selectCardByScrollBar(cards.get(itemNum - 1)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!curTheme.equals(App.getInstance().getSettingsModel().getTheme())) {
            recreate();
        }
    }

    @Override
    public void showCards(List<ActivityCard> cards) {
        this.cards = cards;
        mScrollBar.setCount(cards.size());
        mCarouselView.setCards(cards);
    }

    @Override
    public void showScreen(ActivityCard card) {
        startActivity(new Intent(SettingsActivity.this, card.getActivityClass()));
    }

    @Override
    public void showTitle(String title) {
        mCardDescTextView.setText(title);
    }

    @Override
    public void scrollScrollBarTo(ActivityCard card) {
        mScrollBar.setCurrentItem(cards.indexOf(card)+1);
    }

    @Override
    public void scrollCarouselViewTo(ActivityCard card) {
        mCarouselView.setCurCard(cards.indexOf(card)+1);
    }
}