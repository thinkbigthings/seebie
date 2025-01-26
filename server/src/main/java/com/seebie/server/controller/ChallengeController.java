package com.seebie.server.controller;

import com.seebie.server.dto.ChallengeDetailDto;
import com.seebie.server.dto.ChallengeDto;
import com.seebie.server.service.ChallengeService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


    @PreAuthorize("hasRole('ROLE_ADMIN') || #publicId == authentication.principal.publicId")
    @RequestMapping(value= "/user/{publicId}/challenge", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void createChallenge(@PathVariable String publicId, @Valid @RequestBody ChallengeDto challenge) {
        challengeService.saveNew(publicId, challenge);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #publicId == authentication.principal.publicId")
    @RequestMapping(value= "/user/{publicId}/challenge/{challengeId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void updateChallenge(@Valid @RequestBody ChallengeDto challengeData, @PathVariable String publicId, @PathVariable Long challengeId) {
        challengeService.update(publicId, challengeId, challengeData);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #publicId == authentication.principal.publicId")
    @RequestMapping(value= "/user/{publicId}/challenge/{challengeId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ChallengeDto getChallenge(@PathVariable String publicId, @PathVariable Long challengeId) {
        return challengeService.retrieve(publicId, challengeId);
    }

    /**
     *
     * @param publicId The user's publicId is used to retrieve their challenges
     * @return
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') || #publicId == authentication.principal.publicId")
    @RequestMapping(value= "/user/{publicId}/challenge", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ChallengeDetailDto> getChallenges(@PathVariable String publicId) {
        return challengeService.getChallenges(publicId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #publicId == authentication.principal.publicId")
    @RequestMapping(value= "/user/{publicId}/challenge/{challengeId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void delete(@PathVariable String publicId, @PathVariable Long challengeId) {
        challengeService.remove(publicId, challengeId);
    }

}