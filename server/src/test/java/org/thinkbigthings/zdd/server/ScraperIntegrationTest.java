package org.thinkbigthings.zdd.server;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.thinkbigthings.zdd.dto.StoreRecord;
import org.thinkbigthings.zdd.server.entity.Store;
import org.thinkbigthings.zdd.server.entity.StoreItem;
import org.thinkbigthings.zdd.server.repository.StoreItemRepository;
import org.thinkbigthings.zdd.server.service.StoreService;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.platform.commons.util.Preconditions.condition;
import static org.thinkbigthings.zdd.server.test.data.TestData.readItems;


public class ScraperIntegrationTest extends IntegrationTest {

    protected static Logger LOG = LoggerFactory.getLogger(ScraperIntegrationTest.class);

    private Pageable firstPage = PageRequest.of(0, 10);

    @Autowired
    private StoreService storeService;

    @Autowired
    private StoreItemRepository itemRepository;

    @Test
    @DisplayName("Write items to database without hitting live server")
    public void testScraperFromDisk() throws IOException {

        String name = "testScraperFromDisk-" + UUID.randomUUID();
        Page<StoreItem> items;

        LOG.info("Using store name " + name);

        storeService.saveNewStore(new StoreRecord(name, name));
        items = itemRepository.findByStoreName(name, firstPage);
        condition(items.isEmpty(), "Should start with no items");

        storeService.updateStoreItems(name, readItems("keystone-devon-flower-20210909.json"));
        items = itemRepository.findByStoreName(name, firstPage);
        assertFalse(items.isEmpty(), "Should have items");

        Instant latestBeforeUpdate = items.stream().findFirst().get().getAdded();
        storeService.updateStoreItems(name, readItems("keystone-devon-flower-20210910.json"));
        items = itemRepository.findByStoreName(name, firstPage);
        Instant latestAfterUpdate = items.stream().findFirst().get().getAdded();
        assertTrue(latestAfterUpdate.isAfter(latestBeforeUpdate));
    }

}
