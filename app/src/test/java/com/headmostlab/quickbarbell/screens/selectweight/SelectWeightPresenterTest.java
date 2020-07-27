package com.headmostlab.quickbarbell.screens.selectweight;

import com.headmostlab.quickbarbell.business.BarTypes;
import com.headmostlab.quickbarbell.business.TransitData;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.SettingsModel;
import com.headmostlab.quickbarbell.model.database.dao.BarDao;
import com.headmostlab.quickbarbell.model.database.dao.WeightTemplateDao;
import com.headmostlab.quickbarbell.model.database.entities.Bar;
import com.headmostlab.quickbarbell.model.database.entities.WeightTemplate;
import com.headmostlab.quickbarbell.testutils.TemplateTestUtils;
import com.headmostlab.quickbarbell.testutils.TestUtils;
import com.headmostlab.quickbarbell.testutils.XmlUtils;
import com.headmostlab.quickbarbell.views.recyclerview.memoryview.MemoryListPresenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collections;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SelectWeightPresenterTest {

    @Mock
    private SelectWeightContract.View view;

    @Mock
    private WeightTemplateDao templateDao;

    @Mock
    private BarDao barDao;

    @Mock
    private TransitData transitData;

    @Mock
    private MemoryListPresenter memoryList;

    @Mock
    private LiveData<List<WeightTemplate>> templatesLiveData;

    @Mock
    private SettingsModel settingsModel;

    @Captor
    private ArgumentCaptor<Observer<List<WeightTemplate>>> observerArgumentCaptor;

    @Captor
    private ArgumentCaptor<Boolean> booleanCaptor;

    private SelectWeightPresenter presenter;

    private Bar selectedBar;

    private MeasurementUnit systemUnit;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(templateDao.getByBarId(anyLong())).thenReturn(templatesLiveData);
        selectedBar = TestUtils.generateBar(1, BigDecimal.valueOf(100), MeasurementUnit.KILOGRAM, BarTypes.CURLY);
        when(transitData.getChosenBar()).thenReturn(selectedBar);
        systemUnit = MeasurementUnit.KILOGRAM;
        presenter = new SelectWeightPresenter(transitData, barDao, templateDao, memoryList, XmlUtils.getXmlProvider(), XmlUtils.getXmlParser(), systemUnit);
        presenter.takeView(view);
    }

    @Test
    public void testStartObservingOneTemplate() {
        verify(templatesLiveData, times(1)).observe(any(), observerArgumentCaptor.capture());
        final List<WeightTemplate> templates = TemplateTestUtils.generate(1);
        observerArgumentCaptor.getValue().onChanged(templates);
        verify(memoryList, times(1)).setTemplates(templates);
        verify(view, times(1)).showWeight(templates.get(0).getWeight());
        verify(view, times(1)).showPercent((int) templates.get(0).getPercent());
    }

    @Test
    public void testStartObservingSomeTemplates() {
        verify(templatesLiveData, times(1)).observe(any(), observerArgumentCaptor.capture());
        final List<WeightTemplate> templates = TemplateTestUtils.generate(3);
        observerArgumentCaptor.getValue().onChanged(templates);
        verify(memoryList, times(1)).setTemplates(templates);
        verify(view, times(1)).showWeight(BigDecimal.ZERO);
        verify(view, times(1)).showPercent(100);
    }

    @Test
    public void testSetWeightTemplate() {
        final WeightTemplate template = TemplateTestUtils.generate();
        presenter.setWeightTemplate(template);
        verify(view, times(1)).showWeight(template.getWeight());
        verify(view, times(1)).showPercent((int) template.getPercent());
    }

    @Test
    public void testSetPercent_When_SelectedNoneTemplates() {
        when(memoryList.getSelectedTemplates()).thenReturn(emptyList());
        presenter.setPercent(50);
        verify(memoryList, never()).update(any());
    }

    @Test
    public void testSetPercent_When_SelectedOneTemplate() {
        int newPercent = 50, oldPercent = 80;
        final List<WeightTemplate> selectedTemplates = TemplateTestUtils.generate(1);
        final WeightTemplate template = selectedTemplates.get(0);
        template.setPercent(oldPercent);
        when(memoryList.getSelectedTemplates()).thenReturn(selectedTemplates);
        presenter.setPercent(newPercent);
        assertThat(template.getPercent()).isEqualTo(newPercent);
        verify(memoryList, times(1)).update(template);
    }

    @Test
    public void testSetWeight_When_SelectedNoneTemplates() {
        when(memoryList.getSelectedTemplates()).thenReturn(emptyList());
        presenter.setWeight(BigDecimal.valueOf(50));
        verify(memoryList, never()).update(any());
    }

    @Test
    public void testSetWeight_When_SelectedOneTemplate() {
        BigDecimal newWeight = BigDecimal.valueOf(50), oldWeight = BigDecimal.valueOf(80);
        final List<WeightTemplate> selectedTemplates = TemplateTestUtils.generate(1);
        final WeightTemplate template = selectedTemplates.get(0);
        template.setWeight(oldWeight);
        when(memoryList.getSelectedTemplates()).thenReturn(selectedTemplates);
        presenter.setWeight(newWeight);
        assertThat(template.getWeight()).isEqualTo(newWeight);
        verify(memoryList, times(1)).update(template);
    }

    @Test
    public void testSetTemplateComment_When_SelectedOneTemplate() {
        String comment = "test comment";
        WeightTemplate template = TemplateTestUtils.generate();
        when(memoryList.getSelectedTemplates()).thenReturn(Collections.singletonList(template));
        presenter.updateOrAddTemplate(comment);
        assertThat(template.getComment()).isEqualTo(comment);
        verify(memoryList, times(1)).update(template);
    }

    @Test
    public void testSetTemplateComment_When_SelectedNoneTemplates_CreateNewTemplate() {
        String comment = "test comment";
        BigDecimal weight = BigDecimal.valueOf(50);
        int percent = 100;
        when(memoryList.getSelectedTemplates()).thenReturn(Collections.emptyList());
        presenter.setWeight(weight);
        presenter.setPercent(percent);
        presenter.updateOrAddTemplate(comment);
        ArgumentCaptor<WeightTemplate> templateCaptor = ArgumentCaptor.forClass(WeightTemplate.class);
        verify(templateDao, times(1)).insert(templateCaptor.capture());
        final WeightTemplate newTemplate = templateCaptor.getValue();
        assertThat(newTemplate.getBarid()).isEqualTo(selectedBar.getId());
        assertThat(newTemplate.getWeight()).isEqualTo(weight);
        assertThat(newTemplate.getPercent()).isEqualTo(percent);
        assertThat(newTemplate.getComment()).isEqualTo(comment);
    }

    @Test
    public void testAddNewTemplate() {
        when(memoryList.getSelectedTemplates()).thenReturn(Collections.emptyList());
        presenter.startToAddOrUpdateTemplate();
        verify(view, times(1)).showTemplateCommentDialog(null);
    }

    @Test
    public void testUpdateTemplates() {
        final List<WeightTemplate> templates = TemplateTestUtils.generate(2);
        when(templatesLiveData.getValue()).thenReturn(templates);
        presenter.updateTemplates();
        verify(templateDao, times(1)).update(templates);
    }

    @Test
    public void testDeleteTemplates() {
        final List<WeightTemplate> templates = TemplateTestUtils.generate(2);
        when(memoryList.getSelectedTemplates()).thenReturn(templates);
        presenter.deleteTemplates();
        verify(templateDao, times(1)).delete(templates);
    }

    @Test
    public void testFindWeights() {
        BigDecimal weight = BigDecimal.valueOf(50);
        int percent = 100;
        BigDecimal preciseWeight = weight.multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100), MathContext.DECIMAL32);
        presenter.setWeight(weight);
        presenter.setPercent(percent);
        presenter.findWeights();
        ArgumentCaptor<BigDecimal> preciseWeightCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        verify(transitData, times(1)).setPreciseWeight(preciseWeightCaptor.capture());
        assertThat(preciseWeightCaptor.getValue()).isEqualTo(preciseWeight);
        verify(view, times(1)).showSelectAssembledBarScreen();
    }

    @Test
    public void testOnSelectionTemplatesChanged_SelectionStarted() {
        final List<WeightTemplate> templates = TemplateTestUtils.generate(2);
        when(memoryList.getSelectedTemplates()).thenReturn(templates);
        when(templatesLiveData.getValue()).thenReturn(templates);
        presenter.onSelectionTemplatesChanged();
        verify(view, times(1)).showActionMode();
        verify(view, times(1)).enableActionModeChangeItem(booleanCaptor.capture());
        assertThat(booleanCaptor.getValue()).isEqualTo(false); // newSize == 2
        verify(view, times(1)).enableActionModeSelectAllItem(booleanCaptor.capture());
        assertThat(booleanCaptor.getValue()).isEqualTo(false); // templates size == newSize
        verify(view, times(1)).enableActionModeDeleteItem(booleanCaptor.capture());
        assertThat(booleanCaptor.getValue()).isEqualTo(true); // newSize != 0
    }

    @Test
    public void testOnSelectionTemplatesChanged_SelectionFinished() {
        final List<WeightTemplate> templates = TemplateTestUtils.generate(2);
        when(templatesLiveData.getValue()).thenReturn(templates);
        when(memoryList.getSelectedTemplates()).thenReturn(templates);
        presenter.onSelectionTemplatesChanged();
        Mockito.reset(view);
        when(memoryList.getSelectedTemplates()).thenReturn(Collections.emptyList());
        presenter.onSelectionTemplatesChanged();
        verify(view, times(1)).hideActionMode();
        verify(view, times(1)).enableActionModeChangeItem(booleanCaptor.capture());
        assertThat(booleanCaptor.getValue()).isEqualTo(false); // newSize == 2
        verify(view, times(1)).enableActionModeSelectAllItem(booleanCaptor.capture());
        assertThat(booleanCaptor.getValue()).isEqualTo(true); // templates size == newSize
        verify(view, times(1)).enableActionModeDeleteItem(booleanCaptor.capture());
        assertThat(booleanCaptor.getValue()).isEqualTo(false); // newSize != 0
    }

    @Test
    public void testChangeTemplateComment() {
        final List<WeightTemplate> templates = TemplateTestUtils.generate(1);
        when(memoryList.getSelectedTemplates()).thenReturn(templates);
        presenter.changeTemplateComment();
        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        verify(view, times(1)).showTemplateCommentDialog(stringCaptor.capture());
        assertThat(templates.get(0).getComment()).isEqualTo(stringCaptor.getValue());
    }

}