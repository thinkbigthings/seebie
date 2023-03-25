package com.seebie.server.service;

import com.seebie.server.dto.SleepData;
import com.seebie.server.dto.SleepDataPoint;
import com.seebie.server.dto.SleepDataWithId;
import com.seebie.server.entity.SleepSession;
import com.seebie.server.mapper.dtotoentity.TagMapper;
import com.seebie.server.mapper.dtotoentity.UnsavedSleepListMapper;
import com.seebie.server.mapper.entitytodto.SleepCsvMapper;
import com.seebie.server.mapper.entitytodto.SleepMapper;
import com.seebie.server.repository.SleepRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.util.List;

import static com.seebie.server.Functional.uncheck;

@Service
public class SleepService {

    private SleepRepository sleepRepository;
    private UnsavedSleepListMapper entityMapper;
    private TagMapper tagMapper;

    private SleepMapper sleepMapper = new SleepMapper();
    private SleepCsvMapper csvMapper = new SleepCsvMapper();

    public SleepService(SleepRepository sleepRepository, TagMapper tagMapper, UnsavedSleepListMapper entityMapper) throws IOException {
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

    /**
     * This is for test data now, but will eventually be used for bulk import.
     *
     * @param username
     * @param dtos
     */
    @Transactional
    public void saveNew(String username, List<SleepData> dtos) {

        sleepRepository.saveAll(entityMapper.apply(username, dtos));
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

    public String exportCsv(String username) {

       CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader("Time-Asleep", "Time-Awake", "Duration-Minutes", "Notes")
                .build();

        StringWriter sw = new StringWriter();

        try (final CSVPrinter printer = new CSVPrinter(sw, csvFormat)) {
            sleepRepository.findAllByUsername(username).stream()
                    .map(csvMapper)
                    .forEach(uncheck((String[] s) -> printer.printRecord(s)));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return sw.toString();
    }
}
