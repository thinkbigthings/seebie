package com.seebie.server.controller;

import com.seebie.server.dto.*;
import com.seebie.server.service.HistogramCalculator;
import com.seebie.server.service.SleepService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.StringReader;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.seebie.server.mapper.dtotoentity.CsvToSleepData.CSV_INPUT;
import static java.nio.charset.StandardCharsets.UTF_8;

@RestController
public class SleepController {

    private static Logger LOG = LoggerFactory.getLogger(SleepController.class);

    private final HistogramCalculator histogramCalculator = new HistogramCalculator();

    private final SleepService sleepService;

    // if there's only one constructor, can omit Autowired and Inject
    public SleepController(SleepService sleepService) {
        this.sleepService = sleepService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/sleep", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void saveSleepSession(@Valid @RequestBody SleepData dto, @PathVariable String username) {

        sleepService.saveNew(username, dto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/sleep", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<SleepDetails> getSleepList(@PathVariable String username, @PageableDefault(page = 0, size = 10, sort = {"stopTime"}, direction=Sort.Direction.DESC) Pageable page) {

        return sleepService.listSleepData(username, page);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/sleep/chart", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<SleepDataPoint> getChartData(@PathVariable String username, @RequestParam ZonedDateTime from, @RequestParam ZonedDateTime to) {

        if(to.isBefore(from)) {
            throw new IllegalArgumentException("Request parameter \"from\" must be before \"to\"");
        }

        LOG.info("Requesting chart data with range " + from + " " + to);

        return sleepService.listChartData(username, from, to);
    }

    /**
     * This does not make any changes to the server, but we're using POST instead of GET so that
     * the request body can be used in a standard way. You shouldn't send a request body with a GET request
     * or at the very least it's somewhat controversial.
     *
     * @param username
     * @param request
     * @return
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/sleep/histogram", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public HistogramNormalized getHistogramData(@RequestBody HistogramRequest request, @PathVariable String username) {


        // TODO manual test, validation test, security test, integration test, unit test


        LOG.info("Requesting histogram data with " + request);

        var dataSets = sleepService.listSleepAmounts(username, request.dataFilters());
        var stackedHistogram = histogramCalculator.calculate(request.binSizeMinutes(), dataSets);

        return stackedHistogram;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/sleep/{sleepId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public SleepData getSleepSession(@PathVariable String username, @PathVariable Long sleepId) {

        return sleepService.retrieve(username, sleepId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/sleep/{sleepId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void updateSleepSession(@Valid @RequestBody SleepData sleepData, @PathVariable String username, @PathVariable Long sleepId) {

        sleepService.update(username, sleepId, sleepData);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/sleep/{sleepId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void delete(@PathVariable String username, @PathVariable Long sleepId) {

        sleepService.remove(username, sleepId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/sleep/download", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<?> downloadSleepData(@PathVariable String username) {

        String filename = "seebie-data-" + username + ".csv";
        String headerValue = "attachment; filename=" + filename;

        String csv = sleepService.retrieveCsv(username);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(csv.getBytes(UTF_8));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/sleep/upload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UploadResponse uploadSleepData(@PathVariable String username, @RequestParam("file") MultipartFile file) throws IOException {

        String rawCsv = new String(file.getBytes(), UTF_8);

        // validate first
        try {
            if(CSV_INPUT.parse(new StringReader(rawCsv)).stream().count() == 0) {
                throw new IllegalArgumentException("No records were present");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not parse CSV", e);
        }

        LOG.info("Upload started...");

        long numImported = sleepService.saveCsv(username, rawCsv);

        LOG.info("Imported " + numImported + " records.");

        return new UploadResponse(numImported, username);
    }
}