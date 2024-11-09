package com.seebie.server.mapper.entitytodto;

import com.seebie.server.dto.SleepDetails;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static com.seebie.server.function.Functional.uncheck;
import static java.util.stream.Collectors.joining;

@Component
public class SleepDetailsToCsv implements Function<List<SleepDetails>, String> {

    public enum HEADER {
        TIME_ASLEEP, TIME_AWAKE, TIMEZONE, MINUTES_ASLEEP, MINUTES_AWAKE, NOTES
    }

    public static final CSVFormat CSV_OUTPUT = CSVFormat.RFC4180.builder()
                                                                .setHeader(HEADER.class)
                                                                .build();

    public static String headerRow() {
        return Arrays.stream(CSV_OUTPUT.getHeader()).collect(joining(","));
    }

    private final SleepDetailsToCsvRow toCsvRow = new SleepDetailsToCsvRow();


    @Override
    public String apply(List<SleepDetails> data) {
        return uncheck(this::internalApply).apply(data);
    }

    private String internalApply(List<SleepDetails> data) throws IOException {

        // The IOException is just part of the API that in theory could be triggered by the Appendable
        // (which could be to an Appendable File stream) but which in practice would never happen with a StringWriter.

        StringWriter stringWriter = new StringWriter();

        try (final CSVPrinter printer = new CSVPrinter(stringWriter, CSV_OUTPUT)) {
            data.stream()
                    .map(toCsvRow)
                    .forEach(uncheck((List<String> s) -> printer.printRecord(s)));
        }

        return stringWriter.toString();
    }

}
