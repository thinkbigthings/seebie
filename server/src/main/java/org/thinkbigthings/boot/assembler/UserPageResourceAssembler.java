package org.thinkbigthings.boot.assembler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

   public static final String PAGING_PREFIX    = "paging";
   public static final String PAGING_SEPARATOR = ".";
   
   private UserResourceAssembler userResourceAssembler;

   @Inject
   public UserPageResourceAssembler(UserResourceAssembler userAssembler) {
      super(UserController.class, PagedResources.class);
      userResourceAssembler = userAssembler;
   }

  @Override
  public PagedResources<Resource<User>> toResource(Page<User> users)
  {
     PagedResources.PageMetadata pageMetadata = new ExtendedPageMetadata(users);
     List<Resource> resourceList = userResourceAssembler.toResources(users.getContent());
     PagedResources<Resource<User>> resources = new PagedResources(resourceList, pageMetadata);
     resources.add(createPagingLinks(users));
     return resources;
  }

  public Resource<User> toResource(User user) {
     return userResourceAssembler.toResource(user);
  }

  private Set<Link> createPagingLinks(Page<User> page) 
  {

     Set<Link> links = new HashSet<>();
     String baseUrl = ControllerLinkBuilder.linkTo(UserController.class).toString();
     String pageNumber = PAGING_PREFIX + PAGING_SEPARATOR + "page";
     String pageSize   = PAGING_PREFIX + PAGING_SEPARATOR + "size";

     // the page object here outputs zero-based page numbers
     // but the requests coming in are one-based, so these urls are constructed with an extra offset
     if(page.hasPrevious()) {
        String url = baseUrl + "?" + pageNumber + "=" + (page.getNumber() + 0) + "&" + pageSize + "=" + page.getSize();
        links.add(new Link(url, Link.REL_PREVIOUS));
     }
     if(page.hasNext()) {
        String url = baseUrl + "?" + pageNumber + "=" + (page.getNumber() + 2) + "&" + pageSize + "=" + page.getSize();
        links.add(new Link(url, Link.REL_NEXT));
     }

     return links;
  }

}
