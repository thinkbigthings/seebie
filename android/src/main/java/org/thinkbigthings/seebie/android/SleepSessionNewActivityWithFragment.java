package org.thinkbigthings.seebie.android;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class SleepSessionNewActivityWithFragment extends FragmentActivity implements HasSleepSession {

  protected SleepSession currentSession;
  protected GeneralDAO<SleepSession> dao;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    getActionBar().setDisplayHomeAsUpEnabled(true);

    setContentView(R.layout.activity_sleep_session_new_with_fragment);

    Intent intent = getIntent();
    currentSession = (SleepSession)intent.getSerializableExtra(IntentKey.SLEEP_SESSION);

    dao = new GeneralDAO<>(new DatabaseOpenHelper(this));
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
  @Override
  public void onDestroy() {
    super.onDestroy();
    dao.close();
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstance) {
    currentSession = (SleepSession)savedInstance.getSerializable(IntentKey.SLEEP_SESSION);
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstance) {
    savedInstance.putSerializable(IntentKey.SLEEP_SESSION, currentSession);
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

  @Override
  public SleepSession getSleepSession() {
    return currentSession;
  }

  @Override
  public void setSleepSession(SleepSession update) {
    currentSession = new SleepSession(update);
  }
}
