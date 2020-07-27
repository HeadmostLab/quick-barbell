package com.headmostlab.quickbarbell.screens.settings;

import com.headmostlab.quickbarbell.testutils.XmlUtils;
import com.headmostlab.quickbarbell.screens.barinventory.BarInventoryActivity;
import com.headmostlab.quickbarbell.screens.diskinventory.DiskInventoryActivity;
import com.headmostlab.quickbarbell.screens.weightcalculation.WeightCalculationActivity;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards.ActivityCard;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SettingsPresenterTest {

    @Mock
    private SettingsContract.View view;

    @Mock
    private ActivityCardsProvider cardsProvider;

    private SettingsContract.Presenter presenter;

    private List<ActivityCard> cards;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        cards = generateCards();
        when(cardsProvider.getCards()).thenReturn(cards);
        presenter = new SettingsPresenter(XmlUtils.getXmlProvider(), XmlUtils.getXmlParser(), cardsProvider);
    }

    @Test
    public void takeView() {
        presenter.takeView(view);
        verify(view, times(1)).showCards(cards);
    }

    @Test
    public void restorePosition() {
        presenter.takeView(view);
        presenter.selectCardByScrollBar(cards.get(2));
        final String serialize = presenter.serialize();
        presenter = new SettingsPresenter(XmlUtils.getXmlProvider(), XmlUtils.getXmlParser(), cardsProvider);
        when(cardsProvider.find(WeightCalculationActivity.class.getCanonicalName())).thenReturn(cards.get(2));
        presenter.restoreState(serialize);
        presenter.takeView(view);
        verify(view, times(1)).scrollScrollBarTo(cards.get(2));
    }

    private List<ActivityCard> generateCards() {
        List<ActivityCard> cards = new ArrayList<>();
        cards.add(new ActivityCard("icon", BarInventoryActivity.class, "BarInventoryActivity"));
        cards.add(new ActivityCard("icon", DiskInventoryActivity.class, "DiskInventoryActivity"));
        cards.add(new ActivityCard("icon", WeightCalculationActivity.class, "WeightCalculationActivity"));
        return cards;
    }
}