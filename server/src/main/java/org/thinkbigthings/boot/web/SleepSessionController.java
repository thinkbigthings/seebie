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

import org.thinkbigthings.boot.domain.SleepSession;
import org.thinkbigthings.sleep.SleepSessionJSON;

@Controller
public class SleepSessionController {

    private final SleepServiceInterface service;
    
    @Inject
    public SleepSessionController(SleepServiceInterface us)
    {
      service = us;
    }

    @RequestMapping(value = "/user/{id}/sleep", method = POST, produces = {"application/json"})
    @PreAuthorize("isAuthenticated() and (principal.id == #id or hasRole('ADMIN'))")
    public @ResponseBody SleepSession createSleepSession(@PathVariable Long id, @RequestBody @Valid SleepSessionJSON session, BindingResult binding) {
      if (binding.hasErrors()) {
         throw new InvalidRequestBodyException("Validation of incoming object failed at " + binding.getNestedPath());
      }
      SleepSession createdSession = service.createSleepSession(id, new SleepSession(session));
      return createdSession;
    }
}