package org.thinkbigthings.boot.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thinkbigthings.boot.domain.User;
import org.thinkbigthings.boot.service.UserServiceInterface;

import javax.inject.Inject;
import javax.validation.Valid;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedResources;
import org.springframework.security.core.context.SecurityContextHolder;
import org.thinkbigthings.boot.assembler.Resource;
import org.thinkbigthings.boot.assembler.UserPageResourceAssembler;

@Controller
@RequestMapping(value = "/user")
public class UserController {

    private final UserServiceInterface service;
    private final UserPageResourceAssembler resourceAssembler;
    
    @Inject
    public UserController(UserServiceInterface us, UserPageResourceAssembler assembler) {
      service = us;
      resourceAssembler = assembler;
    }

    // TODO 3 escape incoming inputs
    // HtmlUtils.htmlEscape(input) 
    // http://stackoverflow.com/questions/2147958/how-do-i-prevent-people-from-doing-xss-in-java
    // also see apache commons lang StringEscapeUtils
    // maybe make my own @SanitizedRequestBody or @SanitizedRequestParam
    // also see @NoHtml and NoHtmlValidator in my blog post
    @RequestMapping(value = "/register", method = POST, produces = {"application/json"})
    public @ResponseBody Resource<User> register(@RequestBody @Valid User newUser, BindingResult binding) {
      if (binding.hasErrors()) {
         throw new InvalidRequestBodyException(binding);
      }
      User persisted = service.registerNewUser(newUser);
      return resourceAssembler.toResource(persisted);
    }
    
    // TODO 0 get paging working
    // - int test various page sizes and start/end points
    // - test 0 vs 1 based index and document. Do I need custom web argument resolver? custom extended page meta?
    // - figure out nicer way to send url parameters, maybe user resttemplate method, or custruct 
    // - set fallback pageable (default if I don't define it is first page, page size 20) or document the defaults and how to override
    // - http://stackoverflow.com/questions/19756786/spring-pageableargumentresolver-deprecated-how-to-use-pageablehandlermethodargu
        
    
    @RequestMapping(value = "/all", method = GET, produces = {"application/json"})
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody PagedResources<Resource<User>> getUsers(Pageable pageable) {
      PagedResources<Resource<User>> users = resourceAssembler.toResource(service.getUsers(pageable));
      return users;
    }

    
    // TODO 1 allow for sorting.
    
    
    @RequestMapping(value = "/current", method = GET, produces = {"application/json"})
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody Resource<User> getCurrentUser(@AuthenticationPrincipal User currentUser) {
      User current = service.getUserById(currentUser.getId());
      return resourceAssembler.toResource(current);
    }
    
    @RequestMapping(value = "/{id}", method = GET, produces = {"application/json"})
    @PreAuthorize("isAuthenticated() and (principal.id == #id or hasRole('ADMIN'))")
    public @ResponseBody Resource<User> getUser(@PathVariable Long id) {
        
      // TODO 1 user should be returned as dto: field objects/collections should be links
      // can return as HttpEntity or ResponseEntity if need more control over response headers or response code
      User current = service.getUserById(id);
      return resourceAssembler.toResource(current);
    }

    @RequestMapping(value = "/{id}", method = PUT, produces = {"application/json"})
    @PreAuthorize("isAuthenticated() and (principal.id == #id or hasRole('ADMIN'))")
    public @ResponseBody Resource<User> update(@PathVariable Long id, @RequestBody @Valid User user, BindingResult binding) {
      if (binding.hasErrors()) {
         throw new InvalidRequestBodyException(binding);
      }
      if( ! id.equals(user.getId())) {
         throw new InvalidRequestBodyException(binding);
      }

      // removes authentication information for current auth, subsequent calls should re-authenticate with the new username
      SecurityContextHolder.getContext().setAuthentication(null);
      
      User updated = service.updateUser(user);
      return resourceAssembler.toResource(updated);
    }

}