package com.seebie.server.controller;

import com.seebie.server.dto.Challenge;
import com.seebie.server.dto.ChallengeList;
import com.seebie.server.dto.ZoneIdConstraint;
import com.seebie.server.service.ChallengeService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;

// if we use server.servlet.context-path=/api, static content and API all come from the same base
// so we can use that for api-only requests only if the UI is served separately
@RestController
@RequestMapping("/api")
public class ChallengeController {

    private final ChallengeService challengeService;

    // if there's only one constructor, can omit Autowired and Inject
    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }


    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/challenge", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void createChallenge(@PathVariable String username, @Valid @RequestBody Challenge challenge) {
        challengeService.saveNewChallenge(username, challenge);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/challenge", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ChallengeList getChallenges(@PathVariable String username, @RequestParam @ZoneIdConstraint String zoneId) {
        LocalDate today = LocalDate.now(ZoneId.of(zoneId));
        return challengeService.getChallenges(username, today);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/challenge/{challengeId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void delete(@PathVariable String username, @PathVariable Long challengeId) {
        challengeService.remove(username, challengeId);
    }

}