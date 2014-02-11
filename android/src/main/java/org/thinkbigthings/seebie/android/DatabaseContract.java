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

}
