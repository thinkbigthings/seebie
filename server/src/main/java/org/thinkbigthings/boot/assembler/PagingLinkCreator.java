package org.thinkbigthings.boot.assembler;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@SuppressWarnings({"unchecked", "rawtypes"})
@Component
public class PagingLinkCreator {

    public static final String PAGE_NUMBER = "page";
    public static final String PAGE_SIZE = "size";

    public PagedResources.PageMetadata createMeta(Page page) {
        return new PagedResources.PageMetadata(page.getSize(), page.getNumber(), page.getTotalElements(), page.getTotalPages());
    }
    
    public List<Link> createPagingLinks(String baseUrl, Page page) {
        
        List<Link> links = new ArrayList<>();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl).replaceQueryParam(PAGE_SIZE, page.getSize());

        if (!page.isFirst()) {
            String url = builder.replaceQueryParam(PAGE_NUMBER, 0).toUriString();
            links.add(new Link(url, Link.REL_FIRST));
        }
        if (!page.isLast()) {
            String url = builder.replaceQueryParam(PAGE_NUMBER, page.getTotalPages()-1).toUriString();
            links.add(new Link(url, Link.REL_LAST));
        }
        if (page.hasPrevious()) {
            String url = builder.replaceQueryParam(PAGE_NUMBER, (page.getNumber() - 1)).toUriString();
            links.add(new Link(url, Link.REL_PREVIOUS));
        }
        if (page.hasNext()) {
            String url = builder.replaceQueryParam(PAGE_NUMBER, (page.getNumber() + 1)).toUriString();
            links.add(new Link(url, Link.REL_NEXT));
        }
        
        return links;
    }

}
