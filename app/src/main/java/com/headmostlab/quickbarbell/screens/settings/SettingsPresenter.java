package com.headmostlab.quickbarbell.screens.settings;

import com.headmostlab.quickbarbell.utils.Xml;
import com.headmostlab.quickbarbell.utils.XmlParser;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards.ActivityCard;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.xml.transform.TransformerException;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

public class SettingsPresenter extends ViewModel implements SettingsContract.Presenter {

    private WeakReference<SettingsContract.View> view;
    private List<ActivityCard> cards;
    private ActivityCardsProvider cardsProvider;
    private ActivityCard chosenCard;
    private XmlParser xmlParser;
    private Provider<Xml> xmlProvider;
    private ActivityCard restoredCard;

    @Inject
    public SettingsPresenter(Provider<Xml> xmlProvider, XmlParser xmlParser, ActivityCardsProvider cardsProvider) {
        view = new WeakReference<>(null);
        this.cardsProvider = cardsProvider;
        this.xmlProvider = xmlProvider;
        this.xmlParser = xmlParser;
        loadCards();
    }

    @Override
    public void takeView(@NonNull SettingsContract.View view) {
        this.view = new WeakReference<>(view);

        showCards();
        restorePosition();
        showFirstCardTitle();
    }

    private void showFirstCardTitle() {
        view().showTitle(cards.get(0).getDescription());
    }

    private void restorePosition() {
        if (restoredCard != null) {
            chosenCard = restoredCard;
            restoredCard = null;
            if (isView()) {
                view().scrollCarouselViewTo(chosenCard);
                view().scrollScrollBarTo(chosenCard);
                view().showTitle(chosenCard.getDescription());
            }
        }
    }

    private void loadCards() {
        this.cards = cardsProvider.getCards();
        chosenCard = cards.get(0);
    }

    private void showCards() {
        if(isView()) {
            view().showCards(cards);
        }
    }

    @Override
    public void restoreState(String xml) {
        try {
            Xml x = xmlParser.parse(xml);
            final String className = x.getString("class");
            if (className != null) {
                restoredCard = cardsProvider.find(className);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String serialize() {
        Xml xml = xmlProvider.get();
        xml.putString("class", chosenCard.getActivityClass().getCanonicalName());
        try {
            return xml.getString();
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clickOnCard() {
        if (isView()) {
            view().showScreen(chosenCard);
        }
    }

    @Override
    public void selectCardByCarouselView(ActivityCard card) {
        chosenCard = card;
        if (isView()) {
            view().showTitle(card.getDescription());
            view().scrollScrollBarTo(card);
        }
    }

    @Override
    public void selectCardByScrollBar(ActivityCard card) {
        chosenCard = card;
        if (isView()) {
            view().showTitle(card.getDescription());
            view().scrollCarouselViewTo(card);
        }
    }

    @Override
    public SettingsContract.View view() {
        return view.get();
    }

    @Override
    public boolean isView() {
        return view() != null;
    }
}