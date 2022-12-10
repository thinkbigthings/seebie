package org.thinkbigthings.zdd.server.mapper.entitytospec;

import org.springframework.data.jpa.domain.Specification;
import org.thinkbigthings.zdd.server.entity.SavedSearch;
import org.thinkbigthings.zdd.server.entity.StoreItem;

import java.util.function.Function;

import static org.thinkbigthings.zdd.server.mapper.entitytospec.StoreNamesToSpec.TRUE;

public class SearchItemToSpec implements Function<SavedSearch, Specification<StoreItem>> {

    private ParameterToSpec parameterToSpec = new ParameterToSpec();

    /**
     * @param searchItem the type of item to search for
     * @return true (search for all) if no search parameters are present, otherwise AND together parameters.
     */
    @Override
    public Specification<StoreItem> apply(SavedSearch searchItem) {

        return searchItem.getSearchParameters().stream()
                .map(parameterToSpec)
                .reduce(Specification::and)
                .orElse(TRUE);
    }
}
