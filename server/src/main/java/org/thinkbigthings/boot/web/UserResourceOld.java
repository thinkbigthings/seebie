package org.thinkbigthings.boot.web;

import java.io.UnsupportedEncodingException;
//import javax.inject.Inject;
//import javax.validation.Valid;
//import org.containerless.assembler.UserPageResourceAssembler;
//import org.containerless.domain.User;
//import org.containerless.service.UserService;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.hateoas.PagedResources;
//import org.springframework.hateoas.Resource;
//import org.springframework.stereotype.Controller;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;

//@Controller
//@RequestMapping(value = "/resources/user")
public class UserResourceOld
{
//
//    public static final String UTF8 = "UTF-8";
//    private UserService userService;
//    private UserPageResourceAssembler resourceAssembler;
//
//    @Inject
//    public UserResourceOld(UserService us, UserPageResourceAssembler assembler) {
//        userService = us;
//        resourceAssembler = assembler;
//    }
//
//    @RequestMapping(method = RequestMethod.GET, produces = {"application/json"})
//    public @ResponseBody PagedResources<Resource<User>> getUsers(Pageable pageable) {
//        Page<User> users = userService.findUsers(pageable);
//        PagedResources<Resource<User>> resources = resourceAssembler.toResource(users);
//        return resources;
//    }
//
//    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = {"application/json"})
//    public @ResponseBody Resource<User> createUser(@RequestBody @Valid User newUser, BindingResult binding) throws UnsupportedEncodingException {
//        if (binding.hasErrors()) {
//            throw new IllegalArgumentException("Validation failed at " + binding.getNestedPath());
//        }
//        User user = userService.saveNewUser(newUser);
//        Resource<User> resource = resourceAssembler.toResource(user);
//        return resource;
//    }
//
//    @RequestMapping(value = "/current", method = RequestMethod.GET, produces = {"application/json"})
//    public @ResponseBody Resource<User> getCurrentUser() {
//        User user = userService.findUser(1L);
//        Resource<User> resource = resourceAssembler.toResource(user);
//        return resource;
//    }
//
//    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = {"application/json"})
//    public @ResponseBody Resource<User> getUser(@PathVariable final Long id) {
//        User user = userService.findUser(id);
//        Resource<User> resource = resourceAssembler.toResource(user);
//        return resource;
//    }
}
