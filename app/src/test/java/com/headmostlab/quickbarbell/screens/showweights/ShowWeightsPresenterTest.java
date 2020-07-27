package com.headmostlab.quickbarbell.screens.showweights;

import com.headmostlab.quickbarbell.business.TransitData;
import com.headmostlab.quickbarbell.business.assembledbar.AssembledBar;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.database.dao.BarDao;
import com.headmostlab.quickbarbell.model.database.dao.DiskDao;
import com.headmostlab.quickbarbell.model.database.entities.Bar;
import com.headmostlab.quickbarbell.testutils.TestUtils;
import com.headmostlab.quickbarbell.testutils.XmlUtils;
import com.headmostlab.quickbarbell.utils.DiskUtils;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.WeightsListContract;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShowWeightsPresenterTest {
    @Mock
    private ShowWeightsContract.View view;

    @Mock
    private BarDao barDao;

    @Mock
    private DiskDao diskDao;

    @Mock
    private TransitData transitData;

    @Captor
    private ArgumentCaptor<AssembledBar> assembledBarCaptor;

    @Mock
    private WeightsListContract.Presenter weightsListPresenter;

    private ShowWeightsContract.Presenter presenter;

    private AssembledBar assembledBar;

    private Bar bar;

    private MeasurementUnit systemUnit;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        assembledBar = TestUtils.generateAssembledBars(1).get(0);
        bar = assembledBar.getBar();

        when(transitData.getAssembledBar()).thenReturn(assembledBar);
        when(barDao.getById(anyInt())).thenReturn(bar);

        final long[] leftDiskIds = DiskUtils.getDiskIds(assembledBar.getLeftDisks());
        final long[] rightDiskIds = DiskUtils.getDiskIds(assembledBar.getRightDisks());

        when(diskDao.getByIds(leftDiskIds)).thenReturn(assembledBar.getLeftDisks());
        when(diskDao.getByIds(rightDiskIds)).thenReturn(assembledBar.getRightDisks());

        systemUnit = MeasurementUnit.KILOGRAM;

        presenter = new ShowWeightsPresenter(transitData, systemUnit, weightsListPresenter,
                XmlUtils.getXmlProvider(), XmlUtils.getXmlParser(), barDao, diskDao);
    }

    @Test
    public void testTakeView() {
        presenter.takeView(view);
        verify(view, times(1)).showTitle(any());
        verify(view, times(1)).showAssembledBar(any());
        verify(weightsListPresenter, times(1)).setCards(any());
    }

    @Test
    public void testRestore() {
        presenter.takeView(view);
        verify(view, times(1)).showAssembledBar(assembledBarCaptor.capture());
        AssembledBar startBar = assembledBarCaptor.getValue();
        final String serialized = presenter.serialize();
        presenter.restoreState(serialized);
        presenter.takeView(view);
        verify(view, times(2)).showAssembledBar(assembledBarCaptor.capture());
        AssembledBar restoredBar = assembledBarCaptor.getValue();
        assertThat(startBar.getWeightInKg()).isEqualTo(restoredBar.getWeightInKg());
    }

    @Test
    public void testRestoreAfterLowMemory() {
        presenter.takeView(view);
        verify(view, times(1)).showAssembledBar(assembledBarCaptor.capture());
        AssembledBar startBar = assembledBarCaptor.getValue();
        final String serialized = presenter.serialize();
        when(transitData.getAssembledBar()).thenReturn(null);
        presenter = new ShowWeightsPresenter(transitData, systemUnit, weightsListPresenter,
                XmlUtils.getXmlProvider(), XmlUtils.getXmlParser(), barDao, diskDao);
        presenter.restoreState(serialized);
        presenter.takeView(view);
        verify(view, times(2)).showAssembledBar(assembledBarCaptor.capture());
        AssembledBar restoredBar = assembledBarCaptor.getValue();
        assertThat(startBar.getWeightInKg()).isEqualTo(restoredBar.getWeightInKg());
    }

}