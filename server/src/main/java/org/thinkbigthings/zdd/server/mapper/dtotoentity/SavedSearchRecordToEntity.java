package org.thinkbigthings.zdd.server.mapper.dtotoentity;

import org.thinkbigthings.zdd.dto.ItemSearch;
import org.thinkbigthings.zdd.server.entity.Operator;
import org.thinkbigthings.zdd.server.entity.SavedSearch;

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

    private org.thinkbigthings.zdd.server.entity.SearchParameter toEntity(org.thinkbigthings.zdd.dto.SearchParameter parameter) {

        String field = parameter.field();
        Operator op = opMapper.apply(parameter.operator());
        String value = parameter.value();

        return new org.thinkbigthings.zdd.server.entity.SearchParameter(field, op, value);
    }
}
