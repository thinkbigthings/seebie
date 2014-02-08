package org.thinkbigthings.seebie.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

// TODO figure out how to use betterpickers without the support fragments, would like to use latest fragments

public class MainActivity extends FragmentActivity {

  public final static String SLEEP_SESSION = "org.thinkbigthings.seebie.android.sleepSession";

  private TimeCalculator calculator = new TimeCalculator();
  private TimedSleepSession currentSession = new TimedSleepSession();

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
  private View.OnClickListener startClick = new View.OnClickListener() {
    @Override public void onClick(View v) {
      DateTime time= currentSession.getStartTime();
      RadialTimePickerDialog dialog;
      dialog = RadialTimePickerDialog.newInstance(startTimeCallback, time.getHourOfDay(), time.getMinuteOfHour(), false);
      dialog.show(getSupportFragmentManager(), null);
    }
  };
  private View.OnClickListener finishClick = new View.OnClickListener() {
    @Override public void onClick(View v) {
      DateTime time= currentSession.getFinishTime();
      RadialTimePickerDialog dialog;
      dialog = RadialTimePickerDialog.newInstance(finishTimeCallback, time.getHourOfDay(), time.getMinuteOfHour(), false);
      dialog.show(getSupportFragmentManager(), null);
    }
  };
  private CalendarDatePickerDialog.OnDateSetListener startDateCallback = new CalendarDatePickerDialog.OnDateSetListener() {
    @Override public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int month, int day) {
      currentSession.withStartDate(year, month, day);
      updateDisplay();
    }
  };
  private CalendarDatePickerDialog.OnDateSetListener finishDateCallback = new CalendarDatePickerDialog.OnDateSetListener() {
    @Override public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int month, int day) {
      currentSession.withFinishDate(year, month, day);
      updateDisplay();
    }
  };
  private View.OnClickListener startDateClick = new View.OnClickListener() {
    @Override public void onClick(View v) {
      DateTime time= currentSession.getStartTime();
      CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
            .newInstance(startDateCallback, time.getYear(), time.getMonthOfYear(), time.getDayOfMonth());
        calendarDatePickerDialog.show(getSupportFragmentManager(), null);
    }
  };
  private View.OnClickListener finishDateClick = new View.OnClickListener() {
    @Override public void onClick(View v) {
      DateTime time= currentSession.getFinishTime();
      CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
          .newInstance(finishDateCallback, time.getYear(), time.getMonthOfYear(), time.getDayOfMonth());
      calendarDatePickerDialog.show(getSupportFragmentManager(), null);
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setButtonClickListener(R.id.timeInBedAwake, awakeInBedClickListener);
    setButtonClickListener(R.id.timeOutOfBedAwake, awakeOutOfBedClickListener);
    setButtonClickListener(R.id.startDate, startDateClick);
    setButtonClickListener(R.id.startTime, startClick);
    setButtonClickListener(R.id.finishDate, finishDateClick);
    setButtonClickListener(R.id.finishTime, finishClick);

    updateDisplay();
  }

  private void setButtonClickListener(int id, View.OnClickListener listener) {
    Button button = (Button) findViewById(id);
    button.setOnClickListener(listener);
  }

  private void updateDisplay() {

    Button awakeInBed = (Button) findViewById(R.id.timeInBedAwake);
    awakeInBed.setText(String.valueOf(currentSession.getMinutesAwakeInBed()));

    Button awakeOutBed = (Button) findViewById(R.id.timeOutOfBedAwake);
    awakeOutBed.setText(String.valueOf(currentSession.getMinutesAwakeOutOfBed()));

    Button startButton = (Button) findViewById(R.id.startTime);
    startButton.setText(DateTimeFormat.shortTime().print(currentSession.getStartTime()));

    Button finishButton = (Button) findViewById(R.id.finishTime);
    finishButton.setText(DateTimeFormat.shortTime().print(currentSession.getFinishTime()));

    Button startDateButton = (Button) findViewById(R.id.startDate);
    startDateButton.setText(DateTimeFormat.shortDate().print(currentSession.getStartTime()));

    Button finishDateButton = (Button) findViewById(R.id.finishDate);
    finishDateButton.setText(DateTimeFormat.shortDate().print(currentSession.getFinishTime()));

  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstance) {
    currentSession = (TimedSleepSession)savedInstance.getSerializable(SLEEP_SESSION);
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

  public void sendMessage(View view) {
    Intent intent = new Intent(this, DisplayMessageActivity.class);
    intent.putExtra(SLEEP_SESSION, currentSession);
    startActivity(intent);
  }


}
