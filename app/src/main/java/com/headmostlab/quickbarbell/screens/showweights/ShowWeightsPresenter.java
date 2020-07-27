package com.headmostlab.quickbarbell.screens.showweights;

import com.headmostlab.quickbarbell.business.BarTypes;
import com.headmostlab.quickbarbell.business.TransitData;
import com.headmostlab.quickbarbell.business.assembledbar.AssembledBar;
import com.headmostlab.quickbarbell.business.billing.BillingRepository;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.database.dao.BarDao;
import com.headmostlab.quickbarbell.model.database.dao.DiskDao;
import com.headmostlab.quickbarbell.utils.BigDecimalUtils;
import com.headmostlab.quickbarbell.utils.DiskUtils;
import com.headmostlab.quickbarbell.utils.WeightUtils;
import com.headmostlab.quickbarbell.utils.Xml;
import com.headmostlab.quickbarbell.utils.XmlParser;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.WeightsListContract;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.model.BarCard;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.model.BarCardHidden;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.model.Card;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.model.DiskCard;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.model.DiskCardHidden;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.xml.transform.TransformerException;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

public class ShowWeightsPresenter extends ViewModel implements ShowWeightsContract.Presenter {

    private final BarDao barDao;
    private final DiskDao diskDao;
    private final WeightListCardsProvider weightListCardsProvider;
    private final MeasurementUnit systemUnit;
    private final BillingRepository billingRepository;
    private WeakReference<ShowWeightsContract.View> view;
    private AssembledBar assembledBar;
    private String title;
    private String subTitle;
    private WeightsListContract.Presenter weightsListPresenter;
    private XmlParser xmlParser;
    private Provider<Xml> xmlProvider;
    private final TransitData transitData;


    @Inject
    public ShowWeightsPresenter(TransitData transitData, MeasurementUnit systemUnit,
                                WeightsListContract.Presenter weightsListPresenter,
                                Provider<Xml> xmlProvider, XmlParser xmlParser,
                                BarDao barDao, DiskDao diskDao, BillingRepository billingRepository) {
        this.systemUnit = systemUnit;
        this.weightsListPresenter = weightsListPresenter;
        this.xmlProvider = xmlProvider;
        this.xmlParser = xmlParser;
        this.barDao = barDao;
        this.diskDao = diskDao;
        this.transitData = transitData;

        this.view = new WeakReference<>(null);
        this.assembledBar = transitData.getAssembledBar();
        this.weightListCardsProvider = new WeightListCardsProvider();
        this.billingRepository = billingRepository;

        init();
    }

    private void init() {
        this.weightListCardsProvider.setAssembledBar(assembledBar, !barDao.isOneUnit(), !diskDao.isOneUnit());
    }

    @Override
    public void takeView(@NonNull ShowWeightsContract.View view) {
        this.view = new WeakReference<>(view);
        createTitle();
        showTitle();
        showAssembledBar();
        loadSkus();
    }

    private void loadSkus() {
        billingRepository.getProducts().observe(view().getLifeCycleOwner(), products -> {
            List<Card> cards = weightListCardsProvider.getCards();
            if (products.isEmpty()) {
                hideCardDescription(cards);
            }
            weightsListPresenter.setCards(cards);
        });
    }

    private void hideCardDescription(List<Card> cards) {
        List<Card> newCards = new ArrayList<>();
        for (Card card : cards) {
            newCards.add(card instanceof BarCard ?
                    new BarCardHidden((BarCard) card) : new DiskCardHidden((DiskCard) card));
        }
        cards.clear();
        cards.addAll(newCards);
    }

    private void createTitle() {
        if (title == null) {
            title = BigDecimalUtils.toString(WeightUtils.convertKgTo(assembledBar.getWeightInKg(), systemUnit) ) +
                    (assembledBar.getBar().getBarType() == BarTypes.DOUBLE_DUMBBELL ? "x2" : "");
        }

        if (subTitle == null) {

            final BigDecimal roundedAssembledBarWeightInSystemUnit =
                    BigDecimalUtils.round(WeightUtils.convertKgTo(assembledBar.getWeightInKg(), systemUnit));

            final BigDecimal roundedPreciseWeight =
                    BigDecimalUtils.round(transitData.getPreciseWeight());

            BigDecimal deviation = roundedAssembledBarWeightInSystemUnit.subtract(roundedPreciseWeight);
            String deviationText = (deviation.compareTo(BigDecimal.ZERO) > 0 ? "+" : "") + BigDecimalUtils.toString(deviation);

            subTitle = BigDecimalUtils.toString(transitData.getPreciseWeight()) + (deviationText.equals("0") ? "" : "; "+deviationText);
                     ;
        }
    }

    private void showAssembledBar() {
        if (isView()) {
            view().showAssembledBar(assembledBar);
        }
    }

    private void showTitle() {
        if (isView()) {
            view().showTitle(title, subTitle);
        }
    }

    public boolean isView() {
        return view() != null;
    }

    public ShowWeightsContract.View view() {
        return view.get();
    }

    @Override
    public WeightsListContract.Presenter weightsListPresenter() {
        return weightsListPresenter;
    }

    @Override
    public void restoreState(String xml) {
        if (assembledBar != null) {
            return;
        }
        try {
            Xml x = xmlParser.parse(xml);
            assembledBar = new AssembledBar(barDao.getById(x.getLong("barId")),
                    diskDao.getByIds(x.getLongs("leftWeights")),
                    diskDao.getByIds(x.getLongs("rightWeights"))
            );
            transitData.setPreciseWeight(x.getBigDecimal("preciseWeight"));

            init();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String serialize() {
        Xml xml = xmlProvider.get();
        xml.putLong("barId", assembledBar.getBar().getId());
        xml.putLongs("leftWeights", DiskUtils.getDiskIds(assembledBar.getLeftDisks()));
        xml.putLongs("rightWeights", DiskUtils.getDiskIds(assembledBar.getRightDisks()));
        xml.putBigDecimal("preciseWeight", transitData.getPreciseWeight());
        try {
            return xml.getString();
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }
}