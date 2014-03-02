package org.thinkbigthings.seebie.android;

import android.database.Cursor;

import org.thinkbigthings.sleep.SleepSession;

public class CursorReaderSleepSession implements GeneralDAO.CursorReader<SleepSession>{
  @Override
  public SleepSession read(Cursor cursor) {
    Long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.SleepSession._ID));
    Long ft = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.SleepSession.COLUMN_NAME_FINISH_TIME));
    Long am = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.SleepSession.COLUMN_NAME_ALL_MINUTES));
    Long ai = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.SleepSession.COLUMN_NAME_MINUTES_AWAKE_IN));
    Long ao = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.SleepSession.COLUMN_NAME_MINUTES_AWAKE_OUT));
    return new SleepSession(id, ft, am, ai, ao);
  }
}
