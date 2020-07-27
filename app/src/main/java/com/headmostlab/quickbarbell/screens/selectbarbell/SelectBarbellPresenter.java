package com.headmostlab.quickbarbell.screens.selectbarbell;

import com.headmostlab.quickbarbell.business.TransitData;
import com.headmostlab.quickbarbell.business.billing.BillingRepository;
import com.headmostlab.quickbarbell.model.SettingsModel;
import com.headmostlab.quickbarbell.model.database.dao.BarDao;
import com.headmostlab.quickbarbell.model.database.entities.Bar;
import com.headmostlab.quickbarbell.utils.BarUtility;
import com.headmostlab.quickbarbell.utils.Xml;
import com.headmostlab.quickbarbell.utils.XmlParser;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards.SelectBarCard;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.xml.transform.TransformerException;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModel;

public class SelectBarbellPresenter extends ViewModel implements SelectBarbellContract.Presenter {

    private final BillingRepository billingRepository;
    private SettingsModel mSettingsModel;
    private TransitData transitData;
    private BarDao mBarDao;
    private WeakReference<SelectBarbellContract.View> view;
    private LiveData<List<Bar>> barsLiveData;
    private XmlParser xmlParser;
    private Provider<Xml> xmlProvider;
    private Bar restoredBar;

    @Inject
    public SelectBarbellPresenter(
            TransitData transitData, BarDao barDao, SettingsModel settingsModel,
            Provider<Xml> xmlProvider, XmlParser xmlParser, BillingRepository billingRepository) {
        this.transitData = transitData;
        this.mSettingsModel = settingsModel;
        this.mBarDao = barDao;
        this.view = new WeakReference<>(null);
        this.xmlProvider = xmlProvider;
        this.xmlParser = xmlParser;
        this.billingRepository = billingRepository;
    }

    @Override
    public void takeView(@NonNull SelectBarbellContract.View view) {
        this.view = new WeakReference<>(view);
        this.view().hideHelpIcon(mSettingsModel.isHelpHidden());
        loadCards();
        loadSkus();
    }

    private void loadSkus() {
        billingRepository.getProducts().observe(view().getLifeCycleOwner(), products -> {
            if (isView()) {
                view().showShopButton(products.isEmpty());
            }
        });
    }

    @Override
    public void loadCards() {
        if (barsLiveData == null) {
            barsLiveData = mBarDao.getAll();
        }

        barsLiveData.observe(view().getLifeCycleOwner(), bars -> {
            transitData.setChosenBar(bars.isEmpty() ? null : bars.get(0));
            if (isView()) {
                view().showCards(createBarCards(bars));
                if (restoredBar != null) {
                    transitData.setChosenBar(restoredBar);
                    view().goToBar(restoredBar);
                    restoredBar = null;
                }
            }
        });
    }

    @Override
    public void restoreState(String xml) {
        try {
            final Xml x = xmlParser.parse(xml);
            final Long barId = x.getLong("barId");
            if (barId != null) {
                final Bar bar = mBarDao.getById(barId);
                if (bar != null) {
                    restoredBar = bar;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String serialize() {
        Xml xml = xmlProvider.get();
        if (transitData.getChosenBar() != null) {
            xml.putLong("barId", transitData.getChosenBar().getId());
        }
        try {
            return xml.getString();
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    private List<SelectBarCard> createBarCards(List<Bar> bars) {
        final List<SelectBarCard> cards = new ArrayList<>();
        boolean showUnit = !mBarDao.isOneUnit();
        for (Bar bar : bars) {
            cards.add(new SelectBarCard(bar, BarUtility.getBarCardIcon(bar.getBarType()), showUnit));
        }
        return cards;
    }

    @Override
    public void selectCardByCarouselView(SelectBarCard card) {
        transitData.setChosenBar(card.getBar());
        if (isView()) {
            view().scrollScrollBarTo(card);
        }
    }

    @Override
    public void selectCardByScrollBar(SelectBarCard card) {
        transitData.setChosenBar(card.getBar());
        if (isView()) {
            view().scrollCarouselViewTo(card);
        }
    }

    @Override
    public void clickBarCard() {
        if (isView() && transitData.getChosenBar() != null) {
            view().showSelectWeightScreen();
        }
    }

    public boolean isView() {
        return view() != null;
    }

    public SelectBarbellContract.View view() {
        return view.get();
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        transitData.setChosenBar(null);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void onResume() {
        if (isView()) {
            view().hideHelpIcon(mSettingsModel.isHelpHidden());
        }
    }

}
