package org.thinkbigthings.zdd.server.mapper.entitytodto;

import org.thinkbigthings.zdd.server.entity.StoreItem;
import org.thinkbigthings.zdd.server.scraper.keystone.Item;

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
