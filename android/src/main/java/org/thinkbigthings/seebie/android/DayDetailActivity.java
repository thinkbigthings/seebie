package org.thinkbigthings.seebie.android;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

public class DayDetailActivity extends FragmentActivity {


  private SleepSession currentSession;
  private GeneralDAO<SleepSession> dao;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    getActionBar().setDisplayHomeAsUpEnabled(true);
    
    setContentView(R.layout.activity_day_detail);

    Intent intent = getIntent();
    currentSession = (SleepSession)intent.getSerializableExtra(SleepSessionEditActivity.SLEEP_SESSION);

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
    SleepSession.Format formatter = new SleepSession.Format();
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
    display = String.format(res.getString(R.string._edit_sleep_duration), formatter.duration(session));
    totalSleepDisplay.setText(display);

    TextView efficiencyDisplay = (TextView)findViewById(R.id.efficiency_display);
    display = String.format(res.getString(R.string._edit_sleep_efficiency), formatter.efficiency(session));
    efficiencyDisplay.setText(display);
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstance) {
    currentSession = (SleepSession)savedInstance.getSerializable(SleepSessionEditActivity.SLEEP_SESSION);
    updateDisplay(currentSession);
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstance) {
    savedInstance.putSerializable(SleepSessionEditActivity.SLEEP_SESSION, currentSession);
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
    Intent intent = new Intent(DayDetailActivity.this, SleepSessionEditActivity.class);
    intent.putExtra(SleepSessionEditActivity.SLEEP_SESSION, currentSession);
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
            Intent intent = new Intent(DayDetailActivity.this, SleepSessionListingActivity.class);
            startActivity(intent);
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


