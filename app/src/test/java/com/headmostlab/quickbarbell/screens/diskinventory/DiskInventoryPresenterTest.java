package com.headmostlab.quickbarbell.screens.diskinventory;

import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.database.dao.BunchDao;
import com.headmostlab.quickbarbell.model.database.dao.DiskDao;
import com.headmostlab.quickbarbell.model.database.entities.Disk;
import com.headmostlab.quickbarbell.testutils.TestUtils;
import com.headmostlab.quickbarbell.testutils.XmlUtils;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.WeightsListContract;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.model.Card;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DiskInventoryPresenterTest {


    @Mock
    private DiskDao diskDao;

    @Mock
    private BunchDao bunchDao;

    @Mock
    private WeightsListContract.Presenter weightsListPresenter;

    @Mock
    private LiveData<List<Disk>> disksLiveData;

    @Mock
    private DiskInventoryContract.View view;

    @Captor
    private ArgumentCaptor<Observer<List<Disk>>> disksObserverCaptor;

    @Captor
    private ArgumentCaptor<List<Card>> diskCardsCaptor;

    @Captor
    private ArgumentCaptor<List<Disk>> disksCaptor;

    private DiskInventoryContract.Presenter presenter;

    private List<Disk> disks;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        disks = TestUtils.genDisks(MeasurementUnit.KILOGRAM, 10f, 5.0f, 2.5f);

        when(diskDao.getAllLive()).thenReturn(disksLiveData);

        presenter = new DiskInventoryPresenter(diskDao, bunchDao, XmlUtils.getXmlProvider(), XmlUtils.getXmlParser(), weightsListPresenter);
    }

    @Test
    public void takeView() {
        presenter.takeView(view);
        verify(view).shoWeight(any());
        verify(view).showCount(anyInt());
        verify(disksLiveData).observe(any(), disksObserverCaptor.capture());
        disksObserverCaptor.getValue().onChanged(disks);
        verify(weightsListPresenter).setCards(any());
    }

    @Test
    public void restoreState() {
        presenter.setWeight(BigDecimal.valueOf(50));
        presenter.setCount(10);
        presenter.restoreState(presenter.serialize());
        presenter.takeView(view);
        verify(view).shoWeight(BigDecimal.valueOf(50));
        verify(view).showCount(10);
    }

    @Test
    public void testDeleteSelectedDisks() {
        presenter.takeView(view);
        verify(disksLiveData).observe(any(), disksObserverCaptor.capture());
        disksObserverCaptor.getValue().onChanged(disks);
        verify(weightsListPresenter).setCards(diskCardsCaptor.capture());
        final List<Card> diskCards = diskCardsCaptor.getValue();
        when(weightsListPresenter.getSelectedCards()).thenReturn(diskCards);
        presenter.deleteSelectedDisks();
        verify(diskDao).hide(disks);
    }

    @Test
    public void createDisks() {
        presenter.setWeight(BigDecimal.valueOf(50));
        presenter.setCount(6);
        presenter.createDisks();
        verify(diskDao).insertAll(disksCaptor.capture());
        final List<Disk> disks = disksCaptor.getValue();
        assertThat(disks.size()).isEqualTo(6);
        assertThat(disks.get(0).getWeight()).isEqualTo(BigDecimal.valueOf(50));
    }
}