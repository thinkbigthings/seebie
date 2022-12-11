package com.seebie.server.mapper.entitytodto;

import com.seebie.server.scraper.keystone.Item;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.Test;
import com.seebie.server.entity.StoreItem;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemMapperTest {

    private ItemMapper toItemDto = new ItemMapper();

    @Test
    public void testItemMapping() {

        StoreItem entity = TestData.randomItem();

        Item item = toItemDto.apply(entity);

        assertEquals(entity.getThcPercent(), item.thc());
        assertEquals(entity.getCbdPercent(), item.cbd());
    }
}
