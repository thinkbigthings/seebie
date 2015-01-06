package org.thinkbigthings.boot.assembler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.Link;

@RunWith(Parameterized.class)
public class PagingLinkCreatorTest {

    private PagingLinkCreator linkCreator = new PagingLinkCreator();
    private static final String URL = "https://www.myapplication.com";

    private static List<String> contents = new ArrayList<>(20);
    private static final int TOTAL = 100;
    private static final Predicate<Link> IS_NEXT = k -> k.getRel().equals(Link.REL_NEXT);
    private static final Predicate<Link> IS_PREV = k -> k.getRel().equals(Link.REL_PREVIOUS);
    private static final Predicate<Link> IS_FIRST = k -> k.getRel().equals(Link.REL_FIRST);

    private boolean expectFirst;
    private boolean expectLast;
    private boolean expectPrev;
    private boolean expectNext;
    private int pageNum;
    private Page<String> page;
    
    @BeforeClass
    public static void setup() {
        String[] data = new String[20];
        Arrays.fill(data, "data");
        contents = Arrays.asList(data);
    }
    
    public PagingLinkCreatorTest(int pageNum, boolean expectFirst, boolean expectLast, boolean expectPrev, boolean expectNext) {
        this.pageNum = pageNum;
        this.expectFirst = expectFirst;
        this.expectLast = expectLast;
        this.expectPrev = expectPrev;
        this.expectNext = expectNext;
        page = new PageImpl(contents, new PageRequest(pageNum, contents.size()), TOTAL);
    }
    
    @Parameters
    public static Collection<Object[]> createPageExpectations() throws Exception {
        
        return Arrays.asList(new Object[][]{
            {0, false, true, false, true},
            {1, true, true, true, true},
            {2, true, true, true, true},
            {3, true, true, true, true},
            {4, true, false, true, false}
        });
    }

    @Test
    public void testFirst() throws Exception {

        List<Link> pagingLinks = linkCreator.createPagingLinks(URL, page);

        assertEquals(expectFirst, pagingLinks.stream().filter(IS_FIRST).findFirst().isPresent());
        
        pagingLinks.stream()
                .filter(IS_FIRST)
                .findFirst()
                .ifPresent(link -> assertTrue(link.getHref().contains("page=0")));

    }
    
    @Test
    public void testPrevious() throws Exception {

        List<Link> pagingLinks = linkCreator.createPagingLinks(URL, page);

        assertEquals(expectPrev, pagingLinks.stream().filter(IS_PREV).findFirst().isPresent());

        pagingLinks.stream()
                .filter(IS_PREV)
                .findFirst()
                .ifPresent(link -> assertTrue(link.getHref().contains("page=" + (pageNum - 1))));
    }
    
    @Test
    public void testNext() throws Exception {

        List<Link> pagingLinks = linkCreator.createPagingLinks(URL, page);

        assertEquals(expectNext, pagingLinks.stream().filter(IS_NEXT).findFirst().isPresent());
        
        pagingLinks.stream()
                .filter(IS_NEXT)
                .findFirst()
                .ifPresent(link -> assertTrue(link.getHref().contains("page=" + (pageNum + 1))));

    }

}
