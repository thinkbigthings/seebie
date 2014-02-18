package org.thinkbigthings.seebie.android;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import java.text.DecimalFormat;

// TODO figure out how to use betterpickers without the support fragments, would like to use latest fragments

public class SleepSessionEditActivity extends FragmentActivity {

  public final static String SLEEP_SESSION = "org.thinkbigthings.seebie.android.sleepSession";
  public final static String CREATE_OR_UPDATE = "org.thinkbigthings.seebie.android.sleepSession.createOrUpdate";
  public final static String CREATE = "CREATE";

  private NumberPickerDialogFragment.NumberPickerDialogHandler awakeInBedCallback = new NumberPickerDialogFragment.NumberPickerDialogHandler() {
    @Override public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber) {
      currentSession.withMinutesAwakeInBed(number);
      updateDisplay(currentSession, isCreate);
    }
  };
  private NumberPickerDialogFragment.NumberPickerDialogHandler awakeOutOfBedCallback = new NumberPickerDialogFragment.NumberPickerDialogHandler() {
    @Override public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber) {
      currentSession.withMinutesAwakeOutOfBed(number);
      updateDisplay(currentSession, isCreate);
    }
  };
  private RadialTimePickerDialog.OnTimeSetListener startTimeCallback = new RadialTimePickerDialog.OnTimeSetListener() {
    @Override public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
      currentSession.withStartTime(hour, minute);
      updateDisplay(currentSession, isCreate);
    }
  };
  private RadialTimePickerDialog.OnTimeSetListener finishTimeCallback = new RadialTimePickerDialog.OnTimeSetListener() {
    @Override public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
      currentSession.withFinishTime(hour, minute);
      updateDisplay(currentSession, isCreate);
    }
  };

  private View.OnClickListener awakeInBedClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      NumberPickerBuilder npb = new NumberPickerBuilder()
          .setFragmentManager(getSupportFragmentManager())
          .setStyleResId(R.style.BetterPickersDialogFragment)
          .setLabelText(getResources().getString(R.string.minutes))
          .setDecimalVisibility(View.GONE)
          .setPlusMinusVisibility(View.GONE)
          .addNumberPickerDialogHandler(awakeInBedCallback);
      npb.show();
    }
  };
  private View.OnClickListener awakeOutOfBedClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      NumberPickerBuilder npb = new NumberPickerBuilder()
          .setFragmentManager(getSupportFragmentManager())
          .setStyleResId(R.style.BetterPickersDialogFragment)
          .setLabelText(getResources().getString(R.string.minutes))
          .setDecimalVisibility(View.GONE)
          .setPlusMinusVisibility(View.GONE)
          .addNumberPickerDialogHandler(awakeOutOfBedCallback);
      npb.show();
    }
  };
  private View.OnClickListener startTimeClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      LocalTime time= currentSession.getStartTime();
      RadialTimePickerDialog dialog;
      dialog = RadialTimePickerDialog.newInstance(startTimeCallback, time.getHourOfDay(), time.getMinuteOfHour(), false);
      dialog.show(getSupportFragmentManager(), null);
    }
  };

  private static class JodaDatePickerDialog extends CalendarDatePickerDialog {
    public static CalendarDatePickerDialog newInstance(CalendarDatePickerDialog.OnDateSetListener listener, DateTime date) {
      // JodaTime month is one-based, betterpickers is zero-based like Java Calendar
      return CalendarDatePickerDialog.newInstance(listener, date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth());
    }
  };
  private CalendarDatePickerDialog.OnDateSetListener finishDateCallback = new CalendarDatePickerDialog.OnDateSetListener() {
    @Override public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int month, int day) {
      // JodaTime month is one-based, betterpickers is zero-based like Java Calendar
      currentSession.withFinishDate(year, month + 1, day);
      updateDisplay(currentSession, isCreate);
    }
  };
  private View.OnClickListener finishDateClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      DateTime time= currentSession.getFinishTime();
      CalendarDatePickerDialog calendarDatePickerDialog = JodaDatePickerDialog.newInstance(finishDateCallback, time);
      calendarDatePickerDialog.show(getSupportFragmentManager(), null);
    }
  };
  private View.OnClickListener finishTimeClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      DateTime time= currentSession.getFinishTime();
      RadialTimePickerDialog dialog;
      dialog = RadialTimePickerDialog.newInstance(finishTimeCallback, time.getHourOfDay(), time.getMinuteOfHour(), false);
      dialog.show(getSupportFragmentManager(), null);
    }
  };

  private SleepSession currentSession;
  private boolean isCreate;
  private GeneralDAO<SleepSession> dao;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    getActionBar().setDisplayHomeAsUpEnabled(true);
    
    setContentView(R.layout.activity_sleep_session_edit);

    setButtonClickListener(R.id.timeInBedAwake, awakeInBedClickListener);
    setButtonClickListener(R.id.timeOutOfBedAwake, awakeOutOfBedClickListener);
    setButtonClickListener(R.id.startTime, startTimeClickListener);
    setButtonClickListener(R.id.finishDate, finishDateClickListener);
    setButtonClickListener(R.id.finishTime, finishTimeClickListener);

    Intent intent = getIntent();
    currentSession = (SleepSession)intent.getSerializableExtra(SleepSessionEditActivity.SLEEP_SESSION);
    isCreate = CREATE.equals(intent.getStringExtra(SleepSessionEditActivity.CREATE_OR_UPDATE));

    updateDisplay(currentSession, isCreate);

    dao = new GeneralDAO<SleepSession>(new DatabaseOpenHelper(this));
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    dao.close();
  }

  private void setButtonClickListener(int id, View.OnClickListener listener) {
    Button button = (Button) findViewById(id);
    button.setOnClickListener(listener);
  }

  private void updateDisplay(SleepSession session, boolean create) {

    String display;
    SleepSession.Format formatter = new SleepSession.Format();
    Resources res = getResources();

    display = String.format(res.getString(R.string._edit_sleep_timeInBedAwake), session.getMinutesAwakeInBed());
    ((Button) findViewById(R.id.timeInBedAwake)).setText(display);

    display = String.format(res.getString(R.string._edit_sleep_timeOutOfBedAwake), session.getMinutesAwakeOutOfBed());
    ((Button) findViewById(R.id.timeOutOfBedAwake)).setText(display);

    display = String.format(res.getString(R.string._edit_sleep_startTime),  DateTimeFormat.shortTime().print(session.getStartTime()));
    ((Button) findViewById(R.id.startTime)).setText(display);

    display = String.format(res.getString(R.string._edit_sleep_finishTime), DateTimeFormat.shortTime().print(session.getFinishTime()));
    ((Button) findViewById(R.id.finishTime)).setText(display);

    display = String.format(res.getString(R.string._edit_sleep_finishDate), formatter.day(session));
    ((Button) findViewById(R.id.finishDate)).setText(display);

    TextView totalSleepDisplay = (TextView)findViewById(R.id.total_sleep_display);
    display = String.format(res.getString(R.string._edit_sleep_duration), formatter.duration(session));
    totalSleepDisplay.setText(display);

    TextView efficiencyDisplay = (TextView)findViewById(R.id.efficiency_display);
    display = String.format(res.getString(R.string._edit_sleep_efficiency), formatter.efficiency(session));
    efficiencyDisplay.setText(display);

    ((Button) findViewById(R.id.deleteButton)).setVisibility(create ? View.GONE : View.VISIBLE);
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstance) {
    currentSession = (SleepSession)savedInstance.getSerializable(SLEEP_SESSION);
    updateDisplay(currentSession, isCreate);
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstance) {
    savedInstance.putSerializable(SLEEP_SESSION, currentSession);
    savedInstance.putString(CREATE_OR_UPDATE, isCreate ? "CREATE" : "UPDATE");
  }

  public void onDelete(View button) {

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    Resources res = getResources();
    builder.setMessage(res.getString(R.string._delete_this_session))
            .setTitle(res.getString(R.string._confirm))
            .setPositiveButton(res.getString(R.string._delete), new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                deleteCurrentSleepSession();
                Intent intent = new Intent(SleepSessionEditActivity.this, SleepSessionListingActivity.class);
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

  public void onCancel(View button) {
    Intent intent = new Intent(this, SleepSessionListingActivity.class);
    startActivity(intent);
  }

  public void onSave(View button) {
    saveCurrentSleepSession();
    Intent intent = new Intent(this, SleepSessionListingActivity.class);
    startActivity(intent);
  }

  public void deleteCurrentSleepSession() {
    String whereIdEquals = "_ID = ?";
    String[] currentId = new String[]{currentSession.getId().toString()};
    dao.delete(DatabaseContract.SleepSession.TABLE_NAME, whereIdEquals, currentId);
  }

  public void saveCurrentSleepSession() {

    // mapping from domain object to database record
    ContentValues values = new ContentValues();
    values.put(DatabaseContract.SleepSession.COLUMN_NAME_ALL_MINUTES, currentSession.calculateAllMinutes());
    values.put(DatabaseContract.SleepSession.COLUMN_NAME_FINISH_TIME, currentSession.getFinishTime().getMillis());
    values.put(DatabaseContract.SleepSession.COLUMN_NAME_MINUTES_AWAKE_IN, currentSession.getMinutesAwakeInBed());
    values.put(DatabaseContract.SleepSession.COLUMN_NAME_MINUTES_AWAKE_OUT, currentSession.getMinutesAwakeOutOfBed());

    if(isCreate) {
      dao.create(DatabaseContract.SleepSession.TABLE_NAME, values);
    }
    else {
      String whereIdEquals = "_ID = ?";
      String[] currentId = new String[]{currentSession.getId().toString()};
      dao.update(DatabaseContract.SleepSession.TABLE_NAME, values, whereIdEquals, currentId);
    }

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

}
