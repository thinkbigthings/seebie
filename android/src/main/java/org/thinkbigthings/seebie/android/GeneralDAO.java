package org.thinkbigthings.seebie.android;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class GeneralDAO<T> {

  private SQLiteDatabase database;

  public GeneralDAO(SQLiteOpenHelper helper) {

    // TODO call getWritableDatabase() or getReadableDatabase() in a background thread
    // such as with AsyncTask or IntentService.

    // TODO see how to handle database stuff with an Activity
    // http://awiden.wordpress.com/2010/03/26/database-mangement-and-the-activity-lifecycle/

    database = helper.getWritableDatabase();
  }

  public void close() {
    database.close();
  }

  public void create(String tableName, ContentValues values) {
    database.beginTransaction();
    try {
      database.insert(tableName, null, values);
      database.setTransactionSuccessful();
    }
    catch(Exception ex) {
      String cause = ex.getMessage();
      throw ex;
    }
    finally {
      database.endTransaction();
    }
  }

  public List<T> read(CursorReader<T> reader, String tableName, String[] columns, String sortOrder, long limit) {
    List<T> results = new ArrayList<T>();
    database.beginTransaction();
    try {
      Cursor cursor = database.query(tableName, columns, null, null, null, null, sortOrder, String.valueOf(limit));
      if(cursor.moveToFirst()) {
        do {
          results.add(reader.read(cursor));
        } while(cursor.moveToNext());
      }

      database.setTransactionSuccessful();
    }
    catch(Exception ex) {
      String cause = ex.getMessage();
      throw ex;
    }
    finally {
      database.endTransaction();
    }

    return results;
  }

  public void update(String tableName, ContentValues values, String where, String[] params) {
    database.beginTransaction();
    try {
      database.update(tableName, values, where, params);
      database.setTransactionSuccessful();
    }
    catch(Exception ex) {
      String cause = ex.getMessage();
      throw ex;
    }
    finally {
      database.endTransaction();
    }
  }

  public void delete(String tableName, String where, String[] params) {
    database.beginTransaction();
    try {
      database.delete(DatabaseContract.SleepSession.TABLE_NAME, where, params);
      database.setTransactionSuccessful();
    }
    catch (Exception ex) {
      String cause = ex.getMessage();
      throw ex;
    }
    finally {
      database.endTransaction();
    }
  }

  public interface CursorReader<T> {
    public T read(Cursor cursor);
  }
}
