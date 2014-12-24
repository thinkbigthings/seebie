package org.thinkbigthings.boot.assembler;

import org.thinkbigthings.boot.dto.UserResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;
import org.thinkbigthings.boot.domain.User;
import org.thinkbigthings.boot.web.UserController;

@Component
@SuppressWarnings({"rawtypes", "unchecked"})
public class UserResourceAssembler extends ResourceAssemblerSupport<User, UserResource> {

    public UserResourceAssembler() {
        super(UserController.class, UserResource.class);
    }

    @Override
    public UserResource toResource(User user) {

        // other links are defined inside UserResource, and based on the selfLink
        Link selfLink = ControllerLinkBuilder.linkTo(UserController.class).slash(user).withSelfRel();
        UserResource resource = new UserResource(user.getUsername(), user.getDisplayName(), selfLink);
        return resource;
    }

}
