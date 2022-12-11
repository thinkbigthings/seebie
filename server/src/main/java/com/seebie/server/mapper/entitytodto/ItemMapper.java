package com.seebie.server.mapper.entitytodto;

import com.seebie.server.scraper.keystone.Item;
import com.seebie.server.entity.StoreItem;

import java.util.function.Function;

public class ItemMapper implements Function<StoreItem, Item> {

    @Override
    public Item apply(StoreItem item) {
        return new Item(item.getSubspecies(),
                item.getStrain(),
                item.getThcPercent(),
                item.getCbdPercent(),
                item.getWeightGrams(),
                item.getPriceDollars(),
                item.getVendor());
    }
}
