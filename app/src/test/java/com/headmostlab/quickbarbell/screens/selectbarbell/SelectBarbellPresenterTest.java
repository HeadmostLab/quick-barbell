package com.headmostlab.quickbarbell.screens.selectbarbell;

import com.headmostlab.quickbarbell.business.TransitData;
import com.headmostlab.quickbarbell.model.SettingsModel;
import com.headmostlab.quickbarbell.model.database.dao.BarDao;
import com.headmostlab.quickbarbell.model.database.entities.Bar;
import com.headmostlab.quickbarbell.testutils.TestUtils;
import com.headmostlab.quickbarbell.testutils.XmlUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SelectBarbellPresenterTest {

    @Mock
    private SelectBarbellContract.View view;

    @Mock
    private BarDao barDao;

    @Mock
    private SettingsModel settingsModel;

    @Mock
    private TransitData transitData;

    @Mock
    private LiveData<List<Bar>> barsLiveData;

    @Captor
    private ArgumentCaptor<Observer<List<Bar>>> observerArgumentCaptor;

    private SelectBarbellContract.Presenter presenter;

    private List<Bar> bars1 = TestUtils.generateBars(3);

    @Before
    public void setUpPresenter() throws Exception {
        MockitoAnnotations.initMocks(this);

        presenter = new SelectBarbellPresenter(transitData, barDao, settingsModel, XmlUtils.getXmlProvider(), XmlUtils.getXmlParser());
    }

    @Test
    public void takeView_DoNotInvokeShowCardsOnTheSameBars() {
        when(barDao.getAll()).thenReturn(barsLiveData);
        presenter.takeView(view);
        verify(barsLiveData, times(1)).observe(any(), observerArgumentCaptor.capture());
        observerArgumentCaptor.getValue().onChanged(bars1);
        verify(view, times(1)).showCards(anyList());
    }

}