package com.headmostlab.quickbarbell.screens.barinventory;

import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.business.BarTypes;
import com.headmostlab.quickbarbell.business.billing.BillingRepository;
import com.headmostlab.quickbarbell.business.billing.products.Product;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.database.dao.BarDao;
import com.headmostlab.quickbarbell.model.database.entities.Bar;
import com.headmostlab.quickbarbell.utils.Xml;
import com.headmostlab.quickbarbell.utils.XmlParser;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards.InventoryBarCard;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.WeightsListContract;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.model.BarCard;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.model.Card;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.xml.transform.TransformerException;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;

public class BarInventoryPresenter extends ViewModel implements BarInventoryContract.Presenter {

    private final BarDao barDao;
    private final WeightsListContract.Presenter weightsListPresenter;
    private final BillingRepository billingRepository;
    private WeakReference<BarInventoryContract.View> view;
    private XmlParser xmlParser;
    private Provider<Xml> xmlProvider;
    private InventoryBarCard selectedCarouselCard;
    private List<InventoryBarCard> carouselCards;
    private final CarouselCardsProvider carouselCardsProvider;
    private BigDecimal weight = BigDecimal.ZERO;
    private LiveData<List<Bar>> bars;
    private int selectedCardsCount;
    private MeasurementUnit unit = MeasurementUnit.KILOGRAM;
    private Set<Product> products = Collections.emptySet();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    public BarInventoryPresenter(BarDao barDao, Provider<Xml> xmlProvider, XmlParser xmlParser,
                                 CarouselCardsProvider carouselCardsProvider,
                                 WeightsListContract.Presenter weightsListPresenter,
                                 BillingRepository billingRepository, MeasurementUnit systemUnit) {
        this.barDao = barDao;
        this.xmlProvider = xmlProvider;
        this.xmlParser = xmlParser;
        this.weightsListPresenter = weightsListPresenter;
        this.carouselCardsProvider = carouselCardsProvider;
        this.billingRepository = billingRepository;
        this.unit = systemUnit;

        this.view = new WeakReference<>(null);
        this.carouselCards = carouselCardsProvider.getCards();
        this.selectedCarouselCard = this.carouselCards.get(0);
    }

    @Override
    public void takeView(@NonNull BarInventoryContract.View view) {
        this.view = new WeakReference<>(view);

        showCarouselCards();
        restoreCarouselPosition();
        restoreWeight();
        restoreUnit();
        showBars();
        loadProducts();
    }

    private void loadProducts() {
        billingRepository.getProducts().observe(view().getLifeCycleOwner(), prds -> products = prds);
    }

    private void restoreUnit() {
        if (isView()) {
            view().showUnit(unit);
        }
    }

    private void showBars() {
        if (bars == null) {
            bars = barDao.getAll();
        }
        if (isView()) {
            bars.observe(view().getLifeCycleOwner(), bars -> {
                weightsListPresenter.setCards(createCards(bars));
//                weightsListPresenter.scrollToLast();
            });
        }
    }

    private List<Card> createCards(List<Bar> bars) {
        List<Card> cards = new ArrayList<>();
        for (Bar bar : bars) {
            cards.add(new BarCard(bar));
        }
        return cards;
    }

    private void restoreWeight() {
        if (isView()) {
            view().shoWeight(weight);
        }
    }

    private void restoreCarouselPosition() {
        if (isView()) {
            view().scrollCarouselViewTo(selectedCarouselCard);
            view().scrollScrollBarTo(selectedCarouselCard);
        }
    }

    private void showCarouselCards() {
        if (isView()) {
            view().showCarouselCards(carouselCards);
        }
    }

    @Override
    public void selectCardByCarouselView(InventoryBarCard card) {
        selectedCarouselCard = card;

        if (isView()) {
            view().scrollScrollBarTo(card);
        }
    }

    @Override
    public void selectCardByScrollBar(InventoryBarCard card) {
        selectedCarouselCard = card;

        if (isView()) {
            view().scrollCarouselViewTo(card);
        }
    }

    @Override
    public void restoreState(String xml) {
        try {
            final Xml x = xmlParser.parse(xml);
            final String barTypeName = x.getString("barType");
            weight = x.getBigDecimal("weight");
            unit = MeasurementUnit.values()[x.getInt("unit")];
            if (barTypeName != null) {
                selectedCarouselCard = carouselCardsProvider.find(BarTypes.valueOf(barTypeName));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String serialize() {
        Xml xml = xmlProvider.get();
        xml.putString("barType", selectedCarouselCard.getBarType().name());
        xml.putBigDecimal("weight", weight);
        xml.putInt("unit", unit.ordinal());
        try {
            return xml.getString();
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    @Override
    public WeightsListContract.Presenter getWeightsListPresenter() {
        return weightsListPresenter;
    }

    @Override
    public void deleteSelectedBars() {
        List<Bar> bars = new ArrayList<>();
        final List<Card> selectedCards = weightsListPresenter.getSelectedCards();
        for (Card card : selectedCards) {
            if (card instanceof BarCard) {
                BarCard barCard = (BarCard) card;
                bars.add(barCard.getBar());
            }
        }
        barDao.deleteAll(bars);
        view().hideActionMode();
    }

    @Override
    public void onSelectionCardsChanged() {
        if (view() == null) {
            return;
        }

        final int oldSize = selectedCardsCount;
        final int newSize = weightsListPresenter.getSelectedCards().size();
        selectedCardsCount = newSize;

        boolean isSizeChanged = oldSize != newSize;
        boolean isSelectionStarted = oldSize == 0 && newSize != 0;
        boolean isSelectionFinished = oldSize != 0 && newSize == 0;

        if (isSelectionStarted) {
            view().showActionMode();

        } else if (isSelectionFinished) {
            view().hideActionMode();
        }

        if (isSizeChanged) {
            view().showActionModeChangeItem(newSize == 1);
            view().enableActionModeSelectAllItem(bars.getValue().size() != newSize);
        }
    }

    public boolean isView() {
        return view() != null;
    }

    public BarInventoryContract.View view() {
        return view.get();
    }

    @Override
    public void createBar() {
        if (products.isEmpty()) {
            if (isView()) {
                view().showShop();
            }
            return;
        }
        if (weight.compareTo(BigDecimal.ZERO) == 0) {
            if (isView()) {
                view().showMessage(R.string.abi_specify_weight);
            }
        } else {
            barDao.insert(new Bar(selectedCarouselCard.getBarType(), weight, unit));
            if (isView()) {
                view().showMessage(R.string.abi_bar_added);
            }
        }
    }

    @Override
    public void setUnit(MeasurementUnit unit) {
        this.unit = unit;
    }

    @Override
    public void startToUpdateBar() {
        if (selectedCardsCount == 1) {
            if (isView()) {
                BarCard card = (BarCard)weightsListPresenter.getSelectedCards().get(0);
                view().showEditBarDialog(card.getUnit(), card.getWeight(), card.getBarType());
            }
        }
    }

    @Override
    public void updateBar(MeasurementUnit unit, BigDecimal weight, BarTypes barType) {
        if (selectedCardsCount == 1) {
            if (isView()) {
                BarCard card = (BarCard)weightsListPresenter.getSelectedCards().get(0);
                Bar bar = card.getBar();

                bar.setUnit(unit);
                bar.setWeight(weight);
                bar.setBarType(barType);

                card.setBarType(barType);
                card.setWeight(weight);
                card.setUnit(unit);

                weightsListPresenter.update(card);
                barDao.update(bar);
                weightsListPresenter.exitSelectionMode();
            }
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}