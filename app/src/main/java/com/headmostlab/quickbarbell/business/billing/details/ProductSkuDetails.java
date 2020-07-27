package com.headmostlab.quickbarbell.business.billing.details;

import com.android.billingclient.api.SkuDetails;

import androidx.annotation.NonNull;

public class ProductSkuDetails {
    private SkuDetails skuDetails;

    public ProductSkuDetails(SkuDetails skuDetails) {
        this.skuDetails = skuDetails;
    }

    public SkuDetails getSkuDetails() {
        return skuDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductSkuDetails)) return false;
        ProductSkuDetails product = (ProductSkuDetails) o;
        return skuDetails.equals(product.skuDetails);
    }

    @Override
    public int hashCode() {
        return skuDetails.hashCode();
    }

    @NonNull
    @Override
    public String toString() {
        return skuDetails.toString();
    }
}
