package org.thinkbigthings.boot.assembler;

import org.springframework.data.domain.Page;
import org.springframework.hateoas.PagedResources;

@SuppressWarnings("rawtypes")
public class ExtendedPageMetadata extends PagedResources.PageMetadata {

   private boolean firstPage;
   private boolean lastPage;
   private long startElement;
   private long endElement;

   public ExtendedPageMetadata(Page page) {
      super(page.getSize(), page.getNumber(), page.getTotalElements(), page.getTotalPages());
      firstPage = page.getNumber() == 0;
      lastPage = page.getNumber() == (page.getTotalPages() - 1);
      startElement = (page.getNumber() * page.getSize()) + 1;
      endElement = startElement + page.getContent().size() - 1;
   }

   public boolean isFirstPage() {
      return firstPage;
   }

   public boolean isLastPage() {
      return lastPage;
   }

   /** one-based start element of page */
   public long getStartElement() {
      return startElement;
   }

   /** one-based end element of page */
   public long getEndElement() {
      return endElement;
   }
}