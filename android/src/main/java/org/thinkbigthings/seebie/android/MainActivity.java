package org.thinkbigthings.seebie.android;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.doomonafireball.betterpickers.datepicker.DatePickerBuilder;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.doomonafireball.betterpickers.timepicker.TimePickerBuilder;
import com.doomonafireball.betterpickers.timepicker.TimePickerDialogFragment;
import com.google.common.base.Optional;
import com.google.common.base.Strings;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;

import org.joda.time.DateTime;

// TODO figure out how to use betterpickers without the support fragments, would like to use latest fragments
public class MainActivity extends FragmentActivity implements CalendarDatePickerDialog.OnDateSetListener, RadialTimePickerDialog.OnTimeSetListener {

  public final static String SLEEP_SESSION = "org.thinkbigthings.seebie.android.sleepSession";

  private TimeCalculator calculator = new TimeCalculator();

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    TimePicker picker1 = (TimePicker) findViewById(R.id.timePicker);
    picker1.setCurrentHour(21);
    picker1.setCurrentMinute(30);

    TimePicker picker2 = (TimePicker) findViewById(R.id.timePicker2);
    picker2.setCurrentHour(4);
    picker2.setCurrentMinute(30);

    Button button = (Button) findViewById(R.id.button);

    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        RadialTimePickerDialog dialog;
        dialog = RadialTimePickerDialog.newInstance(MainActivity.this, 21, 30, false);
        dialog.show(getSupportFragmentManager(), null);

//        DateTime now = DateTime.now();
//        CalendarDatePickerDialog calendarDatePickerDialog;
//        calendarDatePickerDialog = CalendarDatePickerDialog
//            .newInstance(MainActivity.this, now.getYear(), now.getMonthOfYear(), now.getDayOfMonth());
//        calendarDatePickerDialog.show(getSupportFragmentManager(), "fragment_date_picker_name");
      }
    });

  }
  @Override
  public void onTimeSet(RadialPickerLayout radialPickerLayout, int i, int i2) {

  }
  @Override
  public void onDateSet(CalendarDatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
    String text = "Year: " + year + "\nMonth: " + monthOfYear + "\nDay: " + dayOfMonth;
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

  // for button click
  public void sendMessage(View view) {

    TimePicker picker1 = (TimePicker) findViewById(R.id.timePicker);
    TimePicker picker2 = (TimePicker) findViewById(R.id.timePicker2);

    int allMinutes = calculator.getMinutesBetween(picker1.getCurrentHour(),
        picker1.getCurrentMinute(),
        picker2.getCurrentHour(),
        picker2.getCurrentMinute());

    EditText awakeInBed = (EditText) findViewById(R.id.awake_in_minutes);
    EditText awakeOutBed = (EditText) findViewById(R.id.awake_out_minutes);

    String s1 = awakeInBed.getText().length() > 0 ? awakeInBed.getText().toString() : "0";
    String s2 = awakeOutBed.getText().length() > 0 ? awakeOutBed.getText().toString() : "0";
    int minutesAwakeInBed = Integer.parseInt(s1);
    int minutesAwakeOutOfBed = Integer.parseInt(s2);

    SleepSession session = new SleepSession(allMinutes, minutesAwakeInBed, minutesAwakeOutOfBed);

    Intent intent = new Intent(this, DisplayMessageActivity.class);
    intent.putExtra(SLEEP_SESSION, session);
    startActivity(intent);
  }


}
