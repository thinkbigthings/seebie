package org.thinkbigthings.seebie.android;


import android.provider.BaseColumns;

public final class DatabaseContract {

  public DatabaseContract() {}

  public static abstract class SleepSession implements BaseColumns {
    public static final String TABLE_NAME = "sleepSession";
    public static final String COLUMN_NAME_MINUTES_AWAKE_IN = "minutesAwakeInBed";
    public static final String COLUMN_NAME_MINUTES_AWAKE_OUT = "minutesAwakeOutOfBed";
    public static final String COLUMN_NAME_ALL_MINUTES = "allMinutes";
    public static final String COLUMN_NAME_FINISH_TIME = "finishTime";
  }

  // types reference http://www.sqlite.org/datatype3.html
  private static final String TEXT_TYPE = " TEXT";
  private static final String NUMBER_TYPE = " INTEGER";
  private static final String COMMA_SEP = ",";
  private static final String SQL_CREATE_ENTRIES =
      "CREATE TABLE " + SleepSession.TABLE_NAME + " (" +
          SleepSession._ID + " INTEGER PRIMARY KEY," +
          SleepSession.COLUMN_NAME_MINUTES_AWAKE_IN + NUMBER_TYPE + COMMA_SEP +
          SleepSession.COLUMN_NAME_MINUTES_AWAKE_OUT + NUMBER_TYPE + COMMA_SEP +
          SleepSession.COLUMN_NAME_ALL_MINUTES + NUMBER_TYPE + COMMA_SEP +
          SleepSession.COLUMN_NAME_FINISH_TIME + NUMBER_TYPE + COMMA_SEP +
      " )";

  private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + SleepSession.TABLE_NAME;
}
