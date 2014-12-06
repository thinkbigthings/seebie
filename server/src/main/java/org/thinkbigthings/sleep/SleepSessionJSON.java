package org.thinkbigthings.sleep;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class SleepSessionJSON implements Serializable {

   public static final long serialVersionUID = 1L;

   public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm a zzz");

   protected Date timeOutOfBed = new Date();
   protected int minutesNapping = 0;
   protected int minutesTotal = 0;
   protected int minutesAwakeInBed = 0;
   protected int minutesAwakeNotInBed = 0;

   // TODO deserialize with constructor instead of setters?
   // http://www.cowtowncoder.com/blog/archives/2010/08/entry_409.html
   // http://stackoverflow.com/questions/15121643/immutable-polymorphic-pojo-json-serialization-with-jackson
   // http://jira.codehaus.org/browse/JACKSON-469
   @JsonCreator
   public SleepSessionJSON( @JsonProperty("timeOutOfBed") String endStr, 
                            @JsonProperty("minutesTotal") int mt, 
                            @JsonProperty("minutesAwakeInBed") int mib, 
                            @JsonProperty("minutesAwakeNotInBed") int mob) 
   {
      this( DATE_TIME_FORMAT.parseDateTime(endStr).toDate(), mt, mib, mob, 0);
   }

   public SleepSessionJSON(Date f, int t, int ib, int ob, int naps) {
      timeOutOfBed = f;
      minutesTotal = t;
      minutesAwakeInBed = ib;
      minutesAwakeNotInBed = ob;
      minutesNapping = naps;
   }

   public String getTimeOutOfBed() {
       return new SimpleDateFormat("yyyy-MM-dd hh:mm a zzz").format(timeOutOfBed);
   }
   
   public int getMinutesNapping() {
       return minutesNapping;
   }
   
   public int getMinutesTotal() {
       return minutesTotal;
   }
   
   public int getMinutesAwakeInBed() {
       return minutesAwakeInBed;
   }
   
   public int getMinutesAwakeNotInBed() {
       return minutesAwakeNotInBed;
   }

}
