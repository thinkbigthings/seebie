package com.seebie.server.controller;

import com.seebie.server.dto.*;
import com.seebie.server.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

// if we use server.servlet.context-path=/api, static content and API all come from the same base
// so we can use that for api-only requests only if the UI is served separately
@RestController
@RequestMapping(path="/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;

    // if there's only one constructor, can omit Autowired and Inject
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/registration")
    public void createUser(@Valid @RequestBody RegistrationRequest newUser) {

        userService.saveNewUser(newUser);
    }

    // This url doesn't actually do anything special,
    // it just provides a convenient endpoint which can be used to go through the authentication process
    // and get the user's details.
    // The logout endpoint is defined in the security configuration
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/login")
    public User login(Authentication auth) {
        return userService.getUserByEmail(auth.getName());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/user")
    public PagedModel<UserSummary> getUsers(@PageableDefault(page = 0, size = 10, sort = {"registrationTime"}, direction=Sort.Direction.DESC) Pageable page) {

        return userService.getUserSummaries(page);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #publicId == authentication.principal.publicId")
    @RequestMapping("/user/{publicId}/personalInfo")
    public User updateUser(@Valid @RequestBody PersonalInfo userData, @PathVariable UUID publicId) {

        return userService.updateUser(publicId, userData);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #publicId == authentication.principal.publicId")
    @PostMapping("/user/{publicId}/password/update")
    public String updatePassword(@Valid @RequestBody PasswordResetRequest resetRequest, @PathVariable UUID publicId) {

        userService.updatePassword(publicId, resetRequest.plainTextPassword());
        return "Password was updated";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #publicId == authentication.principal.publicId")
    @GetMapping("/user/{publicId}")
    public User getUser(@PathVariable UUID publicId) {

        return userService.getUser(publicId);
    }

}