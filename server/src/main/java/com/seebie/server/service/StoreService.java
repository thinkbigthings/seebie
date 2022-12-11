package com.seebie.server.service;

import com.seebie.dto.StoreRecord;
import com.seebie.server.entity.Store;
import com.seebie.server.entity.StoreItem;
import com.seebie.server.repository.StoreRepository;
import com.seebie.server.scraper.keystone.Scraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thinkbigthings.zdd.dto.*;
import org.thinkbigthings.zdd.server.entity.*;

import java.time.Instant;
import java.util.*;

import static java.util.function.Predicate.not;

@Service
public class StoreService {

    private static Logger LOG = LoggerFactory.getLogger(StoreService.class);

    private StoreRepository storeRepository;
    private Scraper scraper;

    private final Comparator<StoreItem> comparator = Comparator.comparing(StoreItem::getStrain)
            .thenComparing(StoreItem::getSubspecies)
            .thenComparing(StoreItem::getWeightGrams)
            .thenComparing(StoreItem::getVendor);

    public StoreService(StoreRepository repo, Scraper scraper) {
        this.storeRepository = repo;
        this.scraper = scraper;
    }

    @Transactional(readOnly = true)
    public Page<StoreRecord> getStores(Pageable page) {
        return storeRepository.loadSummaries(page);
    }

    @Transactional(readOnly = true)
    public Store getStore(String storeName) {
        return storeRepository.findByName(storeName).get();
    }

    @Transactional
    public Store saveNewStore(StoreRecord newStore) {
        return storeRepository.save(new Store(newStore.name(), newStore.website()));
    }

    @Transactional
    public void scrapeStores() {

        record StoreUpdate(List<StoreItem> items, String storeName) {};

        storeRepository.findAll().stream()
                .map(store -> new StoreUpdate(scraper.scrape(store.getWebsite()), store.getName()))
                .filter(not(storeUpdate -> storeUpdate.items().isEmpty()))
                .forEach(storeUpdate -> updateStoreItems(storeUpdate.storeName(), storeUpdate.items()));
    }

    public static <T> boolean contains(Collection<T> collection, T element, Comparator<T> comparator) {
        return collection.stream().anyMatch(e -> comparator.compare(e, element) == 0);
    }

    public static <T> boolean retainAll(Collection<T> collection, Collection<T> elementsToRetain, Comparator<T> comparator) {
        return collection.removeIf(not(e -> contains(elementsToRetain, e, comparator)));
    }

    public static <T> boolean addAll(Collection<T> collection, Collection<T> elementsToAdd, Comparator<T> comparator) {
        return elementsToAdd.stream()
                .filter(not(e -> contains(collection, e, comparator)))
                .map(collection::add)
                .reduce(false, (b1, b2) -> b1 || b2);
    }

    @Transactional
    public void updateStoreItems(String storeName, List<StoreItem> latestItems) {

        Store store = storeRepository.findByName(storeName).get();

        // remove old items not in new dataset, then add all new items
        // compare items with comparator instead of .equals() since new items weren't persisted yet
        Set<StoreItem> originalItems = store.getItems();
        retainAll(originalItems, latestItems, comparator);
        addAll(originalItems, latestItems, comparator);

        latestItems.forEach(item -> item.setAdded(Instant.now()));
        latestItems.forEach(item -> item.setStore(store));

        // JPA will flush all this automatically and we don't need to call save on the repository
    }
}
