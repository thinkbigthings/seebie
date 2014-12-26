package org.thinkbigthings.boot.web;


import org.thinkbigthings.boot.service.SleepService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.thinkbigthings.boot.dto.SleepResource.DATE_FORMAT;

import java.util.Date;
import javax.validation.Valid;
import org.joda.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.thinkbigthings.boot.assembler.SleepPageResourceAssembler;
import org.thinkbigthings.boot.domain.Sleep;
import org.thinkbigthings.boot.dto.SleepResource;

@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
@Controller
public class SleepResourceController {

    private final SleepService service;
    private final SleepPageResourceAssembler assembler;
    
    @Inject
    public SleepResourceController(SleepService us, SleepPageResourceAssembler sa) {
        service = us;
        assembler = sa;
    }

    @RequestMapping(value = "/user/{userId}/sleepresource", method = POST)
    @PreAuthorize("isAuthenticated() and (principal.id == #userId or hasRole('ADMIN'))")
    public @ResponseBody SleepResource createSleepSession(@PathVariable Long userId, @RequestBody @Valid SleepResource sleepData, BindingResult binding) {
        if (binding.hasErrors()) {
            throw new InvalidRequestBodyException(binding);
        }
        Sleep createdSession = service.createSleepSession(userId, sleepData);
        SleepResource resource = assembler.toResource(createdSession);
        return resource;
    }
    
    // TODO 0 enable resource for pages of averages
    // start with averages resource, 
    // - add parameter for averages group sizes: DAY, WEEK, MONTH, YEAR, ALL (day is just same as regular results)
    // may be able to consolidate with regular sleepresource url
    // default is just daily for latest page


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
    public @ResponseBody SleepResource updateSleepSession(@PathVariable Long userId, @PathVariable Long sleepId, @RequestBody @Valid SleepResource session, BindingResult binding) {
        if (binding.hasErrors()) {
         throw new InvalidRequestBodyException(binding);
        }
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
