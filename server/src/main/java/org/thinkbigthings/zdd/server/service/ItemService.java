package org.thinkbigthings.zdd.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thinkbigthings.zdd.server.mapper.entitytodto.ItemMapper;
import org.thinkbigthings.zdd.server.repository.StoreItemRepository;
import org.thinkbigthings.zdd.server.scraper.keystone.Item;

@Service
public class ItemService {

    private static Logger LOG = LoggerFactory.getLogger(ItemService.class);

    private ItemMapper toItemDto = new ItemMapper();

    private StoreItemRepository itemRepository;

    public ItemService(StoreItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Transactional(readOnly = true)
    public Page<Item> findItems(Pageable page) {
        return itemRepository.findAll(page).map(toItemDto);
    }

}
