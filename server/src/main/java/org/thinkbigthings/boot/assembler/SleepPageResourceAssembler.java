package org.thinkbigthings.boot.assembler;

import org.thinkbigthings.boot.dto.SleepResource;
import java.util.List;
import javax.inject.Inject;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;
import org.thinkbigthings.boot.domain.Sleep;
import org.thinkbigthings.boot.web.SleepResourceController;

@SuppressWarnings({"unchecked", "rawtypes"})
@Component
public class SleepPageResourceAssembler extends ResourceAssemblerSupport<Page<Sleep>, PagedResources> {

    private SleepResourceAssembler sleepResourceAssembler;
    private PagingLinkCreator pagingLink;

    @Inject
    public SleepPageResourceAssembler(SleepResourceAssembler sleepAssembler, PagingLinkCreator creator) {
        super(SleepResourceController.class, PagedResources.class);
        sleepResourceAssembler = sleepAssembler;
        pagingLink = creator;
    }
 
    @Override
    public PagedResources<SleepResource> toResource(Page<Sleep> page) {
        String baseUrl = ControllerLinkBuilder.linkTo(SleepResourceController.class).toString();
        List<SleepResource> resourceList = sleepResourceAssembler.toResources(page.getContent());
        List<Link> pagingLinks = pagingLink.createPagingLinks(baseUrl, page);
        PagedResources.PageMetadata meta = pagingLink.createMeta(page);
                
        return new PagedResources(resourceList, meta, pagingLinks);
    }

    public SleepResource toResource(Sleep user) {
        return sleepResourceAssembler.toResource(user);
    }


}
