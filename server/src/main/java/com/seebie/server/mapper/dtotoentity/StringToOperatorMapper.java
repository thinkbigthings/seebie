package com.seebie.server.mapper.dtotoentity;

import com.seebie.server.entity.Operator;

import java.util.function.Function;

public class StringToOperatorMapper implements Function<String, Operator> {

    @Override
    public Operator apply(String op) {
        return switch(op) {
            case "<"  -> Operator.LT;
            case "<=" -> Operator.LTE;
            case "="  -> Operator.EQ;
            case ">=" -> Operator.GTE;
            case ">"  -> Operator.GT;
            default   -> throw new IllegalArgumentException("Operator string not recognized: " + op);
        };
    }
}
