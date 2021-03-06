package org.thinkbigthings.seebie.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import org.joda.time.format.DateTimeFormat;
import org.thinkbigthings.sleep.SleepSession;
import org.thinkbigthings.sleep.SleepSessionFormat;

public class DailyDetailActivity extends FragmentActivity {


  private SleepSession currentSession;
  private GeneralDAO<SleepSession> dao;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    getActionBar().setDisplayHomeAsUpEnabled(true);
    
    setContentView(R.layout.activity_daily_detail);

    Intent intent = getIntent();
    currentSession = (SleepSession)intent.getSerializableExtra(IntentKey.SLEEP_SESSION);

    updateDisplay(currentSession);

    dao = new GeneralDAO<SleepSession>(new DatabaseOpenHelper(this));
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    dao.close();
  }

  private void updateDisplay(SleepSession session) {

    String display;
    SleepSessionFormat formatter = new SleepSessionFormat();
    Resources res = getResources();

    display = String.format(res.getString(R.string._edit_sleep_timeInBedAwake), session.getMinutesAwakeInBed());
    ((TextView) findViewById(R.id.timeInBedAwake)).setText(display);

    display = String.format(res.getString(R.string._edit_sleep_timeOutOfBedAwake), session.getMinutesAwakeOutOfBed());
    ((TextView) findViewById(R.id.timeOutOfBedAwake)).setText(display);

    display = String.format(res.getString(R.string._edit_sleep_startTime),  DateTimeFormat.shortTime().print(session.getStartTime()));
    ((TextView) findViewById(R.id.startTime)).setText(display);

    display = String.format(res.getString(R.string._edit_sleep_finishTime), DateTimeFormat.shortTime().print(session.getFinishTime()));
    ((TextView) findViewById(R.id.finishTime)).setText(display);

    display = String.format(res.getString(R.string._edit_sleep_finishDate), formatter.day(session));
    ((TextView) findViewById(R.id.finishDate)).setText(display);

    TextView totalSleepDisplay = (TextView)findViewById(R.id.total_sleep_display);
    String[] hrMin = formatter.duration(session);
    display = String.format(res.getString(R.string._edit_sleep_duration), hrMin[0], hrMin[1]);
    totalSleepDisplay.setText(display);

    TextView efficiencyDisplay = (TextView)findViewById(R.id.efficiency_display);
    display = String.format(res.getString(R.string._edit_sleep_efficiency), formatter.efficiency(session));
    efficiencyDisplay.setText(display);
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstance) {
    currentSession = (SleepSession)savedInstance.getSerializable(IntentKey.SLEEP_SESSION);
    updateDisplay(currentSession);
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstance) {
    savedInstance.putSerializable(IntentKey.SLEEP_SESSION, currentSession);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu items for use in the action bar
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.edit_delete, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.action_edit) {
      onEdit();
      return true;
    }
    if (id == R.id.action_delete) {
      onDelete();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public void onEdit() {
    Intent intent = new Intent(DailyDetailActivity.this, SleepSessionEditActivityWithFragment.class);
    intent.putExtra(IntentKey.SLEEP_SESSION, currentSession);
    startActivity(intent);
  }

  public void onDelete() {

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    Resources res = getResources();
    builder.setMessage(res.getString(R.string._delete_this_session))
        .setTitle(res.getString(R.string._confirm))
        .setPositiveButton(res.getString(R.string._delete), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            deleteCurrentSleepSession();
            startActivity(new Intent(DailyDetailActivity.this, DailyListingActivity.class));
          }
        })
        .setNegativeButton(res.getString(R.string._cancel), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
          }
        });

    AlertDialog dialog = builder.create();
    dialog.show();

  }

  public void deleteCurrentSleepSession() {
    String whereIdEquals = "_ID = ?";
    String[] currentId = new String[]{currentSession.getId().toString()};
    dao.delete(DatabaseContract.SleepSession.TABLE_NAME, whereIdEquals, currentId);
  }
}


