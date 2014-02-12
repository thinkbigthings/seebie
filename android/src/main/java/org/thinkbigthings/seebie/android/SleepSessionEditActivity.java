package org.thinkbigthings.seebie.android;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import java.text.DecimalFormat;

// TODO figure out how to use betterpickers without the support fragments, would like to use latest fragments

public class SleepSessionEditActivity extends FragmentActivity {

  public final static String SLEEP_SESSION = "org.thinkbigthings.seebie.android.sleepSession";
  public final static String CREATE_OR_UPDATE = "org.thinkbigthings.seebie.android.sleepSession.createOrUpdate";
  public final static String CREATE = "CREATE";

  private NumberPickerDialogFragment.NumberPickerDialogHandler awakeInBedCallback = new NumberPickerDialogFragment.NumberPickerDialogHandler() {
    @Override public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber) {
      currentSession.withMinutesAwakeInBed(number);
      updateDisplay();
    }
  };
  private NumberPickerDialogFragment.NumberPickerDialogHandler awakeOutOfBedCallback = new NumberPickerDialogFragment.NumberPickerDialogHandler() {
    @Override public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber) {
      currentSession.withMinutesAwakeOutOfBed(number);
      updateDisplay();
    }
  };
  private RadialTimePickerDialog.OnTimeSetListener startTimeCallback = new RadialTimePickerDialog.OnTimeSetListener() {
    @Override public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
      currentSession.withStartTime(hour, minute);
      updateDisplay();
    }
  };
  private RadialTimePickerDialog.OnTimeSetListener finishTimeCallback = new RadialTimePickerDialog.OnTimeSetListener() {
    @Override public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
      currentSession.withFinishTime(hour, minute);
      updateDisplay();
    }
  };
  private CalendarDatePickerDialog.OnDateSetListener finishDateCallback = new CalendarDatePickerDialog.OnDateSetListener() {
    @Override public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int month, int day) {
      currentSession.withFinishDate(year, month, day);
      updateDisplay();
    }
  };
  private View.OnClickListener awakeInBedClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      NumberPickerBuilder npb = new NumberPickerBuilder()
          .setFragmentManager(getSupportFragmentManager())
          .setStyleResId(R.style.BetterPickersDialogFragment)
          .setDecimalVisibility(View.GONE)
          .setPlusMinusVisibility(View.GONE)
          .addNumberPickerDialogHandler(awakeInBedCallback);
      npb.show();
    }
  };
  private View.OnClickListener awakeOutOfBedClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      NumberPickerBuilder npb = new NumberPickerBuilder()
          .setFragmentManager(getSupportFragmentManager())
          .setStyleResId(R.style.BetterPickersDialogFragment)
          .setDecimalVisibility(View.GONE)
          .setPlusMinusVisibility(View.GONE)
          .addNumberPickerDialogHandler(awakeOutOfBedCallback);
      npb.show();
    }
  };
  private View.OnClickListener startTimeClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      LocalTime time= currentSession.getStartTime();
      RadialTimePickerDialog dialog;
      dialog = RadialTimePickerDialog.newInstance(startTimeCallback, time.getHourOfDay(), time.getMinuteOfHour(), false);
      dialog.show(getSupportFragmentManager(), null);
    }
  };
  private View.OnClickListener finishDateClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      DateTime time= currentSession.getFinishTime();
      CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
          .newInstance(finishDateCallback, time.getYear(), time.getMonthOfYear(), time.getDayOfMonth());
      calendarDatePickerDialog.show(getSupportFragmentManager(), null);
    }
  };
  private View.OnClickListener finishTimeClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      DateTime time= currentSession.getFinishTime();
      RadialTimePickerDialog dialog;
      dialog = RadialTimePickerDialog.newInstance(finishTimeCallback, time.getHourOfDay(), time.getMinuteOfHour(), false);
      dialog.show(getSupportFragmentManager(), null);
    }
  };

  private SleepSession currentSession;
  private boolean isCreate;
  private DatabaseOpenHelper helper;
  private SQLiteDatabase writableDatabase;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sleep_session_edit);

    setButtonClickListener(R.id.timeInBedAwake, awakeInBedClickListener);
    setButtonClickListener(R.id.timeOutOfBedAwake, awakeOutOfBedClickListener);
    setButtonClickListener(R.id.startTime, startTimeClickListener);
    setButtonClickListener(R.id.finishDate, finishDateClickListener);
    setButtonClickListener(R.id.finishTime, finishTimeClickListener);

    Intent intent = getIntent();
    currentSession = (SleepSession)intent.getSerializableExtra(SleepSessionEditActivity.SLEEP_SESSION);
    isCreate = CREATE.equals(intent.getStringExtra(SleepSessionEditActivity.CREATE_OR_UPDATE));

    updateDisplay();

    // TODO call getWritableDatabase() or getReadableDatabase() in a background thread
    // such as with AsyncTask or IntentService.
    helper = new DatabaseOpenHelper(this);
    writableDatabase = helper.getWritableDatabase();

  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    helper.close();
  }

  private void setButtonClickListener(int id, View.OnClickListener listener) {
    Button button = (Button) findViewById(id);
    button.setOnClickListener(listener);
  }

  private void updateDisplay() {

    String display;

    display = "Awake in bed for " + currentSession.getMinutesAwakeInBed() + " minutes";
    ((Button) findViewById(R.id.timeInBedAwake)).setText(display);

    display = "Awake out of bed for " + currentSession.getMinutesAwakeOutOfBed() + " minutes";
    ((Button) findViewById(R.id.timeOutOfBedAwake)).setText(display);

    display = "Got into bed at "+ DateTimeFormat.shortTime().print(currentSession.getStartTime());
    ((Button) findViewById(R.id.startTime)).setText(display);

    display = "Got up for the day at "+ DateTimeFormat.shortTime().print(currentSession.getFinishTime());
    ((Button) findViewById(R.id.finishTime)).setText(display);

    display = "On " + DateTimeFormat.forPattern("EEEE").print(currentSession.getFinishTime())  + " "
                    + DateTimeFormat.shortDate().print(currentSession.getFinishTime());
    ((Button) findViewById(R.id.finishDate)).setText(display);

    TextView totalSleepDisplay = (TextView)findViewById(R.id.total_sleep_display);
    TextView efficiencyDisplay = (TextView)findViewById(R.id.efficiency_display);
    long time = currentSession.calculateMinutesInBedSleeping();
    DecimalFormat format = new DecimalFormat("#.##");
    totalSleepDisplay.setText("Sleep Time: " + time + " m, (" + format.format((double)time/60d) + " hrs)");
    efficiencyDisplay.setText("Efficiency: " + format.format(currentSession.calculateEfficiency()*100) + "%");

    ((Button) findViewById(R.id.deleteButton)).setVisibility(isCreate ? View.INVISIBLE : View.VISIBLE);

  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstance) {
    currentSession = (SleepSession)savedInstance.getSerializable(SLEEP_SESSION);
    updateDisplay();
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstance) {
    savedInstance.putSerializable(SLEEP_SESSION, currentSession);
  }

  public void onDelete(View button) {

    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    builder.setMessage("Delete this sleep session?")
            .setTitle("Confirm")
            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                deleteCurrentSleepSession();
                Intent intent = new Intent(SleepSessionEditActivity.this, SleepSessionListingActivity.class);
                startActivity(intent);
              }
            })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
          }
        });

    AlertDialog dialog = builder.create();
    dialog.show();

  }

  public void onCancel(View button) {
    Intent intent = new Intent(this, SleepSessionListingActivity.class);
    startActivity(intent);
  }

  public void onSave(View button) {
    saveCurrentSleepSession();
    Intent intent = new Intent(this, SleepSessionListingActivity.class);
    startActivity(intent);
  }

  public void deleteCurrentSleepSession() {
    writableDatabase.beginTransaction();

    try {
      String whereIdEquals = "_ID = ?";
      String[] currentId = new String[]{currentSession.getId().toString()};
      writableDatabase.delete(DatabaseContract.SleepSession.TABLE_NAME, whereIdEquals, currentId);
      writableDatabase.setTransactionSuccessful();
    }
    catch(Exception ex) {
      String cause = ex.getMessage();
      throw ex;
    }
    finally {
      writableDatabase.endTransaction();
    }
  }

  public void saveCurrentSleepSession() {

    // mapping from domain object to database record
    ContentValues values = new ContentValues();
    values.put(DatabaseContract.SleepSession.COLUMN_NAME_ALL_MINUTES, currentSession.calculateAllMinutes());
    values.put(DatabaseContract.SleepSession.COLUMN_NAME_FINISH_TIME, currentSession.getFinishTime().getMillis());
    values.put(DatabaseContract.SleepSession.COLUMN_NAME_MINUTES_AWAKE_IN, currentSession.getMinutesAwakeInBed());
    values.put(DatabaseContract.SleepSession.COLUMN_NAME_MINUTES_AWAKE_OUT, currentSession.getMinutesAwakeOutOfBed());

    writableDatabase.beginTransaction();

    try {
      if(isCreate) {
        writableDatabase.insert(DatabaseContract.SleepSession.TABLE_NAME, null, values);
      }
      else {
        String whereIdEquals = "_ID = ?";
        String[] currentId = new String[]{currentSession.getId().toString()};
        writableDatabase.update(DatabaseContract.SleepSession.TABLE_NAME, values, whereIdEquals, currentId);
      }
      writableDatabase.setTransactionSuccessful();
    }
    catch(Exception ex) {
      String cause = ex.getMessage();
      throw ex;
    }
    finally {
      writableDatabase.endTransaction();
    }

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

}
