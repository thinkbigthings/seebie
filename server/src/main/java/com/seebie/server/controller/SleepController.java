package com.seebie.server.controller;

import com.seebie.server.dto.*;
import com.seebie.server.service.HistogramCalculator;
import com.seebie.server.service.SleepService;
import com.seebie.server.validation.ValidDurations;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

// if we use server.servlet.context-path=/api, static content and API all come from the same base
// so we can use that for api-only requests only if the UI is served separately
@RestController
@RequestMapping("/api")
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
    public void saveSleepSession(@Valid @ValidDurations @RequestBody SleepData dto, @PathVariable String username) {

        sleepService.saveNew(username, dto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/sleep", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PagedModel<SleepDetails> getSleepList(@PathVariable String username, @PageableDefault(page = 0, size = 10, sort = {"stopTime"}, direction=Sort.Direction.DESC) Pageable page) {

        return sleepService.listSleepData(username, page);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/sleep/chart", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<SleepDataPoint> getChartData(@PathVariable String username, @RequestParam LocalDate from, @RequestParam LocalDate to) {

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
    public StackedHistograms getHistogramData(@Valid @RequestBody HistogramRequest request, @PathVariable String username) {

        LOG.info("Requesting histogram data with " + request);

        var listsSleepAmounts = sleepService.listSleepAmounts(username, request.filters().dataFilters());
        return histogramCalculator.buildNormalizedHistogram(request.binSize(), listsSleepAmounts);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/sleep/{sleepId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public SleepDetails getSleepSession(@PathVariable String username, @PathVariable Long sleepId) {
        return sleepService.retrieve(username, sleepId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/sleep/{sleepId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void updateSleepSession(@Valid @ValidDurations @RequestBody SleepData sleepData, @PathVariable String username, @PathVariable Long sleepId) {
        sleepService.update(username, sleepId, sleepData);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/sleep/{sleepId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void delete(@PathVariable String username, @PathVariable Long sleepId) {
        sleepService.remove(username, sleepId);
    }

}