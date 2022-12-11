package com.seebie.server.service;

import com.seebie.server.mapper.dtotoentity.SavedSearchRecordToEntity;
import com.seebie.server.mapper.entitytodto.ItemMapper;
import com.seebie.server.mapper.entitytospec.SearchConfigToSpec;
import com.seebie.server.mapper.entitytospec.SearchItemToSpec;
import com.seebie.server.mapper.entitytospec.StoreNamesToSpec;
import com.seebie.server.repository.SearchConfigRepository;
import com.seebie.server.repository.StoreItemRepository;
import com.seebie.server.scraper.keystone.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.seebie.dto.SearchItemsInStores;

import java.util.ArrayList;
import java.util.List;


@Service
public class SearchService {

    private static Logger LOG = LoggerFactory.getLogger(SearchService.class);

    private SearchConfigToSpec searchConfigToSpec = new SearchConfigToSpec();
    private SavedSearchRecordToEntity dtoToEntity = new SavedSearchRecordToEntity();
    private SearchItemToSpec searchItemEntityToSpec = new SearchItemToSpec();
    private StoreNamesToSpec storeNamesToSpec = new StoreNamesToSpec();
    private ItemMapper toItemDto = new ItemMapper();

    private SearchConfigRepository searchRepo;
    private StoreItemRepository itemRepo;

    public SearchService(SearchConfigRepository repo, StoreItemRepository storeItemRepo) {
        this.searchRepo = repo;
        this.itemRepo = storeItemRepo;
    }

    @Transactional(readOnly = true)
    public List<Item> search(String username) {

        var items = searchRepo.findActiveByUsername(username)
                .map(searchConfigToSpec)
                .map(itemRepo::findAll)
                .orElse(new ArrayList<>());

        return items.stream().map(toItemDto).toList();
    }

    /**
     * This is for users to test out their searches,
     * it's like an alert search but doesn't limit items by date added.
     *
     * @param userSearches
     * @return
     */
    @Transactional(readOnly = true)
    public List<Item> search(SearchItemsInStores userSearches) {

        var itemsSearch = userSearches.searches().stream()
                .map(dtoToEntity)
                .map(searchItemEntityToSpec)
                .reduce(Specification::or)
                .orElse(StoreNamesToSpec.TRUE);

        var byStore = storeNamesToSpec.apply(userSearches.storeNames());

        var results = itemRepo.findAll(itemsSearch.and(byStore));

        return results.stream()
                .map(toItemDto)
                .toList();
    }

}
