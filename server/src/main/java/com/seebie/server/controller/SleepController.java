package com.seebie.server.controller;

import com.seebie.server.dto.SleepData;
import com.seebie.server.dto.SleepDataPoint;
import com.seebie.server.dto.SleepDataWithId;
import com.seebie.server.service.SleepService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
public class SleepController {

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
    public Page<SleepDataWithId> getSleepList(@PathVariable String username, @PageableDefault(page = 0, size = 10, sort = {"stopTime"}, direction=Sort.Direction.DESC) Pageable page) {

        return sleepService.listSleepData(username, page);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/sleep/chart", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<SleepDataPoint> getChartData(@PathVariable String username, @RequestParam ZonedDateTime from, @RequestParam ZonedDateTime to) {

        if(to.isBefore(from)) {
            throw new IllegalArgumentException("Request parameter \"from\" must be before \"to\"");
        }

        return sleepService.listChartData(username, from, to);
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

}