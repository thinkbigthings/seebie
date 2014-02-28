package org.thinkbigthings.seebie.android;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;

import org.thinkbigthings.sleep.SleepSession;

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

    final int navIndex = getIntent().getIntExtra(IntentKey.NAV_FILTER, 0);

    dao = new GeneralDAO<>(new DatabaseOpenHelper(this));

    SpinnerAdapter navSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array._nav_spinner, android.R.layout.simple_spinner_dropdown_item);

    ActionBar.OnNavigationListener navigationListener = new ActionBar.OnNavigationListener() {
      @Override
      public boolean onNavigationItemSelected(int position, long itemId) {
        if(position == navIndex) {
          return true;
        }
        if(position == 0) {
          Intent intent = new Intent(DailyListingActivity.this, DailyListingActivity.class);
          intent.putExtra(IntentKey.NAV_FILTER, position);
          startActivity(intent);
        }
        if(position == 1) {
          Intent intent = new Intent(DailyListingActivity.this, DailyListingActivity.class);
          intent.putExtra(IntentKey.NAV_FILTER, position);
          startActivity(intent);
        }
        if(position == 2) {
          Intent intent = new Intent(DailyListingActivity.this, DailyListingActivity.class);
          intent.putExtra(IntentKey.NAV_FILTER, position);
          startActivity(intent);
        }
        return true;
      }
    };

    getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
    getActionBar().setListNavigationCallbacks(navSpinnerAdapter, navigationListener);
    getActionBar().setSelectedNavigationItem(navIndex);


    ListView listing = ((ListView) findViewById(R.id.listing));
    listing.setAdapter(new DailyListingAdapter(this, getListingCursor()));
    listing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
