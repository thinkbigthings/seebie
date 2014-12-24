package org.thinkbigthings.boot.assembler;

import org.thinkbigthings.boot.dto.UserResource;
import java.util.List;
import javax.inject.Inject;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;
import org.thinkbigthings.boot.domain.User;
import org.thinkbigthings.boot.web.UserController;

@SuppressWarnings({"unchecked", "rawtypes"})
@Component
public class UserPageResourceAssembler extends ResourceAssemblerSupport<Page<User>, PagedResources> {

    protected UserResourceAssembler userResourceAssembler;
    protected PagingLinkCreator pagingLink;
    
    @Inject
    public UserPageResourceAssembler(UserResourceAssembler userAssembler, PagingLinkCreator creator) {
        super(UserController.class, PagedResources.class);
        userResourceAssembler = userAssembler;
        pagingLink = creator;
        
    }

    @Override
    public PagedResources<UserResource> toResource(Page<User> page) {
        String baseUrl = ControllerLinkBuilder.linkTo(UserController.class).toString();
        List<UserResource> resourceList = userResourceAssembler.toResources(page.getContent());
        List<Link> pagingLinks = pagingLink.createPagingLinks(baseUrl, page);
        PagedResources.PageMetadata meta = pagingLink.createMeta(page);
                
        return new PagedResources(resourceList, meta, pagingLinks);
    }

    public UserResource toResource(User user) {
        return userResourceAssembler.toResource(user);
    }

}
