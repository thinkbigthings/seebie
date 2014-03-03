package org.thinkbigthings.seebie.android;

import android.database.Cursor;

import org.joda.time.DateTimeConstants;
import org.thinkbigthings.sleep.SleepSession;
import org.thinkbigthings.sleep.SleepSessionAverage;

public class CursorReaderAverageWeekly implements GeneralDAO.CursorReader<SleepSessionAverage> {
  private CursorReaderSleepSession singleSessionReader = new CursorReaderSleepSession();

  @Override
  public SleepSessionAverage read(Cursor cursor) {

    // assumes records returned by the Cursor are sorted by date
    // assumes cursor handling is outside this class

    SleepSessionAverage averages = new SleepSessionAverage();
    do {
      if(cursor.isAfterLast()) {
        break;
      }
      SleepSession session = singleSessionReader.read(cursor);
      averages.with(session);
      int monday = DateTimeConstants.MONDAY;
      int currentDay = session.getFinishTime().getDayOfWeek();
      if( currentDay == monday) {
        break;
      }
    }
    while(cursor.moveToNext());

    return averages;
  }
}

