package com.seebie.server.service;

import com.seebie.server.entity.SearchConfig;
import com.seebie.server.mapper.dtotoentity.SavedSearchRecordToEntity;
import com.seebie.server.mapper.entitytodto.SearchConfigMapper;
import com.seebie.server.repository.SearchConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.seebie.dto.SearchItemsInStores;


@Service
public class SearchConfigService {

    private static Logger LOG = LoggerFactory.getLogger(SearchConfigService.class);

    private SearchConfigRepository searchConfigRepo;

    private SavedSearchRecordToEntity searchToEntity = new SavedSearchRecordToEntity();
    private SearchConfigMapper searchConfigMapper = new SearchConfigMapper();

    public SearchConfigService(SearchConfigRepository repo) {
        this.searchConfigRepo = repo;
    }

    @Transactional(readOnly = true)
    public SearchItemsInStores getSearches(String username) {
        return searchConfigRepo.findByUsername(username).map(searchConfigMapper).get();
    }

    @Transactional
    public void updateSearchConfig(String username, SearchItemsInStores updatedConfig) {

        SearchConfig config = searchConfigRepo.findByUsername(username).get();

        config.getSearchStores().clear();
        config.getSearchStores().addAll(searchConfigRepo.findByNames(updatedConfig.storeNames()));

        config.getSubSearches().clear();
        config.getSubSearches().addAll(updatedConfig.searches().stream().map(searchToEntity).toList());
        config.getSubSearches().forEach(search -> search.setSearchConfig(config));

        // JPA will flush all this automatically and we don't need to call save on the repository
    }

}
