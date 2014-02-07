package org.thinkbigthings.seebie.android;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.common.base.Optional;

import java.text.DecimalFormat;

public class DisplayMessageActivity extends Activity {

  @SuppressLint("NewApi")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_display_message);

    getActionBar().setDisplayHomeAsUpEnabled(true);

    Intent intent = getIntent();

    SleepSession session = (SleepSession)intent.getSerializableExtra(MainActivity.SLEEP_SESSION);

    TextView totalSleepDisplay = (TextView)findViewById(R.id.total_sleep_display);
    TextView efficiencyDisplay = (TextView)findViewById(R.id.efficiency_display);

    int time = session.calculateMinutesInBedSleeping();

    DecimalFormat format = new DecimalFormat("#.##");

    totalSleepDisplay.setText("Sleep Time: " + time + " m, (" + format.format((double)time/60d) + " hrs)");
    efficiencyDisplay.setText("Efficiency: " + format.format(session.calculateEfficiency()*100) + "%");
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
