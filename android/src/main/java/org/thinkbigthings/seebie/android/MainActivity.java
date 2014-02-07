package org.thinkbigthings.seebie.android;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.common.base.Optional;
import com.google.common.base.Strings;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;

public class MainActivity extends Activity {

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
