package com.headmostlab.quickbarbell.business.billing.products;

import java.util.Arrays;

public class Product {
    private String sku;

    public Product(String sku) {
        this.sku = sku;
    }

    public String getSku() {
        return sku;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return sku.equals(product.sku);
    }

    @Override
    public int hashCode() {
        return  Arrays.hashCode(new String[]{sku});
    }
}
