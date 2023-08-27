package com.seebie.server.validation;

import com.seebie.server.dto.FilterList;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class OrderedDatesValidator implements ConstraintValidator<OrderedDates, FilterList> {

    @Override
    public void initialize(OrderedDates value) {
    }

    @Override
    public boolean isValid(FilterList value, ConstraintValidatorContext cxt) {
        return value.dataFilters().stream().allMatch(range -> range.from().isBefore(range.to()));
    }
}
