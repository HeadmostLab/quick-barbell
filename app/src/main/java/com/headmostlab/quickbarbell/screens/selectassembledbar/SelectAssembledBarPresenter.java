package com.headmostlab.quickbarbell.screens.selectassembledbar;

import com.headmostlab.quickbarbell.business.BarTypes;
import com.headmostlab.quickbarbell.business.TransitData;
import com.headmostlab.quickbarbell.business.assembledbar.AssembledBar;
import com.headmostlab.quickbarbell.business.assembledbar.AssembledBarsProvider;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.database.dao.BarDao;
import com.headmostlab.quickbarbell.utils.BigDecimalUtils;
import com.headmostlab.quickbarbell.utils.WeightUtils;
import com.headmostlab.quickbarbell.utils.Xml;
import com.headmostlab.quickbarbell.utils.XmlParser;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards.AssembledBarCard;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.xml.transform.TransformerException;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

public class SelectAssembledBarPresenter extends ViewModel implements SelectAssembledBarContract.Presenter {

    private final TransitData transitData;
    private final BarDao barDao;
    private final MeasurementUnit systemUnit;
    private WeakReference<SelectAssembledBarContract.View> view;
    private XmlParser xmlParser;
    private Provider<Xml> xmlProvider;
    private List<AssembledBarCard> cards;
    private List<AssembledBar> bars;
    private Integer restoredPosition;
    private AssembledBarsProvider assembledBarsProvider;

    @Inject
    public SelectAssembledBarPresenter(TransitData transitData, MeasurementUnit systemUnit, BarDao barDao,
                                       Provider<Xml> xmlProvider, XmlParser xmlParser, AssembledBarsProvider assembledBarsProvider) {
        this.transitData = transitData;
        this.systemUnit = systemUnit;
        this.barDao = barDao;
        this.view = new WeakReference<>(null);
        this.xmlProvider = xmlProvider;
        this.xmlParser = xmlParser;
        this.assembledBarsProvider = assembledBarsProvider;

        initAssembledBarsProvider();
    }

    private void initAssembledBarsProvider() {
        assembledBarsProvider.setPreciseWeight(transitData.getPreciseWeight());
        assembledBarsProvider.setBar(transitData.getChosenBar());
        assembledBarsProvider.setBalanced(transitData.isBalanced());
    }

    @Override
    public void takeView(@NonNull SelectAssembledBarContract.View view) {
        this.view = new WeakReference<>(view);
        setTitle();
        loadCards();
    }

    private void loadCards() {
        if (bars == null) {
            bars = assembledBarsProvider.getBars();
            cards = new ArrayList<>();
            for (AssembledBar assembledBar : bars) {
                final BigDecimal roundedAssembledBarWeightInSystemUnit =
                        BigDecimalUtils.round(WeightUtils.convertKgTo(assembledBar.getWeightInKg(), systemUnit));

                final BigDecimal roundedPreciseWeight =
                        BigDecimalUtils.round(transitData.getPreciseWeight());

                cards.add(new AssembledBarCard(assembledBar, roundedAssembledBarWeightInSystemUnit,
                        roundedAssembledBarWeightInSystemUnit.subtract(roundedPreciseWeight)));
            }
            if (bars.size() > 0) {
                transitData.setAssembledBar(bars.get(0));
            }
        }
        if (isView()) {
            view().showCards(cards);
            restorePosition(restoredPosition);
        }
    }

    private void restorePosition(Integer restoredPosition) {
        if (isView() && restoredPosition != null && restoredPosition >= 0 && restoredPosition < cards.size()) {
            final AssembledBarCard card = cards.get(restoredPosition);
            view().scrollCarouselViewTo(card);
            view().scrollScrollBarTo(card);
            transitData.setAssembledBar(bars.get(restoredPosition));
            this.restoredPosition = null;
        }
    }

    @Override
    public void selectCardByCarouselView(AssembledBarCard card) {
        transitData.setAssembledBar(card.getAssembledBar());
        if (isView()) {
            view().scrollScrollBarTo(card);
        }
    }

    @Override
    public void selectCardByScrollBar(AssembledBarCard card) {
        transitData.setAssembledBar(card.getAssembledBar());
        if (isView()) {
            view().scrollCarouselViewTo(card);
        }
    }

    @Override
    public void clickCurCard() {
        if (isView()) {
            view().showShowWeightsScreen();
        }
    }

    @Override
    public BarTypes getBarType() {
        return transitData.getChosenBar().getBarType();
    }

    @Override
    public MeasurementUnit getMeasurement() {
        return systemUnit;
    }

    @Override
    public void restoreState(String xml) {
        try {
            final Xml x = xmlParser.parse(xml);
            transitData.setChosenBar(barDao.getById(x.getLong("barId")));
            transitData.setPreciseWeight(x.getBigDecimal("preciseWeight"));
            transitData.setBalanced(x.getBoolean("balanced"));
            restoredPosition = x.getInt("position");
            initAssembledBarsProvider();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String serialize() {
        Xml xml = xmlProvider.get();
        xml.putLong("barId", transitData.getChosenBar().getId());
        xml.putBigDecimal("preciseWeight", transitData.getPreciseWeight());
        xml.putBoolean("balanced", transitData.isBalanced());
        xml.putInt("position", bars.indexOf(transitData.getAssembledBar()));
        try {
            return xml.getString();
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    private void setTitle() {
        if (isView()) {
            view().showTitle(BigDecimalUtils.toString(transitData.getPreciseWeight()));
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        transitData.setAssembledBar(null);
    }

    public boolean isView() {
        return view() != null;
    }

    public SelectAssembledBarContract.View view() {
        return view.get();
    }
}