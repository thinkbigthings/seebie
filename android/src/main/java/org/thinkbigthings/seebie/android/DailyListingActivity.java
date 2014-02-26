package org.thinkbigthings.seebie.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.List;

public class DailyListingActivity extends Activity {

  private GeneralDAO<SleepSession> dao;

  private final GeneralDAO.CursorReader<SleepSession> reader = new GeneralDAO.CursorReader<SleepSession>() {
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

  @SuppressLint("NewApi")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_daily_listing);

    dao = new GeneralDAO<>(new DatabaseOpenHelper(this));

    LinearLayout sessionLayout = ((LinearLayout) findViewById(R.id.sessionLayout));

    DailyListingAdapter adapter = new DailyListingAdapter(this, getListingCursor());
    ListView list = new ListView(this);
    list.setAdapter(adapter);
    sessionLayout.addView(list);

    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SleepSession session = dao.findById(id, reader, DatabaseContract.SleepSession.TABLE_NAME, DatabaseContract.SleepSession.ALL_COLUMNS);
        Intent intent = new Intent(DailyListingActivity.this, DailyDetailActivity.class);
        intent.putExtra(IntentKey.SLEEP_SESSION, session);
        startActivity(intent);
      }
    });

  }

  private Cursor getListingCursor() {
    // Define a projection that specifies which columns from the database
    // you will actually use after this query.
    String[] columns = DatabaseContract.SleepSession.ALL_COLUMNS;

    // How you want the results sorted in the resulting Cursor
    String sortOrder = DatabaseContract.SleepSession.COLUMN_NAME_FINISH_TIME + " DESC";
    return dao.createCursor(DatabaseContract.SleepSession.TABLE_NAME, columns, sortOrder, 365L);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    dao.close();
  }

  public void onNewSleepSessionClick() {
    Intent intent = new Intent(this, SleepSessionNewActivityWithFragment.class);
    intent.putExtra(IntentKey.SLEEP_SESSION, new SleepSession());
    startActivity(intent);
  }

  private void openSettings() {
    Intent intent = new Intent(this, SettingsActivity.class);
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
        openSettings();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

}
