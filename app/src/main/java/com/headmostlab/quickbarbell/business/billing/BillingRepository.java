package com.headmostlab.quickbarbell.business.billing;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.headmostlab.quickbarbell.business.billing.details.InappSkuDetails;
import com.headmostlab.quickbarbell.business.billing.details.SubSkuDetails;
import com.headmostlab.quickbarbell.business.billing.products.Inapp;
import com.headmostlab.quickbarbell.business.billing.products.Product;
import com.headmostlab.quickbarbell.business.billing.products.Sub;
import com.headmostlab.quickbarbell.di.ApplicationScope;
import com.headmostlab.quickbarbell.licensing.Security;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

@ApplicationScope
public class BillingRepository implements PurchasesUpdatedListener, BillingClientStateListener {

    private Context context;
    private BillingClient playStoreBillingClient;
    private String key;

    private Set<Product> storedProducts = new HashSet<>();
    private MutableLiveData<Set<Product>> productsLiveData = new MutableLiveData<>();

    private Set<Inapp> storedInapp = new HashSet<>();
    private MutableLiveData<Set<Inapp>> inappLiveData = new MutableLiveData<>();
    private Set<InappSkuDetails> storedInappSkuDetails = new HashSet<>();
    private MutableLiveData<Set<InappSkuDetails>> inappSkuDetailsLiveData = new MutableLiveData<>();

    private Set<Sub> storedSub = new HashSet<>();
    private MutableLiveData<Set<Sub>> subLiveData = new MutableLiveData<>();
    private Set<SubSkuDetails> storedSubSkuDetails = new HashSet<>();
    private MutableLiveData<Set<SubSkuDetails>> subSkuDetailsLiveData = new MutableLiveData<>();

    @Inject
    public BillingRepository(Application application, @Named("PublicKey") String key) {
        this.context = application;
        this.key = key;
    }

    private void clearCache() {
        storedProducts.clear();
        storedInapp.clear();
        storedSub.clear();
    }

    public LiveData<Set<Product>> getProducts() {
//        productsLiveData.setValue(new HashSet<>(Arrays.asList(new Product("sub")))); // TO DO: 29.04.2020 закомментировать
        return productsLiveData;
    }

    public LiveData<Set<Inapp>> getInapp() {
        return inappLiveData;
    }

    public LiveData<Set<Sub>> getSub() {
        return subLiveData;
    }

    public LiveData<Set<InappSkuDetails>> getInappSkuDetails() {
        return inappSkuDetailsLiveData;
    }

    public LiveData<Set<SubSkuDetails>> getSubSkuDetails() {
        return subSkuDetailsLiveData;
    }

    public void startConnection() {
        clearCache();
        endConnectionInternal();
        instantiateAndConnectToPlayBillingService();
    }

    private void endConnectionInternal() {
        if (playStoreBillingClient != null && playStoreBillingClient.isReady()) {
            playStoreBillingClient.endConnection();
        }
    }

    public void endConnection() {
        playStoreBillingClient.endConnection();
    }

    private void instantiateAndConnectToPlayBillingService() {
        playStoreBillingClient = BillingClient.newBuilder(context)
                .enablePendingPurchases()
                .setListener(this).build();
        connectToPlayBillingService();
    }

    private boolean connectToPlayBillingService() {
        if (!playStoreBillingClient.isReady()) {
            playStoreBillingClient.startConnection(this);
            return true;
        }
        return false;
    }

    @Override
    public void onBillingSetupFinished(BillingResult billingResult) {
        if (isOk(billingResult)) {
            querySkuDetailsAsync(BillingClient.SkuType.INAPP, (String[]) Skus.INAPP_SKUS.toArray());
            querySkuDetailsAsync(BillingClient.SkuType.SUBS, (String[]) Skus.SUBS_SKUS.toArray());
            queryPurchasesAsync();
        }
    }

    private void queryPurchasesAsync() {
        Set<Purchase> purchases = new HashSet<>();
        Purchase.PurchasesResult result = playStoreBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
        if (result.getPurchasesList() != null) {
            purchases.addAll(result.getPurchasesList());
        }
        if (isSubscriptionSupported()) {
            result = playStoreBillingClient.queryPurchases(BillingClient.SkuType.SUBS);
            if (result.getPurchasesList() != null) {
                purchases.addAll(result.getPurchasesList());
            }
        }
        processPurchases(purchases);
    }

