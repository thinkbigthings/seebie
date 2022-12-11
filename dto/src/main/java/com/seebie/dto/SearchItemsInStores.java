package com.seebie.dto;

import java.util.ArrayList;
import java.util.List;

public record SearchItemsInStores(List<String> storeNames, List<ItemSearch> searches) {

    public static SearchItemsInStores newSavedSearches() {
        return new SearchItemsInStores(new ArrayList<>(), new ArrayList<>());
    }

    public SearchItemsInStores withStore(String storeName) {
        List<String> newStoreNames = new ArrayList<>(storeNames);
        newStoreNames.add(storeName);
        return new SearchItemsInStores(List.copyOf(newStoreNames), searches);
    }

    public SearchItemsInStores withSearch(String field, String operator, String value) {
        return withSearch(new ItemSearch(List.of(new SearchParameter(field, operator, value))));
    }

    public SearchItemsInStores withSearch(ItemSearch newSearch) {
        List<ItemSearch> newSearches = new ArrayList<>(searches);
        newSearches.add(newSearch);
        return new SearchItemsInStores(storeNames, List.copyOf(newSearches));
    }

}
