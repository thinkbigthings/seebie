package org.thinkbigthings.zdd.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thinkbigthings.zdd.dto.SearchItemsInStores;
import org.thinkbigthings.zdd.server.mapper.dtotoentity.SavedSearchRecordToEntity;
import org.thinkbigthings.zdd.server.mapper.entitytodto.ItemMapper;
import org.thinkbigthings.zdd.server.mapper.entitytospec.SearchConfigToSpec;
import org.thinkbigthings.zdd.server.mapper.entitytospec.SearchItemToSpec;
import org.thinkbigthings.zdd.server.mapper.entitytospec.StoreNamesToSpec;
import org.thinkbigthings.zdd.server.repository.SearchConfigRepository;
import org.thinkbigthings.zdd.server.repository.StoreItemRepository;
import org.thinkbigthings.zdd.server.scraper.keystone.Item;

import java.util.ArrayList;
import java.util.List;

import static org.thinkbigthings.zdd.server.mapper.entitytospec.StoreNamesToSpec.TRUE;


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
                .orElse(TRUE);

        var byStore = storeNamesToSpec.apply(userSearches.storeNames());

        var results = itemRepo.findAll(itemsSearch.and(byStore));

        return results.stream()
                .map(toItemDto)
                .toList();
    }

}
