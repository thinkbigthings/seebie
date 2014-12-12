package org.thinkbigthings.boot.assembler;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;
import org.thinkbigthings.boot.domain.User;
import org.thinkbigthings.boot.web.UserController;

@Component
@SuppressWarnings({"rawtypes", "unchecked"})
public class UserResourceAssembler extends ResourceAssemblerSupport<User, Resource> {

   public UserResourceAssembler() {
      super(UserController.class, Resource.class);
   }

   @Override
   public Resource<User> toResource(User user)
   {
     Link selfLink = ControllerLinkBuilder.linkTo(UserController.class).slash(user).withSelfRel();
     Resource<User> resource = new Resource(user, new Link[] { selfLink } );
     return resource;
   }
   
}