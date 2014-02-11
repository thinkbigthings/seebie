package org.thinkbigthings.seebie.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class SleepSessionListingActivity extends Activity {

  @SuppressLint("NewApi")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sleep_session_listing);
  }

  public void onNewSleepSessionClick(View button) {
    Intent intent = new Intent(this, SleepSessionEditActivity.class);
    intent.putExtra(SleepSessionEditActivity.SLEEP_SESSION, new SleepSession());
    startActivity(intent);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    // TODO this may be faster as a switch statement

    if (id == R.id.action_settings) {
      return true;
    }
    return super.onOptionsItemSelected(item);

  }

}
