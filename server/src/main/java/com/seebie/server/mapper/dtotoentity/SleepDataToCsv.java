package com.seebie.server.mapper.dtotoentity;

import com.seebie.server.dto.SleepData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.seebie.server.Functional.uncheck;
import static com.seebie.server.mapper.entitytodto.ZonedDateTimeToString.format;

@Component
public class SleepDataToCsv implements Function<List<SleepData>, String> {

    private static Logger LOG = LoggerFactory.getLogger(SleepDataToCsv.class);

//    public static final String[] HEADER = new String[] {"Time-Asleep","Time-Awake","Duration-Minutes","Num-Times-Up","Notes"};
    public enum HEADER {
        TIME_ASLEEP, TIME_AWAKE, DURATION_MINUTES, MINUTES_AWAKE, NOTES
    }

    public static final CSVFormat CSV_OUTPUT = CSVFormat.RFC4180.builder()
                                                                .setHeader(HEADER.class)
                                                                .build();

    public static String headerRow() {
        return Arrays.asList(CSV_OUTPUT.getHeader()).stream().collect(Collectors.joining(","));
    }


    @Override
    public String apply(List<SleepData> data) {

        StringWriter stringWriter = new StringWriter();


        try (final CSVPrinter printer = new CSVPrinter(stringWriter, CSV_OUTPUT)) {
                    data.stream()
                        .map(this::toCsvRow)
                        .forEach(uncheck((List<String> s) -> printer.printRecord(s)));
        }
        catch (IOException e) {
            // I think the IOException is just part of the API that in theory could be triggered by the Appendable
            // (which could be to an Appendable File stream) but which in practice would never happen with a StringWriter.
            LOG.error("This should never happen.");
            throw new RuntimeException(e);
        }

        return stringWriter.toString();
    }

    private List<String> toCsvRow(SleepData data) {

        return List.of(
                format(data.startTime()),
                format(data.stopTime()),
                Long.toString(Duration.between(data.startTime(), data.stopTime()).toMinutes()),
                Integer.toString(data.minutesAwake()),
                data.notes()
        );
    }
}
