package org.thinkbigthings.seebie.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;

import org.joda.time.DateTime;

// TODO figure out how to use betterpickers without the support fragments, would like to use latest fragments

public class MainActivity extends FragmentActivity {

  public final static String SLEEP_SESSION = "org.thinkbigthings.seebie.android.sleepSession";

  private TimeCalculator calculator = new TimeCalculator();
  private TimedSleepSession currentSession = new TimedSleepSession();

  private RadialTimePickerDialog.OnTimeSetListener startTimeListener = new RadialTimePickerDialog.OnTimeSetListener() {
    @Override public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
      currentSession.withStartTime(hour, minute);
    }
  };
  private RadialTimePickerDialog.OnTimeSetListener finishTimeListener = new RadialTimePickerDialog.OnTimeSetListener() {
    @Override public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
      currentSession.withFinishTime(hour, minute);
    }
  };
  private View.OnClickListener startClick = new View.OnClickListener() {
    @Override public void onClick(View v) {
      DateTime time= currentSession.getStartTime();
      RadialTimePickerDialog dialog;
      dialog = RadialTimePickerDialog.newInstance(startTimeListener, time.getHourOfDay(), time.getMinuteOfHour(), false);
      dialog.show(getSupportFragmentManager(), null);
    }
  };
  private View.OnClickListener finishClick = new View.OnClickListener() {
    @Override public void onClick(View v) {
      DateTime time= currentSession.getFinishTime();
      RadialTimePickerDialog dialog;
      dialog = RadialTimePickerDialog.newInstance(finishTimeListener, time.getHourOfDay(), time.getMinuteOfHour(), false);
      dialog.show(getSupportFragmentManager(), null);
    }
  };
  private CalendarDatePickerDialog.OnDateSetListener startDateListener = new CalendarDatePickerDialog.OnDateSetListener() {
    @Override public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int month, int day) {
      currentSession.withStartDate(year, month, day);
    }
  };

  private CalendarDatePickerDialog.OnDateSetListener finishDateListener = new CalendarDatePickerDialog.OnDateSetListener() {
    @Override public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int month, int day) {
      currentSession.withFinishDate(year, month, day);
    }
  };
  private View.OnClickListener startDateClick = new View.OnClickListener() {
    @Override public void onClick(View v) {
      DateTime time= currentSession.getStartTime();
      CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
            .newInstance(startDateListener, time.getYear(), time.getMonthOfYear(), time.getDayOfMonth());
        calendarDatePickerDialog.show(getSupportFragmentManager(), null);
    }
  };
  private View.OnClickListener finishDateClick = new View.OnClickListener() {
    @Override public void onClick(View v) {
      DateTime time= currentSession.getFinishTime();
      CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
          .newInstance(finishDateListener, time.getYear(), time.getMonthOfYear(), time.getDayOfMonth());
      calendarDatePickerDialog.show(getSupportFragmentManager(), null);
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Button startButton = (Button) findViewById(R.id.startTime);
    startButton.setOnClickListener(startClick);

    Button finishButton = (Button) findViewById(R.id.finishTime);
    finishButton.setOnClickListener(finishClick);

    Button startDateButton = (Button) findViewById(R.id.startDate);
    startDateButton.setOnClickListener(startDateClick);

    Button finishDateButton = (Button) findViewById(R.id.finishDate);
   finishDateButton.setOnClickListener(finishDateClick);
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstance) {
    currentSession = (TimedSleepSession)savedInstance.getSerializable(SLEEP_SESSION);
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

    EditText awakeInBed = (EditText) findViewById(R.id.awake_in_minutes);
    EditText awakeOutBed = (EditText) findViewById(R.id.awake_out_minutes);
    String s1 = awakeInBed.getText().length() > 0 ? awakeInBed.getText().toString() : "0";
    String s2 = awakeOutBed.getText().length() > 0 ? awakeOutBed.getText().toString() : "0";

    currentSession.withMinutesAwakeInBed(Long.parseLong(s1)).withMinutesAwakeOutOfBed(Long.parseLong(s2));

    Intent intent = new Intent(this, DisplayMessageActivity.class);
    intent.putExtra(SLEEP_SESSION, currentSession);
    startActivity(intent);
  }


}
