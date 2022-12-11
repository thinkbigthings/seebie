package com.seebie.server.mapper.entitytodto;

import com.seebie.dto.ItemSearch;
import com.seebie.dto.SearchItemsInStores;
import com.seebie.dto.SearchParameter;
import com.seebie.server.entity.SavedSearch;
import com.seebie.server.entity.SearchConfig;
import com.seebie.server.entity.Store;

import java.util.List;
import java.util.function.Function;

public class SearchConfigMapper implements Function<SearchConfig, SearchItemsInStores> {

    private OperatorToStringMapper opMapper = new OperatorToStringMapper();

    @Override
    public SearchItemsInStores apply(SearchConfig searchConfig) {

        List<String> storeNames = searchConfig.getSearchStores().stream()
                .map(Store::getName)
                .toList();

        List<ItemSearch> searches = searchConfig.getSubSearches().stream()
                .map(this::fromEntity)
                .toList();

        return new SearchItemsInStores(storeNames, searches);
    }

    public ItemSearch fromEntity(SavedSearch entity) {

        List<SearchParameter> parameters = entity.getSearchParameters().stream()
                .map(this::fromEntity)
                .toList();

        return new ItemSearch(parameters);
    }

    public SearchParameter fromEntity(com.seebie.server.entity.SearchParameter parameter) {
        return new SearchParameter(parameter.getField(), opMapper.apply(parameter.getOperator()), parameter.getValue());
    }
}
