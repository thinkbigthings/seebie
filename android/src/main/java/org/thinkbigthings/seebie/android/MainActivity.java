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



// TODO work on a useful app: sleep efficiency calculator to start (or regular calculator)
// http://android-developers.blogspot.com/2011/11/new-layout-widgets-space-and-gridlayout.html
// do I need to save state? what happens when I set time and navigate away and come back?

// TODO try out new time picker
// https://github.com/inteist/android-better-time-picker

// TODO do custom settings activity, copy out of old project, be able to save settings to device

// TODO get pure unit test to work
// Unlike on Eclipse or ADT Bundle, The new Android Studio doesn't require a separate android testing project.
// official docs http://tools.android.com/tech-docs/new-build-system/user-guide#TOC-Testing
// but this is for Android runtime tests
//
// alternative test runner to run android tests
// http://robolectric.org/ and https://github.com/novoda/robolectric-plugin
// http://www.element84.com/easy-testing-with-android-studio.html
// using mockito instead of Android tests ends up with you writing a reverse implementation of the entire Android system
//
// pure unit tests require more work to set up
// https://coderwall.com/p/ybds4w


// TODO read up on the fragment docs, I think this is recommended
// https://developer.android.com/training/basics/fragments/index.html

// TODO save this note about making jodatime more efficient
// http://daniel-codes.blogspot.com/2013/08/joda-times-memory-issue-in-android.html


public class MainActivity extends Activity {

  public final static String EXTRA_MESSAGE = "org.thinkbigthings.seebie.android.MESSAGE";

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

    int minutes = calculator.getMinutesBetween(picker1.getCurrentHour(),
        picker1.getCurrentMinute(),
        picker2.getCurrentHour(),
        picker2.getCurrentMinute());


    TextView minutesDisplay = (TextView) findViewById(R.id.textView);
    minutesDisplay.setText(Integer.toString(minutes));

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
    Intent intent = new Intent(this, DisplayMessageActivity.class);
    EditText editText = (EditText) findViewById(R.id.edit_message);
    String message = editText.getText().toString();
    intent.putExtra(EXTRA_MESSAGE, message);
    startActivity(intent);
  }

}
