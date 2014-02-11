package org.thinkbigthings.seebie.android;

import android.content.Intent;
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

public class MainActivity extends FragmentActivity {

  public final static String SLEEP_SESSION = "org.thinkbigthings.seebie.android.sleepSession";

  private SleepSession currentSession = new SleepSession();

  private NumberPickerDialogFragment.NumberPickerDialogHandler awakeInBedCallback = new NumberPickerDialogFragment.NumberPickerDialogHandler() {
    @Override public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber) {
      currentSession.withMinutesAwakeInBed(number);
      updateDisplay();
    }
  };
  private NumberPickerDialogFragment.NumberPickerDialogHandler awakeOutOfBedCallback = new NumberPickerDialogFragment.NumberPickerDialogHandler() {
    @Override public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber) {
      currentSession.withMinutesAwakeOutOfBed(number);
      updateDisplay();
    }
  };
  private RadialTimePickerDialog.OnTimeSetListener startTimeCallback = new RadialTimePickerDialog.OnTimeSetListener() {
    @Override public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
      currentSession.withStartTime(hour, minute);
      updateDisplay();
    }
  };
  private RadialTimePickerDialog.OnTimeSetListener finishTimeCallback = new RadialTimePickerDialog.OnTimeSetListener() {
    @Override public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
      currentSession.withFinishTime(hour, minute);
      updateDisplay();
    }
  };
  private CalendarDatePickerDialog.OnDateSetListener finishDateCallback = new CalendarDatePickerDialog.OnDateSetListener() {
    @Override public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int month, int day) {
      currentSession.withFinishDate(year, month, day);
      updateDisplay();
    }
  };
  private View.OnClickListener awakeInBedClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      NumberPickerBuilder npb = new NumberPickerBuilder()
          .setFragmentManager(getSupportFragmentManager())
          .setStyleResId(R.style.BetterPickersDialogFragment)
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
  private View.OnClickListener finishDateClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      DateTime time= currentSession.getFinishTime();
      CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
          .newInstance(finishDateCallback, time.getYear(), time.getMonthOfYear(), time.getDayOfMonth());
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


  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setButtonClickListener(R.id.timeInBedAwake, awakeInBedClickListener);
    setButtonClickListener(R.id.timeOutOfBedAwake, awakeOutOfBedClickListener);
    setButtonClickListener(R.id.startTime, startTimeClickListener);
    setButtonClickListener(R.id.finishDate, finishDateClickListener);
    setButtonClickListener(R.id.finishTime, finishTimeClickListener);

    updateDisplay();
  }

  private void setButtonClickListener(int id, View.OnClickListener listener) {
    Button button = (Button) findViewById(id);
    button.setOnClickListener(listener);
  }

  private void updateDisplay() {

    String display;

    display = "Awake in bed for " + currentSession.getMinutesAwakeInBed() + " minutes";
    ((Button) findViewById(R.id.timeInBedAwake)).setText(display);

    display = "Awake out of bed for " + currentSession.getMinutesAwakeOutOfBed() + " minutes";
    ((Button) findViewById(R.id.timeOutOfBedAwake)).setText(display);

    display = "Got into bed at "+ DateTimeFormat.shortTime().print(currentSession.getStartTime());
    ((Button) findViewById(R.id.startTime)).setText(display);

    display = "Got up for the day at "+ DateTimeFormat.shortTime().print(currentSession.getFinishTime());
    ((Button) findViewById(R.id.finishTime)).setText(display);

    display = "On " + DateTimeFormat.forPattern("EEEE").print(currentSession.getFinishTime())  + " "
                    + DateTimeFormat.shortDate().print(currentSession.getFinishTime());
    ((Button) findViewById(R.id.finishDate)).setText(display);

    TextView totalSleepDisplay = (TextView)findViewById(R.id.total_sleep_display);
    TextView efficiencyDisplay = (TextView)findViewById(R.id.efficiency_display);
    long time = currentSession.calculateMinutesInBedSleeping();
    DecimalFormat format = new DecimalFormat("#.##");
    totalSleepDisplay.setText("Sleep Time: " + time + " m, (" + format.format((double)time/60d) + " hrs)");
    efficiencyDisplay.setText("Efficiency: " + format.format(currentSession.calculateEfficiency()*100) + "%");

  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstance) {
    currentSession = (SleepSession)savedInstance.getSerializable(SLEEP_SESSION);
    updateDisplay();
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstance) {
    savedInstance.putSerializable(SLEEP_SESSION, currentSession);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
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
