package com.seebie.server.controller;

import com.seebie.server.dto.SleepData;
import com.seebie.server.dto.SleepDataWithId;
import com.seebie.server.service.SleepService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public void saveSleepSession(@RequestBody SleepData dto, @PathVariable String username) {

        sleepService.saveNew(username, dto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/sleep", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<SleepDataWithId> getSleepList(@PathVariable String username, @PageableDefault(page = 0, size = 10, sort = {"dateAwakened"}, direction=Sort.Direction.DESC) Pageable page) {
        return sleepService.listSleepData(username, page);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/sleep/{sleepId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void updateSleepSession(@RequestBody SleepData sleepData, @PathVariable String username, @PathVariable Long sleepId) {

        sleepService.update(username, sleepId, sleepData);
    }

}