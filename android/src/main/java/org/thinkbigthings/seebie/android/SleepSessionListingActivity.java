package org.thinkbigthings.seebie.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

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

    List<SleepSession> sessions = loadSessionsFromDatabase();
    LinearLayout sessionLayout = ((LinearLayout) findViewById(R.id.sessionLayout));
    for(final SleepSession currentSession : sessions) {
      sessionLayout.addView(createSleepSessionButton(currentSession));
    }
  }

  private Button createSleepSessionButton(final SleepSession currentSession) {

    SleepSession.Format format = new SleepSession.Format();
    Button sessionButton= new Button(this);
    sessionButton.setText(format.title(currentSession));
    sessionButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(SleepSessionListingActivity.this, SleepSessionEditActivity.class);
        intent.putExtra(SleepSessionEditActivity.SLEEP_SESSION, currentSession);
        intent.putExtra(SleepSessionEditActivity.CREATE_OR_UPDATE, "UPDATE");
        startActivity(intent);
      }
    });
    return sessionButton;
  }

  private List<SleepSession> loadSessionsFromDatabase() {

    List<SleepSession> sessions= new ArrayList<>();

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

      Cursor cursor = readableDatabase.query(DatabaseContract.SleepSession.TABLE_NAME,
          columns,null, null, null, null, sortOrder, String.valueOf(7));

      if(cursor.moveToFirst()) {
        do {
          Long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.SleepSession._ID));
          Long ft = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.SleepSession.COLUMN_NAME_FINISH_TIME));
          Long am = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.SleepSession.COLUMN_NAME_ALL_MINUTES));
          Long ai = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.SleepSession.COLUMN_NAME_MINUTES_AWAKE_IN));
          Long ao = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.SleepSession.COLUMN_NAME_MINUTES_AWAKE_OUT));
          sessions.add(new SleepSession(id, ft, am, ai, ao));
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

    return sessions;

  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    helper.close();
  }

  public void onNewSleepSessionClick(View button) {
    Intent intent = new Intent(this, SleepSessionEditActivity.class);
    intent.putExtra(SleepSessionEditActivity.SLEEP_SESSION, new SleepSession());
    intent.putExtra(SleepSessionEditActivity.CREATE_OR_UPDATE, SleepSessionEditActivity.CREATE);
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
