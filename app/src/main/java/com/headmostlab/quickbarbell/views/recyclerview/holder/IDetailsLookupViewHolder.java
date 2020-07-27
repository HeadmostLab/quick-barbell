package com.headmostlab.quickbarbell.views.recyclerview.holder;

import androidx.recyclerview.selection.ItemDetailsLookup;

public interface IDetailsLookupViewHolder {
    ItemDetailsLookup.ItemDetails<Long> getItemDetails();
}
