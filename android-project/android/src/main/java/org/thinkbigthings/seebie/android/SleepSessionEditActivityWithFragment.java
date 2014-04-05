package org.thinkbigthings.seebie.android;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import org.thinkbigthings.sleep.HasSleepSession;
import org.thinkbigthings.sleep.SleepSession;

// TODO figure out how to use betterpickers without the support fragments, would like to use latest fragments

public class SleepSessionEditActivityWithFragment extends FragmentActivity implements HasSleepSession {

  protected SleepSession currentSession;
  protected GeneralDAO<SleepSession> dao;

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    getActionBar().setDisplayHomeAsUpEnabled(true);
    
    setContentView(R.layout.activity_sleep_session_edit_with_fragment);

    Intent intent = getIntent();
    currentSession = (SleepSession)intent.getSerializableExtra(IntentKey.SLEEP_SESSION);

    dao = new GeneralDAO<>(new DatabaseOpenHelper(this));
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

  public void onCancel(View button) {
    Intent intent = new Intent(this, DailyDetailActivity.class);
    intent.putExtra(IntentKey.SLEEP_SESSION, currentSession);
    startActivity(intent);
  }

  public void onSave(View button) {
    saveCurrentSleepSession();
    Intent intent = new Intent(this, DailyDetailActivity.class);
    intent.putExtra(IntentKey.SLEEP_SESSION, currentSession);

    startActivity(intent);
  }

  public void saveCurrentSleepSession() {

    // mapping from domain object to database record
    ContentValues values = new ContentValues();
    values.put(DatabaseContract.SleepSession.COLUMN_NAME_ALL_MINUTES, currentSession.calculateAllMinutes());
    values.put(DatabaseContract.SleepSession.COLUMN_NAME_FINISH_TIME, currentSession.getFinishTime().getMillis());
    values.put(DatabaseContract.SleepSession.COLUMN_NAME_MINUTES_AWAKE_IN, currentSession.getMinutesAwakeInBed());
    values.put(DatabaseContract.SleepSession.COLUMN_NAME_MINUTES_AWAKE_OUT, currentSession.getMinutesAwakeOutOfBed());

    String whereIdEquals = "_ID = ?";
    String[] currentId = new String[]{currentSession.getId().toString()};
    dao.update(DatabaseContract.SleepSession.TABLE_NAME, values, whereIdEquals, currentId);
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
