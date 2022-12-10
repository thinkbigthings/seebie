package org.thinkbigthings.zdd.server.mapper.dtotoentity;

import org.thinkbigthings.zdd.server.entity.Operator;

import java.util.function.Function;

import static org.thinkbigthings.zdd.server.entity.Operator.*;

public class StringToOperatorMapper implements Function<String, Operator> {

    @Override
    public Operator apply(String op) {
        return switch(op) {
            case "<"  -> LT;
            case "<=" -> LTE;
            case "="  -> EQ;
            case ">=" -> GTE;
            case ">"  -> GT;
            default   -> throw new IllegalArgumentException("Operator string not recognized: " + op);
        };
    }
}
