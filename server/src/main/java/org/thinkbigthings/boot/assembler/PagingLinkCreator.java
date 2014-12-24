package org.thinkbigthings.boot.assembler;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.stereotype.Component;

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
        // TODO 3 add links for first and last, add following those links to user integration test
        if (page.hasPrevious()) {
            String url = baseUrl + "?" + PAGE_NUMBER + "=" + (page.getNumber() - 1) + "&" + PAGE_SIZE + "=" + page.getSize();
            links.add(new Link(url, Link.REL_PREVIOUS));
        }
        if (page.hasNext()) {
            String url = baseUrl + "?" + PAGE_NUMBER + "=" + (page.getNumber() + 1) + "&" + PAGE_SIZE + "=" + page.getSize();
            links.add(new Link(url, Link.REL_NEXT));
        }
        return links;
    }

}
