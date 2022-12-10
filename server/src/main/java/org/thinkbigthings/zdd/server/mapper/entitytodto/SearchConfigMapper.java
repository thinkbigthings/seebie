package org.thinkbigthings.zdd.server.mapper.entitytodto;

import org.thinkbigthings.zdd.dto.ItemSearch;
import org.thinkbigthings.zdd.dto.SearchItemsInStores;
import org.thinkbigthings.zdd.server.entity.SavedSearch;
import org.thinkbigthings.zdd.server.entity.SearchConfig;
import org.thinkbigthings.zdd.server.entity.Store;

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

        List<org.thinkbigthings.zdd.dto.SearchParameter> parameters = entity.getSearchParameters().stream()
                .map(this::fromEntity)
                .toList();

        return new ItemSearch(parameters);
    }

    public org.thinkbigthings.zdd.dto.SearchParameter fromEntity(org.thinkbigthings.zdd.server.entity.SearchParameter parameter) {
        return new org.thinkbigthings.zdd.dto.SearchParameter(parameter.getField(), opMapper.apply(parameter.getOperator()), parameter.getValue());
    }
}
