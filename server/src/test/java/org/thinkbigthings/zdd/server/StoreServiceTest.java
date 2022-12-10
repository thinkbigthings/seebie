package org.thinkbigthings.zdd.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.thinkbigthings.zdd.server.entity.Store;
import org.thinkbigthings.zdd.server.entity.StoreItem;
import org.thinkbigthings.zdd.server.repository.StoreRepository;
import org.thinkbigthings.zdd.server.scraper.keystone.Scraper;
import org.thinkbigthings.zdd.server.service.StoreService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.platform.commons.util.Preconditions.condition;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.thinkbigthings.zdd.server.test.data.TestData.readItems;

public class StoreServiceTest {

    private StoreRepository storeRepo = Mockito.mock(StoreRepository.class);
    private Scraper scraper = Mockito.mock(Scraper.class);

    private String storeName = "keystone";

    private StoreService service;

    @BeforeEach
    public void setup() throws IOException {

        service = new StoreService(storeRepo, scraper);

        Store savedStore = new Store(storeName, "");
        savedStore.setItems(new HashSet<>(readItems("keystone-devon-flower-20210909.json")));

        when(storeRepo.findByName(eq(savedStore.getName()))).thenReturn(of(savedStore));
    }

    @Test
    public void updateItems() throws IOException {

        Store savedStore = storeRepo.findByName(storeName).get();

        List<StoreItem> newItems = readItems("keystone-devon-flower-20210910.json");
        List<StoreItem> oldItems = new ArrayList<>(savedStore.getItems());

        // precondition on specific items, find items in both, in just the one, and in just the other

        String strainInBoth = "Dream Queen";
        String strainInOldOnly = "Cinnamon Buns";
        String strainInNewOnly = "Sour Diesel";

        String preconditionMessage = "Must contain expected elements";
        condition(newItems.stream().anyMatch(item -> item.getStrain().equals(strainInBoth)),    preconditionMessage);
        condition(newItems.stream().anyMatch(item -> item.getStrain().equals(strainInNewOnly)), preconditionMessage);
        condition(oldItems.stream().anyMatch(item -> item.getStrain().equals(strainInBoth)),    preconditionMessage);
        condition(oldItems.stream().anyMatch(item -> item.getStrain().equals(strainInOldOnly)), preconditionMessage);

        service.updateStoreItems(storeName, newItems);

        assertAll(
                () -> savedStore.getItems().stream().anyMatch(item -> item.getStrain().equals(strainInBoth)),
                () -> savedStore.getItems().stream().anyMatch(item -> item.getStrain().equals(strainInNewOnly)),
                () -> savedStore.getItems().stream().noneMatch(item -> item.getStrain().equals(strainInOldOnly))
        );

    }

}
