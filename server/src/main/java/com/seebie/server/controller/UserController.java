package com.seebie.server.controller;

import com.seebie.server.dto.PersonalInfo;
import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.dto.User;
import com.seebie.server.dto.UserSummary;
import com.seebie.server.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
public class UserController {

    private static Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    // if there's only one constructor, can omit Autowired and Inject
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/registration", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void createUser(@Valid @RequestBody RegistrationRequest newUser) {

        userService.saveNewUser(newUser);
    }

    // This url doesn't actually do anything special,
    // it just provides a convenient endpoint which can be used to go through the authentication process
    // The url "/logout" is configured by spring security, so it's not mapped in this controller
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value="/login", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public User login(Authentication auth) {

        // login and logout logging should use the same object so we can tie them together
        LOG.info("Logged in auth: " + auth);

        return userService.loginUser(auth.getName());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<UserSummary> getUsers(@PageableDefault(page = 0, size = 10, sort = {"registrationTime"}, direction=Sort.Direction.DESC) Pageable page) {

        return userService.getUserSummaries(page);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/personalInfo", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public User updateUser(@Valid @RequestBody PersonalInfo userData, @PathVariable String username) {

        return userService.updateUser(username, userData);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/password/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void updatePassword(@RequestBody String newPassword, @PathVariable String username, @AuthenticationPrincipal UserDetails user) {

        userService.updatePassword(username, newPassword);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public User getUser(@PathVariable String username) {

        return userService.getUser(username);
    }

}