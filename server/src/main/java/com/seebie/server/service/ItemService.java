package com.seebie.server.service;

import com.seebie.server.mapper.entitytodto.ItemMapper;
import com.seebie.server.repository.StoreItemRepository;
import com.seebie.server.scraper.keystone.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
