package com.headmostlab.quickbarbell.screens.showweights;

import com.headmostlab.quickbarbell.business.TransitData;
import com.headmostlab.quickbarbell.business.assembledbar.AssembledBar;
import com.headmostlab.quickbarbell.business.BarTypes;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.SettingsModel;
import com.headmostlab.quickbarbell.model.database.dao.BarDao;
import com.headmostlab.quickbarbell.model.database.dao.DiskDao;
import com.headmostlab.quickbarbell.model.database.entities.Bar;
import com.headmostlab.quickbarbell.testutils.TestUtils;
import com.headmostlab.quickbarbell.testutils.XmlUtils;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.WeightsListContract;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.model.BarCard;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.model.Card;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.model.DiskCard;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShowWeightsPresenterTest2 {
    @Mock
    private ShowWeightsContract.View view;

    @Mock
    private SettingsModel settingsModel;

    @Mock
    private BarDao barDao;

    @Mock
    private DiskDao diskDao;

    @Mock
    private TransitData transitData;

    @Mock
    private WeightsListContract.Presenter weightsListPresenter;

    @Captor
    ArgumentCaptor<List<Card>> cardsCaptor;

    private ShowWeightsContract.Presenter presenter;

    private AssembledBar straightAssembledBar;
    private AssembledBar dDumbbellAssembledBar;

    private List<Card> straightBarCards = new ArrayList<>();
    private List<Card> dDumbbellBarCards = new ArrayList<>();

    private MeasurementUnit systemUnit;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        systemUnit = MeasurementUnit.KILOGRAM;

        straightAssembledBar = new AssembledBar(
                new Bar(BarTypes.STRAIGHT, BigDecimal.TEN, MeasurementUnit.KILOGRAM),
                TestUtils.genDisks(MeasurementUnit.KILOGRAM, 1.25f, 5f, 10f),
                TestUtils.genDisks(MeasurementUnit.KILOGRAM, 1.25f, 5f, 10f));

        dDumbbellAssembledBar = new AssembledBar(
                new Bar(BarTypes.DOUBLE_DUMBBELL, BigDecimal.TEN, MeasurementUnit.KILOGRAM),
                TestUtils.genDisks(MeasurementUnit.KILOGRAM, 2.5f, 5f),
                TestUtils.genDisks(MeasurementUnit.KILOGRAM, 2.5f, 2.5f, 2.5f));

        straightBarCards.addAll(Arrays.asList(
                new BarCard(straightAssembledBar.getBar()),
                new DiskCard(BigDecimal.valueOf(10), 2, MeasurementUnit.KILOGRAM),
                new DiskCard(BigDecimal.valueOf(5), 2, MeasurementUnit.KILOGRAM),
                new DiskCard(BigDecimal.valueOf(1.25), 2, MeasurementUnit.KILOGRAM))
        );

        dDumbbellBarCards.addAll(Arrays.asList(
                new BarCard(dDumbbellAssembledBar.getBar()),
                new DiskCard(BigDecimal.valueOf(5), 1, MeasurementUnit.KILOGRAM),
                new DiskCard(BigDecimal.valueOf(2.5), 3, MeasurementUnit.KILOGRAM))
        );
    }

    @Test
    public void setStraightAssembledBar() {
        when(transitData.getAssembledBar()).thenReturn(straightAssembledBar);
        presenter = new ShowWeightsPresenter(transitData, systemUnit, weightsListPresenter,
                XmlUtils.getXmlProvider(), XmlUtils.getXmlParser(), barDao, diskDao);
        presenter.takeView(view);
        verify(weightsListPresenter).setCards(cardsCaptor.capture());
        final List<Card> cards = cardsCaptor.getValue();
        assertThat(cards.size()).isEqualTo(straightBarCards.size());
        assertThat(((BarCard)cards.get(0)).getBarType()).isEqualByComparingTo(BarTypes.STRAIGHT);
        assertThat(cards.get(1).getWeight()).isEqualTo(BigDecimal.valueOf(10.0));
        assertThat(cards.get(2).getWeight()).isEqualTo(BigDecimal.valueOf(5.0));
        assertThat(cards.get(3).getWeight()).isEqualTo(BigDecimal.valueOf(1.25f));
    }

    @Test
    public void setDDumbbellAssembledBar() {
        when(transitData.getAssembledBar()).thenReturn(dDumbbellAssembledBar);
        presenter = new ShowWeightsPresenter(transitData, systemUnit, weightsListPresenter,
                XmlUtils.getXmlProvider(), XmlUtils.getXmlParser(), barDao, diskDao);
        presenter.takeView(view);
        verify(weightsListPresenter).setCards(cardsCaptor.capture());
        final List<Card> cards = cardsCaptor.getValue();
        assertThat(cards.size()).isEqualTo(dDumbbellBarCards.size());
        assertThat(((BarCard)cards.get(0)).getBarType()).isEqualByComparingTo(BarTypes.DUMBBELL);
        assertThat(cards.get(1).getWeight()).isEqualTo(BigDecimal.valueOf(5.0f));
        assertThat(((DiskCard)cards.get(1)).getCount()).isEqualTo(1);
        assertThat(cards.get(2).getWeight()).isEqualTo(BigDecimal.valueOf(2.5f));
        assertThat(((DiskCard)cards.get(2)).getCount()).isEqualTo(4);
    }
}