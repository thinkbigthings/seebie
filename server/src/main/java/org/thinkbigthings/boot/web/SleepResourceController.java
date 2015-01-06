package org.thinkbigthings.boot.web;


import org.thinkbigthings.boot.service.SleepService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.thinkbigthings.boot.assembler.SleepPageResourceAssembler;
import org.thinkbigthings.boot.assembler.SleepAveragesResourceAssembler;
import org.thinkbigthings.boot.domain.Sleep;
import org.thinkbigthings.boot.dto.SleepResource;
import org.thinkbigthings.boot.dto.SleepAveragesResource;
import org.thinkbigthings.sleep.SleepStatisticsCalculator.Group;

@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
@Controller
public class SleepResourceController {

    public static final Sort DEFAULT_AVG_SORT = new Sort(new Order(Direction.DESC, "groupEnding"));
            
    private final SleepService service;
    private final SleepPageResourceAssembler assembler;
    private final SleepAveragesResourceAssembler statsAssembler;
    
    @Inject
    public SleepResourceController(SleepService us, SleepPageResourceAssembler sa, SleepAveragesResourceAssembler ta) {
        service = us;
        assembler = sa;
        statsAssembler = ta;
    }

    @RequestMapping(value = "/user/{userId}/sleepresource", method = POST)
    @PreAuthorize("isAuthenticated() and (principal.id == #userId or hasRole('ADMIN'))")
    public @ResponseBody SleepResource createSleepSession(@PathVariable Long userId, @RequestBody SleepResource sleepData, BindingResult binding) {
        Sleep createdSession = service.createSleepSession(userId, sleepData);
        SleepResource resource = assembler.toResource(createdSession);
        return resource;
    }

    /**
     * This is not consolidated with regular sleepresource url 
     * because an averages resource is actually a little different from a sleep record resource
     * (includes grouping size and count for average, only uses dates)
     * 
     * @param userId
     * @param pageable
     * @param averagesGroup default is WEEK, incoming string is automatically converted to enum
     * @return 
     */
    @RequestMapping(value = "/user/{userId}/sleepresource/averages", method = GET)
    @PreAuthorize("isAuthenticated() and (principal.id == #userId or hasRole('ADMIN'))")
    public @ResponseBody PagedResources<SleepAveragesResource> getSleepAverages(@PathVariable Long userId, 
                                                                                Pageable pageable, 
                                                                                @RequestParam(value="groupSize", defaultValue="WEEK") Group averagesGroup) 
    {
        // enhance Pageable to replace any nulls with defaults
        Optional<Sort> optionalSort = Optional.ofNullable(pageable.getSort());
        Pageable pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), optionalSort.orElse(DEFAULT_AVG_SORT));

        // get actual data and convert to resource
        Page<SleepAveragesResource> page = service.getSleepAverages(userId, pageRequest, averagesGroup);
        PagedResources<SleepAveragesResource> averagesWithLinks = statsAssembler.toResource(page, userId, pageRequest, averagesGroup);
        
        return averagesWithLinks;
    }


    /**
     * A normal query might look like this:
     * 
     * http://localhost:9000/user/15/sleepresource?sort=timeOutOfBed,desc&page=1&size=30"
     * 
     * @param userId
     * @param pageable
     * @return 
     */
    @RequestMapping(value = "/user/{userId}/sleepresource", method = GET)
    @PreAuthorize("isAuthenticated() and (principal.id == #userId or hasRole('ADMIN'))")
    public @ResponseBody PagedResources<SleepResource> getSleepSessions(@PathVariable Long userId, Pageable pageable) 
    {
        Page<Sleep> page = service.getSleepSessions(userId, pageable);
        PagedResources<SleepResource> sleep = assembler.toResource(page);
        return sleep;
    }
    
    @RequestMapping(value = "/user/{userId}/sleepresource/{sleepId}", method = GET)
    @PreAuthorize("isAuthenticated() and (principal.id == #userId or hasRole('ADMIN'))")
    public @ResponseBody SleepResource getSleepSession(@PathVariable Long userId, @PathVariable Long sleepId) {
        Sleep sleep = service.getSleepSession(userId, sleepId);
        SleepResource resource = assembler.toResource(sleep);
        return resource;
    }
    
    @RequestMapping(value = "/user/{userId}/sleepresource/{sleepId}", method = PUT)
    @PreAuthorize("isAuthenticated() and (principal.id == #userId or hasRole('ADMIN'))")
    public @ResponseBody SleepResource updateSleepSession(@PathVariable Long userId, @PathVariable Long sleepId, @RequestBody SleepResource session, BindingResult binding) {
        Sleep updatedSession = service.updateSleepResource(userId, sleepId, session);
        SleepResource resource = assembler.toResource(updatedSession);
        return resource;
    }
    
    @RequestMapping(value = "/user/{userId}/sleepresource/{sleepId}", method = DELETE)
    @PreAuthorize("isAuthenticated() and (principal.id == #userId or hasRole('ADMIN'))")
    public @ResponseBody Boolean deleteSleepSession(@PathVariable Long userId, @PathVariable Long sleepId) {
        return service.deleteSleepSession(sleepId);
    }

    
}
