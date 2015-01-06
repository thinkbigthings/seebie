package org.thinkbigthings.boot.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;
import javax.inject.Inject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;
import org.thinkbigthings.boot.dto.SleepAveragesResource;
import org.thinkbigthings.boot.web.SleepResourceController;
import org.thinkbigthings.sleep.SleepStatisticsCalculator;

@SuppressWarnings({"unchecked", "rawtypes"})
@Component
public class SleepAveragesResourceAssembler extends ResourceAssemblerSupport<Page<SleepAveragesResource>, PagedResources> {

    private PagingLinkCreator pagingLink;

    @Inject
    public SleepAveragesResourceAssembler(PagingLinkCreator creator) {
        super(SleepResourceController.class, PagedResources.class);
        pagingLink = creator;
    }
 
   public PagedResources<SleepAveragesResource> toResource(Page<SleepAveragesResource> page, Long userId, Pageable p, SleepStatisticsCalculator.Group averages) {
        Link currentPageLink = linkTo(methodOn(SleepResourceController.class).getSleepAverages(userId, p, averages)).withSelfRel();
        String baseUrl = currentPageLink.getHref();
        List<Link> pagingLinks = pagingLink.createPagingLinks(baseUrl, page);
        PagedResources.PageMetadata meta = pagingLink.createMeta(page);
        return new PagedResources(page.getContent(), meta, pagingLinks);    
   }
        
    @Override
    public PagedResources<SleepAveragesResource> toResource(Page<SleepAveragesResource> page) {
        String baseUrl = ControllerLinkBuilder.linkTo(SleepResourceController.class).toString();
        List<SleepAveragesResource> resourceList = page.getContent();
        List<Link> pagingLinks = pagingLink.createPagingLinks(baseUrl, page);
        PagedResources.PageMetadata meta = pagingLink.createMeta(page);
        return new PagedResources(resourceList, meta, pagingLinks);
    }

}
