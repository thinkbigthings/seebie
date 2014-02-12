package org.thinkbigthings.seebie.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import static org.thinkbigthings.seebie.android.DatabaseContract.*;

public class SleepSessionListingActivity extends Activity {

  private DatabaseOpenHelper helper;
  private SQLiteDatabase readableDatabase;

  @SuppressLint("NewApi")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_sleep_session_listing);

    // TODO call getWritableDatabase() or getReadableDatabase() in a background thread
    // such as with AsyncTask or IntentService.
    helper = new DatabaseOpenHelper(this);
    readableDatabase = helper.getReadableDatabase();

    loadSessionsFromDatabase();

  }

  private void loadSessionsFromDatabase() {

    // Define a projection that specifies which columns from the database
    // you will actually use after this query.
    String[] columns = {
        DatabaseContract.SleepSession._ID,
        DatabaseContract.SleepSession.COLUMN_NAME_FINISH_TIME,
        DatabaseContract.SleepSession.COLUMN_NAME_ALL_MINUTES,
        DatabaseContract.SleepSession.COLUMN_NAME_MINUTES_AWAKE_IN,
        DatabaseContract.SleepSession.COLUMN_NAME_MINUTES_AWAKE_OUT
    };

    // How you want the results sorted in the resulting Cursor
    String sortOrder = DatabaseContract.SleepSession.COLUMN_NAME_FINISH_TIME + " DESC";

    readableDatabase.beginTransaction();
    try {



      Cursor cursor = readableDatabase.query(
          DatabaseContract.SleepSession.TABLE_NAME, // The table to query
          columns,                                  // The columns to return
          null,                                     // The columns for the WHERE clause, null for ALL
          null,                                     // The values for the WHERE clause
          null,                                     // don't group the rows
          null,                                     // don't filter by row groups
          sortOrder                                 // The sort order
      );

      cursor.moveToFirst();
      if(cursor.getCount() > 0) {
        do {
          Long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.SleepSession._ID));
          Long ft = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.SleepSession.COLUMN_NAME_FINISH_TIME));
          Long am = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.SleepSession.COLUMN_NAME_ALL_MINUTES));
          Long ai = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.SleepSession.COLUMN_NAME_MINUTES_AWAKE_IN));
          Long ao = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.SleepSession.COLUMN_NAME_MINUTES_AWAKE_OUT));
          SleepSession found = new SleepSession(id, ft, am, am, ao);
        } while(cursor.moveToNext());
      }

      readableDatabase.setTransactionSuccessful();
    }
    catch(Exception ex) {
      String cause = ex.getMessage();
      throw ex;
    }
    finally {
      readableDatabase.endTransaction();
    }

  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    helper.close();
  }

  public void onNewSleepSessionClick(View button) {
    Intent intent = new Intent(this, SleepSessionEditActivity.class);
    intent.putExtra(SleepSessionEditActivity.SLEEP_SESSION, new SleepSession());
    startActivity(intent);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    // TODO this may be faster as a switch statement

    if (id == R.id.action_settings) {
      return true;
    }
    return super.onOptionsItemSelected(item);

  }

}
