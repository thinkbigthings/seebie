package org.thinkbigthings.boot.assembler;

import org.thinkbigthings.boot.dto.SleepResource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.thinkbigthings.boot.domain.Sleep.DATE_TIME_FORMAT;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;
import org.thinkbigthings.boot.domain.Sleep;
import org.thinkbigthings.boot.web.SleepResourceController;

@Component
@SuppressWarnings({"rawtypes", "unchecked"})
public class SleepResourceAssembler extends ResourceAssemblerSupport<Sleep, SleepResource> {

   public SleepResourceAssembler() {
      super(SleepResourceController.class, SleepResource.class);
   }

   // TODO 3 try available methods inside assembler, such as createResourceWithId(sleep.getId(), sleep);

   
   @Override
   public SleepResource toResource(Sleep sleep)
   {
     String startTime  = DATE_TIME_FORMAT.print(sleep.getEndAsDateTime().minusMinutes(sleep.getAllMinutes()));
     String finishTime = DATE_TIME_FORMAT.print(sleep.getEndAsDateTime());
     SleepResource resource = new SleepResource(startTime, finishTime, sleep.getMinutesNapping(), sleep.getMinutesAwakeInBed(), sleep.getMinutesAwakeNotInBed());
     
     Link selfLink = linkTo(methodOn(SleepResourceController.class).getSleepSession(sleep.getUser().getId(), sleep.getId())).withSelfRel();
     resource.add(selfLink);
     
     return resource;
   }
   
}