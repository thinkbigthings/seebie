package org.thinkbigthings.boot.web;

import org.thinkbigthings.boot.service.SleepServiceInterface;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.util.List;
import org.thinkbigthings.boot.domain.Sleep;
import org.thinkbigthings.sleep.SleepSessionJSON;

@Controller
public class SleepController {

    private final SleepServiceInterface service;

    @Inject
    public SleepController(SleepServiceInterface us) {
        service = us;
    }

    // TODO 0 use spring-data-rest to do this automatically?
    // http://spring.io/guides/gs/accessing-data-rest/
    @RequestMapping(value = "/user/{userId}/sleep", method = POST, produces = {"application/json"})
    @PreAuthorize("isAuthenticated() and (principal.id == #userId or hasRole('ADMIN'))")
    public @ResponseBody Sleep createSleepSession(@PathVariable Long userId, @RequestBody @Valid SleepSessionJSON session, BindingResult binding) {
        if (binding.hasErrors()) {
         throw new InvalidRequestBodyException(binding);
        }
        Sleep createdSession = service.createSleepSession(userId, session);
        return createdSession;
    }
    
    // userId is required in the method signature so it can be bound in the annotation's SPEL expression
    @RequestMapping(value = "/user/{userId}/sleep/{sleepId}", method = GET, produces = {"application/json"})
    @PreAuthorize("isAuthenticated() and (principal.id == #userId or hasRole('ADMIN'))")
    public @ResponseBody Sleep getSleepSession(@PathVariable Long userId, @PathVariable Long sleepId) {
        return service.getSleepSession(sleepId);
    }

    @RequestMapping(value = "/user/{userId}/sleep", method = GET, produces = {"application/json"})
    @PreAuthorize("isAuthenticated() and (principal.id == #userId or hasRole('ADMIN'))")
    public @ResponseBody List<Sleep> getSleepSessions(@PathVariable Long userId) {
        return service.getSleepSessions(userId);
    }

    @RequestMapping(value = "/user/{userId}/sleep/{sleepId}", method = PUT, produces = {"application/json"})
    @PreAuthorize("isAuthenticated() and (principal.id == #userId or hasRole('ADMIN'))")
    public @ResponseBody Sleep updateSleepSession(@PathVariable Long userId, @PathVariable Long sleepId, @RequestBody @Valid SleepSessionJSON session, BindingResult binding) {
        if (binding.hasErrors()) {
         throw new InvalidRequestBodyException(binding);
        }
        Sleep updatedSession = service.updateSleep(userId, sleepId, session);
        return updatedSession;
    }
    
    // userId is required in the signature so it can be bound in the SPEL expression
    @RequestMapping(value = "/user/{userId}/sleep/{sleepId}", method = DELETE, produces = {"application/json"})
    @PreAuthorize("isAuthenticated() and (principal.id == #userId or hasRole('ADMIN'))")
    public @ResponseBody Boolean deleteSleepSession(@PathVariable Long userId, @PathVariable Long sleepId) {
        return service.deleteSleepSession(sleepId);
    }

}
