package org.thinkbigthings.boot.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thinkbigthings.boot.domain.User;
import org.thinkbigthings.boot.dto.UserResource;
import org.thinkbigthings.boot.dto.UserRegistration;
import org.thinkbigthings.boot.service.UserService;

import javax.inject.Inject;
import javax.validation.Valid;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;

import org.thinkbigthings.boot.assembler.UserPageResourceAssembler;

@Controller
@RequestMapping(value = "/user", produces = {MediaType.APPLICATION_JSON_VALUE})
public class UserController {

    // TODO 2 inject loggers, could have one logger and inject with standard CDI, 
    // or use @Autowired-like logger annotation, with optional setter for unit tests
    // http://java.dzone.com/articles/inject-slf4j-logger-annotation
    private Logger log = LoggerFactory.getLogger(SleepResourceController.class);

    private final UserService service;
    private final UserPageResourceAssembler resourceAssembler;
    
    @Inject
    public UserController(UserService us, UserPageResourceAssembler assembler) {
      service = us;
      resourceAssembler = assembler;
    }

    // TODO 2 (Security) escape incoming inputs
    // HtmlUtils.htmlEscape(input) 
    // http://stackoverflow.com/questions/2147958/how-do-i-prevent-people-from-doing-xss-in-java
    // also see apache commons lang StringEscapeUtils
    // maybe make my own @SanitizedRequestBody or @SanitizedRequestParam
    // also see @NoHtml and NoHtmlValidator in my blog post
    @RequestMapping(value = "/register", method = POST)
    public @ResponseBody UserResource register(@RequestBody @Valid UserRegistration registration, BindingResult binding) {
      if (binding.hasErrors()) {
         throw new InvalidRequestBodyException(binding);
      }
      
      log.info("Registering user: " + registration.getUserName());
      
      User persisted = service.registerNewUser(registration);
      return resourceAssembler.toResource(persisted);
    }

    /**
     * 
     * @param pageable requests and responses are both zero-based.
     * If request is made without paging parameters, default is page 0, size 20. 
     * This default can be overridden with custom argument resolver, or annotations on the method argument.
     * 
     * @return resource of users
     */
    @RequestMapping(method = GET)
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody PagedResources<UserResource> getUsers(Pageable pageable) {
      PagedResources<UserResource> users = resourceAssembler.toResource(service.getUsers(pageable));
      return users;
    }
    
    @RequestMapping(value = "/current", method = GET)
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody UserResource getCurrentUser(@AuthenticationPrincipal User currentUser) {
      User current = service.getUserById(currentUser.getId());
      return resourceAssembler.toResource(current);
    }
    
    @RequestMapping(value = "/{id}", method = GET)
    @PreAuthorize("isAuthenticated() and (principal.id == #id or hasRole('ADMIN'))")
    public @ResponseBody UserResource getUser(@PathVariable Long id) {
        
      // can return as HttpEntity or ResponseEntity if need more control over response headers or response code
      User current = service.getUserById(id);
      return resourceAssembler.toResource(current);
    }

    
    /**
     * update user password, this should be a separate operation from regular update
     * 
     * @param id
     * @param plainTextPassword
     * @return 
     */
    @RequestMapping(value = "/{id}/password", method = PUT)
    @PreAuthorize("isAuthenticated() and (principal.id == #id or hasRole('ADMIN'))")
    public @ResponseBody UserResource updatePassword(@PathVariable Long id, @RequestBody String plainTextPassword) {

      User updated = service.updateUserPassword(id, plainTextPassword);
      UserResource updatedResource = resourceAssembler.toResource(updated);
      
      // removes authentication information for current auth, subsequent calls should re-authenticate with the new username
      SecurityContextHolder.getContext().setAuthentication(null);
      
      return updatedResource;
    }
    
    @RequestMapping(value = "/{id}", method = PUT)
    @PreAuthorize("isAuthenticated() and (principal.id == #id or hasRole('ADMIN'))")
    public @ResponseBody UserResource update(@PathVariable Long id, @RequestBody @Valid UserResource user, BindingResult binding) {
        
      if (binding.hasErrors()) {
         throw new InvalidRequestBodyException(binding);
      }
      
      User updated = service.updateUser(id, user);
      UserResource updatedResource = resourceAssembler.toResource(updated);
      
      // removes authentication information for current auth, subsequent calls should re-authenticate with the new username
      SecurityContextHolder.getContext().setAuthentication(null);
      
      return updatedResource;
      
    }

}