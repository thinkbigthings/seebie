package com.seebie.dto;

import java.util.ArrayList;
import java.util.List;

public record ItemSearch(List<SearchParameter> parameters) {

    public ItemSearch() {
        this(List.of());
    }

    public ItemSearch withParameter(String field, String operator, String value) {
        List<SearchParameter> newParameters = new ArrayList<>(parameters);
        newParameters.add(new SearchParameter(field, operator, value));
        return new ItemSearch(List.copyOf(newParameters));
    }
}
