package org.thinkbigthings.seebie.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class SleepSessionListingActivity extends Activity {

  private GeneralDAO<SleepSession> dao;

  @SuppressLint("NewApi")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_sleep_session_listing);

    dao = new GeneralDAO<SleepSession>(new DatabaseOpenHelper(this));

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
    sessionButton.setGravity(Gravity.LEFT);;
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

    // Define a projection that specifies which columns from the database
    // you will actually use after this query.
    String[] columns = {
        DatabaseContract.SleepSession._ID,
        DatabaseContract.SleepSession.COLUMN_NAME_FINISH_TIME,
        DatabaseContract.SleepSession.COLUMN_NAME_ALL_MINUTES,
        DatabaseContract.SleepSession.COLUMN_NAME_MINUTES_AWAKE_IN,
        DatabaseContract.SleepSession.COLUMN_NAME_MINUTES_AWAKE_OUT
    };

    GeneralDAO.CursorReader<SleepSession> reader = new GeneralDAO.CursorReader() {
      @Override
      public SleepSession read(Cursor cursor) {
        Long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.SleepSession._ID));
        Long ft = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.SleepSession.COLUMN_NAME_FINISH_TIME));
        Long am = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.SleepSession.COLUMN_NAME_ALL_MINUTES));
        Long ai = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.SleepSession.COLUMN_NAME_MINUTES_AWAKE_IN));
        Long ao = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.SleepSession.COLUMN_NAME_MINUTES_AWAKE_OUT));
        return new SleepSession(id, ft, am, ai, ao);
      }
    };
    // How you want the results sorted in the resulting Cursor
    String sortOrder = DatabaseContract.SleepSession.COLUMN_NAME_FINISH_TIME + " DESC";
    List<SleepSession> sessions = dao.read(reader, DatabaseContract.SleepSession.TABLE_NAME, columns, sortOrder, 7L);

    return sessions;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    dao.close();
  }

  public void onNewSleepSessionClick() {
    Intent intent = new Intent(this, SleepSessionEditActivity.class);
    intent.putExtra(SleepSessionEditActivity.SLEEP_SESSION, new SleepSession());
    intent.putExtra(SleepSessionEditActivity.CREATE_OR_UPDATE, SleepSessionEditActivity.CREATE);
    startActivity(intent);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu items for use in the action bar
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case R.id.action_new:
        onNewSleepSessionClick();
        return true;
      case R.id.action_settings:
//        openSettings();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

}
