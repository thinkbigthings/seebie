package org.thinkbigthings.seebie.android;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

  public static final String DATABASE_NAME = "Seebie.db";
  public static final Integer DATABASE_VERSION = 1;

  // types reference http://www.sqlite.org/datatype3.html
  private static final String TEXT_TYPE = " TEXT";
  private static final String NUMBER_TYPE = " INTEGER";
  private static final String COMMA_SEP = ",";
  private static final String SQL_CREATE_ENTRIES =
      "CREATE TABLE " + DatabaseContract.SleepSession.TABLE_NAME + " (" +
          DatabaseContract.SleepSession._ID + " INTEGER PRIMARY KEY," +
          DatabaseContract.SleepSession.COLUMN_NAME_MINUTES_AWAKE_IN + NUMBER_TYPE + COMMA_SEP +
          DatabaseContract.SleepSession.COLUMN_NAME_MINUTES_AWAKE_OUT + NUMBER_TYPE + COMMA_SEP +
          DatabaseContract.SleepSession.COLUMN_NAME_ALL_MINUTES + NUMBER_TYPE + COMMA_SEP +
          DatabaseContract.SleepSession.COLUMN_NAME_FINISH_TIME + NUMBER_TYPE + COMMA_SEP +
          " )";

  private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DatabaseContract.SleepSession.TABLE_NAME;

  public DatabaseOpenHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }
  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(SQL_CREATE_ENTRIES);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

      // If this database was only a cache for online data, the upgrade policy could be
      // to simply to discard the data and start over

      db.execSQL(SQL_DELETE_ENTRIES);
      onCreate(db);
  }
}
