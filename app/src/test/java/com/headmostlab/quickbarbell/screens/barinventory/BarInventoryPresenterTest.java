package com.headmostlab.quickbarbell.screens.barinventory;

import com.headmostlab.quickbarbell.business.BarTypes;
import com.headmostlab.quickbarbell.model.database.dao.BarDao;
import com.headmostlab.quickbarbell.model.database.entities.Bar;
import com.headmostlab.quickbarbell.testutils.TestUtils;
import com.headmostlab.quickbarbell.testutils.XmlUtils;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards.InventoryBarCard;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.WeightsListContract;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.model.Card;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BarInventoryPresenterTest {

    @Mock
    private BarDao barDao;

    @Mock
    private CarouselCardsProvider carouselCardsProvider;

    @Mock
    private WeightsListContract.Presenter weightsListPresenter;

    @Mock
    private BarInventoryContract.View view;

    @Mock
    private LifecycleOwner lifecycleOwner;

    @Mock
    private LiveData<List<Bar>> barsLiveData;

    @Captor
    private ArgumentCaptor<Observer<List<Bar>>> barsObserverCaptor;

    @Captor
    private ArgumentCaptor<List<Card>> cardsCaptor;

    @Captor
    private ArgumentCaptor<Bar> barCaptor;

    private BarInventoryContract.Presenter presenter;

    private List<InventoryBarCard> carouselCards;
    private InventoryBarCard selectedCard;
    private List<Bar> bars;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        carouselCards = createCarouselCards();
        selectedCard = carouselCards.get(0);
        bars = TestUtils.generateBars(3);

        when(carouselCardsProvider.getCards()).thenReturn(carouselCards);
        when(view.getLifeCycleOwner()).thenReturn(lifecycleOwner);
        when(barDao.getAll()).thenReturn(barsLiveData);
        when(barsLiveData.getValue()).thenReturn(bars);

        createPresenter();
    }

    private void createPresenter() {
        presenter = new BarInventoryPresenter(barDao, XmlUtils.getXmlProvider(), XmlUtils.getXmlParser(),
                carouselCardsProvider, weightsListPresenter);
    }

    @Test
    public void takeView() {
        presenter.takeView(view);
        verify(view).showCarouselCards(carouselCards);
        verify(view).scrollCarouselViewTo(selectedCard);
        verify(view).scrollScrollBarTo(selectedCard);
        verify(view).shoWeight(any());

        verify(barsLiveData).observe(any(), barsObserverCaptor.capture());
        barsObserverCaptor.getValue().onChanged(bars);
        verify(weightsListPresenter).setCards(any());
    }

    @Test
    public void restoreState() {
        presenter.takeView(view);
        presenter.setWeight(BigDecimal.valueOf(100));
        selectedCard = carouselCards.get(1);
        presenter.selectCardByCarouselView(selectedCard);

        when(carouselCardsProvider.find(BarTypes.CURLY)).thenReturn(carouselCards.get(1));

        presenter.restoreState(presenter.serialize());
        presenter.takeView(view);

        verify(view, times(1)).shoWeight(BigDecimal.valueOf(100));
        verify(view, times(2)).scrollScrollBarTo(selectedCard);
        verify(view, times(1)).scrollCarouselViewTo(selectedCard);
    }
    @Test
    public void restoreStateAfterLowMemory() {
        presenter.takeView(view);
        presenter.setWeight(BigDecimal.valueOf(100));
        selectedCard = carouselCards.get(1);
        presenter.selectCardByCarouselView(selectedCard);
        when(carouselCardsProvider.find(BarTypes.CURLY)).thenReturn(carouselCards.get(1));
        final String serialize = presenter.serialize();
        createPresenter();
        presenter.restoreState(serialize);
        presenter.takeView(view);
        verify(view, times(1)).shoWeight(BigDecimal.valueOf(100));
        verify(view, times(2)).scrollScrollBarTo(selectedCard);
        verify(view, times(1)).scrollCarouselViewTo(selectedCard);
    }

    @Test
    public void testDeleteSelectedBars() {
        presenter.takeView(view);
        verify(barsLiveData).observe(any(), barsObserverCaptor.capture());
        barsObserverCaptor.getValue().onChanged(bars);
        verify(weightsListPresenter).setCards(cardsCaptor.capture());
        List<Card> cards = cardsCaptor.getValue();
        when(weightsListPresenter.getSelectedCards()).thenReturn(cards);
        presenter.onSelectionCardsChanged();
        presenter.deleteSelectedBars();
        verify(barDao).deleteAll(bars);
    }

    @Test
    public void createBar() {
        presenter.takeView(view);
        presenter.setWeight(BigDecimal.valueOf(100));
        presenter.createBar();
        verify(barDao).insert(barCaptor.capture());
        final Bar newBar = barCaptor.getValue();
        assertThat(newBar.getBarType()).isEqualByComparingTo(selectedCard.getBarType());
        assertThat(newBar.getWeight()).isEqualByComparingTo(BigDecimal.valueOf(100));
    }

    private List<InventoryBarCard> createCarouselCards() {
        return Arrays.asList(new InventoryBarCard(BarTypes.STRAIGHT, "icon"),
                new InventoryBarCard(BarTypes.CURLY, "icon"),
                new InventoryBarCard(BarTypes.DUMBBELL, "icon"));
    }


}