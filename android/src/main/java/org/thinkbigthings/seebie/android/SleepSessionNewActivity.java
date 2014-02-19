package org.thinkbigthings.seebie.android;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SleepSessionNewActivity extends SleepSessionEditActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sleep_session_new);

  }

  public void onCancel(View button) {
    Intent intent = new Intent(this, DailyListingActivity.class);
    startActivity(intent);
  }

  public void onSave(View button) {
    saveCurrentSleepSession();
    Intent intent = new Intent(this, DailyListingActivity.class);
    startActivity(intent);
  }

  public void saveCurrentSleepSession() {

    // mapping from domain object to database record
    ContentValues values = new ContentValues();
    values.put(DatabaseContract.SleepSession.COLUMN_NAME_ALL_MINUTES, currentSession.calculateAllMinutes());
    values.put(DatabaseContract.SleepSession.COLUMN_NAME_FINISH_TIME, currentSession.getFinishTime().getMillis());
    values.put(DatabaseContract.SleepSession.COLUMN_NAME_MINUTES_AWAKE_IN, currentSession.getMinutesAwakeInBed());
    values.put(DatabaseContract.SleepSession.COLUMN_NAME_MINUTES_AWAKE_OUT, currentSession.getMinutesAwakeOutOfBed());

    dao.create(DatabaseContract.SleepSession.TABLE_NAME, values);
  }

}
