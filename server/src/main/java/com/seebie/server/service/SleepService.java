package com.seebie.server.service;

import com.seebie.server.dto.SleepData;
import com.seebie.server.dto.SleepDataPoint;
import com.seebie.server.dto.SleepDataWithId;
import com.seebie.server.mapper.dtotoentity.RowToSleepData;
import com.seebie.server.mapper.dtotoentity.TagMapper;
import com.seebie.server.mapper.dtotoentity.UnsavedSleepListMapper;
import com.seebie.server.mapper.entitytodto.SleepDataToRow;
import com.seebie.server.mapper.entitytodto.SleepMapper;
import com.seebie.server.repository.SleepRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.util.List;

import static com.seebie.server.Functional.uncheck;

@Service
public class SleepService {

    private static Logger LOG = LoggerFactory.getLogger(SleepService.class);

    private SleepRepository sleepRepository;
    private UnsavedSleepListMapper entityMapper;
    private TagMapper tagMapper;

    public static final String[] HEADER = new String[] {"Time-Asleep","Time-Awake","Duration-Minutes","Num-Times-Up","Notes"};
    private SleepMapper sleepMapper = new SleepMapper();
    private SleepDataToRow csvMapper = new SleepDataToRow();
    private CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setSkipHeaderRecord(true).setHeader(HEADER).build();

    public SleepService(SleepRepository sleepRepository, TagMapper tagMapper, UnsavedSleepListMapper entityMapper) {
        this.sleepRepository = sleepRepository;
        this.entityMapper = entityMapper;
        this.tagMapper = tagMapper;
    }

    @Transactional(readOnly = true)
    public Page<SleepDataWithId> listSleepData(String username, Pageable page) {
        return sleepRepository.loadSummaries(page, username);
    }

    @Transactional
    public SleepDataWithId saveNew(String username, SleepData dto) {
        var entity = sleepRepository.save(entityMapper.toUnsavedEntity(username, dto));
        return new SleepDataWithId(entity.getId(), sleepMapper.apply(entity));
    }

    @Transactional
    public void remove(String username, Long sleepId) {

        var entity = sleepRepository.findBy(username, sleepId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sleep session not found"));

        sleepRepository.delete(entity);
    }

    @Transactional
    public void update(String username, Long sleepId, SleepData dto) {

        var entity = sleepRepository.findBy(username, sleepId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sleep session not found"));

        entity.setSleepData(dto.outOfBed(), dto.notes(), tagMapper.apply(dto.tags()), dto.startTime(), dto.stopTime());
    }

    @Transactional(readOnly = true)
    public SleepData retrieve(String username, Long sleepId) {

        return sleepRepository.findBy(username, sleepId)
                .map(sleepMapper)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sleep session not found"));
    }

    @Transactional(readOnly = true)
    public List<SleepDataPoint> listChartData(String username, ZonedDateTime from, ZonedDateTime to) {
         return sleepRepository.loadChartData(username, from, to);
    }

    @Transactional(readOnly = true)
    public String exportCsv(String username) {

        StringWriter stringWriter = new StringWriter();

        try (final CSVPrinter printer = new CSVPrinter(stringWriter, csvFormat)) {
            sleepRepository.findAllByUsername(username).stream()
                    .map(csvMapper)
                    .forEach(uncheck((List<String> s) -> printer.printRecord(s)));
        } catch (IOException e) {
            // I think the IOException is just part of the API that in theory could be triggered by the Appendable
            // (which could be to an Appendable File stream) but which in practice would never happen with a StringWriter.
            LOG.error("This should never happen.");
            throw new RuntimeException(e);
        }

        return stringWriter.toString();
    }

    /**
     * This is for test data now, but will eventually be used for bulk import.
     *
     * @param username
     * @param dtoList
     */
    @Transactional
    public void saveNew(String username, List<SleepData> dtoList) {

        var entityList = entityMapper.apply(username, dtoList);
        sleepRepository.saveAll(entityList);
    }

    public long importCsv(String username, String csv) throws IOException {

        var parser = csvFormat.parse(new StringReader(csv));
        var fromCsv = new RowToSleepData();

        var dtoList = parser.stream().map(fromCsv).toList();
        var entityList = entityMapper.apply(username, dtoList);

        long count = sleepRepository.saveAll(entityList).size();

        return count;
//        return csv.lines().count();
    }
}
