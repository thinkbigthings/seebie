package com.seebie.server.controller;

import com.seebie.server.dto.ChallengeDto;
import com.seebie.server.dto.ChallengeList;
import com.seebie.server.service.ChallengeService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
    public void createChallenge(@PathVariable String username, @Valid @RequestBody ChallengeDto challenge) {
        challengeService.saveNew(username, challenge);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/challenge/{challengeId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void updateChallenge(@Valid @RequestBody ChallengeDto challengeData, @PathVariable String username, @PathVariable Long challengeId) {
        challengeService.update(username, challengeId, challengeData);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/challenge/{challengeId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ChallengeDto getChallenge(@PathVariable String username, @PathVariable Long challengeId) {
        return challengeService.retrieve(username, challengeId);
    }

    /**
     *
     * @param username The user's username is used to retrieve their challenges
     * @return
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/challenge", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ChallengeList getChallenges(@PathVariable String username) {
        return challengeService.getChallenges(username, LocalDate.now());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/challenge/{challengeId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void delete(@PathVariable String username, @PathVariable Long challengeId) {
        challengeService.remove(username, challengeId);
    }

}