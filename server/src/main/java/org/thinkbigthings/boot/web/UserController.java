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
import java.util.List;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
public class UserController {

    public static final String REGISTRATION_URL = "/register";
    
    private final UserServiceInterface service;
    
    @Inject
    public UserController(UserServiceInterface us)
    {
      service = us;
    }

    // http://docs.spring.io/spring-security/site/docs/3.2.x/reference/htmlsingle/#el-access
    
    @RequestMapping(value = "/user/all", method = GET, produces = {"application/json"})
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody List<User> getUsers() {
      return service.getUsers();
    }

    @RequestMapping(value = "/user/current", method = GET, produces = {"application/json"})
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody User getCurrentUser(@AuthenticationPrincipal User currentUser) {
      return currentUser;
    }
    
    // can also pass as a parameter @AuthenticationPrincipal User currentUser
    @RequestMapping(value = "/user/{id}", method = GET, produces = {"application/json"})
    @PreAuthorize("isAuthenticated() and (principal.id == #id or hasRole('ADMIN'))")
    public @ResponseBody User getUser(@PathVariable Long id) {
      return service.getUserById(id);
    }

    @RequestMapping(value = "/user/{id}", method = PUT, produces = {"application/json"})
    @PreAuthorize("isAuthenticated() and (principal.id == #id or hasRole('ADMIN'))")
    public @ResponseBody User update(@PathVariable Long id, @RequestBody @Valid User user, BindingResult binding) {
      if (binding.hasErrors()) {
         throw new InvalidRequestBodyException("Validation of incoming object failed at " + binding.getNestedPath());
      }
      if( ! id.equals(user.getId())) {
         throw new InvalidRequestBodyException("Request body id must match the url id");
      }
      
      return service.updateUser(user);
    }
    
    @RequestMapping(value = REGISTRATION_URL, method = POST, produces = {"application/json"})
    public @ResponseBody User register(@RequestBody @Valid User newUser, BindingResult binding) {
      if (binding.hasErrors()) {
         throw new InvalidRequestBodyException("Validation of incoming object failed at " + binding.getNestedPath());
      }
      return service.registerNewUser(newUser);
    }
    
}