    private void processPurchases(Set<Purchase> purchases) {
        Set<Purchase> validPurchases = new HashSet<>();
        for (Purchase purchase : purchases) {
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                if (isSignatureValid(purchase)) {
                    validPurchases.add(purchase);
                }
            }
        }
        acknowledgeNonConsumablePurchasesAsync(validPurchases);
    }

    private void acknowledgeNonConsumablePurchasesAsync(Set<Purchase> purchases) {
        for (Purchase purchase : purchases) {
            if (!purchase.isAcknowledged()) {
                final AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase
                        .getPurchaseToken()).build();
                playStoreBillingClient.acknowledgePurchase(params, billingResult -> {
                    if (isOk(billingResult)) {
                        addPurchase(purchase);
                        notifyAboutPurchases();
//                        consumePurchase(purchase); // TO DO: 18.01.2020 удалить строчку
                    }
                });
            } else {
                addPurchase(purchase);
//                consumePurchase(purchase); // TO DO: 18.01.2020 удалить строчку
            }
        }
        notifyAboutPurchases();
    }

//    private void consumePurchase(Purchase purchase) {
//        ConsumeParams consumeParams =
//                ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
//
//        playStoreBillingClient.consumeAsync(consumeParams, (billingResult, outToken) -> {
//            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//                LogMethod.e("Consumed");
//            }
//        });
//    }

    private void addPurchase(Purchase purchase) {
        if (Skus.SUBS_SKUS.contains(purchase.getSku())) {
            storedSub.add(new Sub(purchase.getSku()));
        } else if (Skus.INAPP_SKUS.contains(purchase.getSku())) {
            storedInapp.add(new Inapp(purchase.getSku()));
        }
        storedProducts.add(new Product(purchase.getSku()));
    }

    private void notifyAboutPurchases() {
        subLiveData.setValue(storedSub);
        inappLiveData.setValue(storedInapp);
        productsLiveData.setValue(storedProducts);
    }

    private boolean isOk(BillingResult billingResult) {
        return billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK;
    }

    private boolean isSignatureValid(Purchase purchase) {
        return Security.verifyPurchase(key, purchase.getOriginalJson(), purchase.getSignature());
    }

    private boolean isSubscriptionSupported() {
        final BillingResult billingResult = playStoreBillingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS);
        boolean succeeded = false;
        switch (billingResult.getResponseCode()) {
            case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
                connectToPlayBillingService();
                break;

            case BillingClient.BillingResponseCode.OK:
                succeeded = true;
                break;
        }
        return succeeded;
    }

    private void querySkuDetailsAsync(String skuType, String[] skuList) {
        final SkuDetailsParams params = SkuDetailsParams.newBuilder().setSkusList(Arrays.asList(skuList)).setType(skuType).build();
        playStoreBillingClient.querySkuDetailsAsync(params, (billingResult, skuDetailsList) -> {
            if (isOk(billingResult)) {
                addSkuDetails(skuDetailsList);
            }
        });
    }

    private void addSkuDetails(List<SkuDetails> skuDetailsList) {

        for (SkuDetails skuDetails : skuDetailsList) {
            if (Skus.SUBS_SKUS.contains(skuDetails.getSku())) {
                storedSubSkuDetails.add(new SubSkuDetails(skuDetails));
                subSkuDetailsLiveData.setValue(storedSubSkuDetails);
            } else if (Skus.INAPP_SKUS.contains(skuDetails.getSku())) {
                storedInappSkuDetails.add(new InappSkuDetails(skuDetails));
                inappSkuDetailsLiveData.setValue(storedInappSkuDetails);
            }
        }
    }

    @Override
    public void onBillingServiceDisconnected() {
        connectToPlayBillingService();
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        switch (billingResult.getResponseCode()) {
            case BillingClient.BillingResponseCode.OK: {
                // will handle server verification, consumables, and updating the local cache
                if (purchases != null) {
                    processPurchases(new HashSet<>(purchases));
                }
                break;
            }
            case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED: {
                // item already owned? call queryPurchasesAsync to verify and process all such items
                queryPurchasesAsync();
                break;
            }
            case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED: {
                connectToPlayBillingService();
                break;
            }
        }
    }

    public void launchBillingFlow(Activity activity, SkuDetails skuDetails) {
        BillingFlowParams params = BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build();
        playStoreBillingClient.launchBillingFlow(activity, params);
    }

    private static class Skus {
        static final List<String> SUBS_SKUS = Arrays.asList("sub");
        static final List<String> INAPP_SKUS = Arrays.asList("inapp");
    }
}