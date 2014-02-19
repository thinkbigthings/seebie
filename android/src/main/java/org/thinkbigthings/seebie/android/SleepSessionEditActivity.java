package org.thinkbigthings.seebie.android;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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

// TODO figure out how to use betterpickers without the support fragments, would like to use latest fragments

public class SleepSessionEditActivity extends FragmentActivity {

  public final static String SLEEP_SESSION = "org.thinkbigthings.seebie.android.sleepSession";

  protected NumberPickerDialogFragment.NumberPickerDialogHandler awakeInBedCallback = new NumberPickerDialogFragment.NumberPickerDialogHandler() {
    @Override public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber) {
      currentSession.withMinutesAwakeInBed(number);
      updateDisplay(currentSession);
    }
  };
  protected NumberPickerDialogFragment.NumberPickerDialogHandler awakeOutOfBedCallback = new NumberPickerDialogFragment.NumberPickerDialogHandler() {
    @Override public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber) {
      currentSession.withMinutesAwakeOutOfBed(number);
      updateDisplay(currentSession);
    }
  };
  protected RadialTimePickerDialog.OnTimeSetListener startTimeCallback = new RadialTimePickerDialog.OnTimeSetListener() {
    @Override public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
      currentSession.withStartTime(hour, minute);
      updateDisplay(currentSession);
    }
  };
  protected RadialTimePickerDialog.OnTimeSetListener finishTimeCallback = new RadialTimePickerDialog.OnTimeSetListener() {
    @Override public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
      currentSession.withFinishTime(hour, minute);
      updateDisplay(currentSession);
    }
  };

  public void onAwakeInBedClick(View view) {
    NumberPickerBuilder npb = new NumberPickerBuilder()
        .setFragmentManager(getSupportFragmentManager())
        .setStyleResId(R.style.BetterPickersDialogFragment)
        .setLabelText(getResources().getString(R.string.minutes))
        .setDecimalVisibility(View.GONE)
        .setPlusMinusVisibility(View.GONE)
        .addNumberPickerDialogHandler(awakeInBedCallback);
    npb.show();
  }

  public void onAwakeOutOfBedClick(View v) {
    NumberPickerBuilder npb = new NumberPickerBuilder()
        .setFragmentManager(getSupportFragmentManager())
        .setStyleResId(R.style.BetterPickersDialogFragment)
        .setLabelText(getResources().getString(R.string.minutes))
        .setDecimalVisibility(View.GONE)
        .setPlusMinusVisibility(View.GONE)
        .addNumberPickerDialogHandler(awakeOutOfBedCallback);
    npb.show();
  }

  public void onStartTimeClick(View view) {
    LocalTime time= currentSession.getStartTime();
    RadialTimePickerDialog dialog;
    dialog = RadialTimePickerDialog.newInstance(startTimeCallback, time.getHourOfDay(), time.getMinuteOfHour(), false);
    dialog.show(getSupportFragmentManager(), null);
    }

  protected static class JodaDatePickerDialog extends CalendarDatePickerDialog {
    public static CalendarDatePickerDialog newInstance(CalendarDatePickerDialog.OnDateSetListener listener, DateTime date) {
      // JodaTime month is one-based, betterpickers is zero-based like Java Calendar
      return CalendarDatePickerDialog.newInstance(listener, date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth());
    }
  };

  protected CalendarDatePickerDialog.OnDateSetListener finishDateCallback = new CalendarDatePickerDialog.OnDateSetListener() {
    @Override public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int month, int day) {
      // JodaTime month is one-based, betterpickers is zero-based like Java Calendar
      currentSession.withFinishDate(year, month + 1, day);
      updateDisplay(currentSession);
    }
  };
  public void onFinishDateClick(View view) {
    DateTime time= currentSession.getFinishTime();
    CalendarDatePickerDialog calendarDatePickerDialog = JodaDatePickerDialog.newInstance(finishDateCallback, time);
    calendarDatePickerDialog.show(getSupportFragmentManager(), null);
  }

  public void onFinishTimeClick(View view){
    DateTime time= currentSession.getFinishTime();
    RadialTimePickerDialog dialog;
    dialog = RadialTimePickerDialog.newInstance(finishTimeCallback, time.getHourOfDay(), time.getMinuteOfHour(), false);
    dialog.show(getSupportFragmentManager(), null);
  }

  protected SleepSession currentSession;
  protected GeneralDAO<SleepSession> dao;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    getActionBar().setDisplayHomeAsUpEnabled(true);
    
    setContentView(R.layout.activity_sleep_session_edit);

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
    String[] hrMin = formatter.duration(session);
    display = String.format(res.getString(R.string._edit_sleep_duration), hrMin[0], hrMin[1]);
    totalSleepDisplay.setText(display);

    TextView efficiencyDisplay = (TextView)findViewById(R.id.efficiency_display);
    display = String.format(res.getString(R.string._edit_sleep_efficiency), formatter.efficiency(session));
    efficiencyDisplay.setText(display);
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstance) {
    currentSession = (SleepSession)savedInstance.getSerializable(SLEEP_SESSION);
    updateDisplay(currentSession);
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstance) {
    savedInstance.putSerializable(SLEEP_SESSION, currentSession);
  }

  public void onCancel(View button) {
    Intent intent = new Intent(this, DailyDetailActivity.class);
    intent.putExtra(SleepSessionEditActivity.SLEEP_SESSION, currentSession);
    startActivity(intent);
  }

  public void onSave(View button) {
    saveCurrentSleepSession();
    Intent intent = new Intent(this, DailyDetailActivity.class);
    intent.putExtra(SleepSessionEditActivity.SLEEP_SESSION, currentSession);

    startActivity(intent);
  }

  public void saveCurrentSleepSession() {

    // mapping from domain object to database record
    ContentValues values = new ContentValues();
    values.put(DatabaseContract.SleepSession.COLUMN_NAME_ALL_MINUTES, currentSession.calculateAllMinutes());
    values.put(DatabaseContract.SleepSession.COLUMN_NAME_FINISH_TIME, currentSession.getFinishTime().getMillis());
    values.put(DatabaseContract.SleepSession.COLUMN_NAME_MINUTES_AWAKE_IN, currentSession.getMinutesAwakeInBed());
    values.put(DatabaseContract.SleepSession.COLUMN_NAME_MINUTES_AWAKE_OUT, currentSession.getMinutesAwakeOutOfBed());

    String whereIdEquals = "_ID = ?";
    String[] currentId = new String[]{currentSession.getId().toString()};
    dao.update(DatabaseContract.SleepSession.TABLE_NAME, values, whereIdEquals, currentId);
  }

}
