package com.seebie.server.mapper.dtotoentity;

import com.seebie.dto.ItemSearch;
import com.seebie.dto.SearchParameter;
import com.seebie.server.entity.Operator;
import com.seebie.server.entity.SavedSearch;

import java.util.function.Function;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class SavedSearchRecordToEntity implements Function<ItemSearch, SavedSearch> {

    private StringToOperatorMapper opMapper = new StringToOperatorMapper();

    @Override
    public SavedSearch apply(ItemSearch search) {

        return search.parameters().stream()
                .map(this::toEntity)
                .collect(collectingAndThen(toList(), SavedSearch::new));
    }

    private com.seebie.server.entity.SearchParameter toEntity(SearchParameter parameter) {

        String field = parameter.field();
        Operator op = opMapper.apply(parameter.operator());
        String value = parameter.value();

        return new com.seebie.server.entity.SearchParameter(field, op, value);
    }
}
