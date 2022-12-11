package com.seebie.server.mapper.entitytodto;

import com.seebie.server.entity.Operator;

import java.util.function.Function;

public class OperatorToStringMapper implements Function<Operator, String> {

    @Override
    public String apply(Operator op) {
        return switch(op) {
            case LT  -> "<";
            case LTE -> "<=";
            case EQ  -> "=";
            case GTE -> ">=";
            case GT  -> ">";
        };
    }
}
