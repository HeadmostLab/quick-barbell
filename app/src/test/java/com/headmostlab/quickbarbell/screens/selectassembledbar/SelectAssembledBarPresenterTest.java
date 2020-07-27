package com.headmostlab.quickbarbell.screens.selectassembledbar;

import com.headmostlab.quickbarbell.business.TransitData;
import com.headmostlab.quickbarbell.business.assembledbar.AssembledBar;
import com.headmostlab.quickbarbell.business.assembledbar.AssembledBarsProvider;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.SettingsModel;
import com.headmostlab.quickbarbell.model.database.dao.BarDao;
import com.headmostlab.quickbarbell.model.database.entities.Bar;
import com.headmostlab.quickbarbell.testutils.TestUtils;
import com.headmostlab.quickbarbell.testutils.XmlUtils;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards.AssembledBarCard;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SelectAssembledBarPresenterTest {

    @Mock
    private SelectAssembledBarContract.View view;

    @Mock
    private SettingsModel settingsModel;

    @Mock
    private BarDao barDao;

    @Mock
    private TransitData transitData;

    @Mock
    private AssembledBarsProvider assembledBarsProvider;

    @Captor
    private ArgumentCaptor<List<AssembledBarCard>> cardsCaptor;

    private SelectAssembledBarPresenter presenter;

    private List<AssembledBar> assembledBars;

    private Bar bar;

    private MeasurementUnit systemUnit;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        assembledBars = TestUtils.generateAssembledBars(2);
        bar = assembledBars.get(0).getBar();

        when(transitData.getChosenBar()).thenReturn(bar);
        when(assembledBarsProvider.getBars()).thenReturn(assembledBars);
        when(transitData.getPreciseWeight()).thenReturn(BigDecimal.valueOf(100));
        when(transitData.getAssembledBar()).thenReturn(assembledBars.get(0));
        when(barDao.getById(anyInt())).thenReturn(bar);

        systemUnit = MeasurementUnit.KILOGRAM;

        presenter = new SelectAssembledBarPresenter(transitData, systemUnit, barDao,
                XmlUtils.getXmlProvider(), XmlUtils.getXmlParser(), assembledBarsProvider);
    }

    @Test
    public void testTakeViewForTheFirstTime() {
        presenter.takeView(view);
        verify(view, times(1)).showTitle(any());
        verify(view, times(1)).showCards(any());
        verify(transitData, times(1)).setAssembledBar(any());
    }

    @Test
    public void testTakeViewForTheSecondTime() {
        presenter.takeView(view);
        presenter.takeView(view);
        verify(view, times(2)).showTitle(any());
        verify(view, times(2)).showCards(any());
        verify(transitData, times(1)).setAssembledBar(any());
    }

    @Test
    public void testRestorePosition() {
        presenter.takeView(view);
        final String serialized = presenter.serialize();
        presenter.restoreState(serialized);
        presenter.takeView(view);
        presenter.takeView(view);
        verify(view, times(1)).scrollCarouselViewTo(any());
        verify(view, times(1)).scrollScrollBarTo(any());
        verify(transitData, times(2)).setAssembledBar(any());
    }

    @Test
    public void testSelectCardByCarouselView() {
        presenter.takeView(view);
        verify(view).showCards(cardsCaptor.capture());
        final AssembledBarCard card = cardsCaptor.getValue().get(0);
        presenter.selectCardByScrollBar(card);
        verify(view, times(1)).scrollCarouselViewTo(card);
    }

    @Test
    public void testSelectCardByScrollBar() {
        presenter.takeView(view);
        verify(view).showCards(cardsCaptor.capture());
        final AssembledBarCard card = cardsCaptor.getValue().get(0);
        presenter.selectCardByCarouselView(card);
        verify(view, times(1)).scrollScrollBarTo(card);
    }

    @Test
    public void testClickCurCard() {
        presenter.takeView(view);
        presenter.clickCurCard();
        verify(view, times(1)).showShowWeightsScreen();
    }

    @Test
    public void getBarType() {
        assertThat(presenter.getBarType()).isEqualByComparingTo(bar.getBarType());
    }
}