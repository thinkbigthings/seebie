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

import com.google.common.base.Optional;

// TODO go through getting started tutorial again
// also check first project for notes

// TODO work on a useful app: sleep efficiency calculator to start (or regular calculator)
// follow up with grid layout
// http://android-developers.blogspot.com/2011/11/new-layout-widgets-space-and-gridlayout.html

// ActionBar API requires API level 11

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

  @Override
  protected void onCreate(Bundle savedInstanceState) {


    // Using Java 7, can't use try-with-resources for min sdk < 19, but other Java 7 features are fine
    Optional<String> optionalString = Optional.of("some string");

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

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
