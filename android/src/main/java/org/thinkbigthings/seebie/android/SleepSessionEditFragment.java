package org.thinkbigthings.seebie.android;


import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

public class SleepSessionEditFragment  extends Fragment {

  private SleepSessionChangeListener mCallback;
  private SleepSession currentSession;

  protected NumberPickerDialogFragment.NumberPickerDialogHandler awakeInBedCallback = new NumberPickerDialogFragment.NumberPickerDialogHandler() {
    @Override public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber) {
      update(currentSession.withMinutesAwakeInBed(number));
    }
  };
  protected NumberPickerDialogFragment.NumberPickerDialogHandler awakeOutOfBedCallback = new NumberPickerDialogFragment.NumberPickerDialogHandler() {
    @Override public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber) {
      update(currentSession.withMinutesAwakeOutOfBed(number));
    }
  };
  protected RadialTimePickerDialog.OnTimeSetListener startTimeCallback = new RadialTimePickerDialog.OnTimeSetListener() {
    @Override public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
      update(currentSession.withStartTime(hour, minute));
    }
  };
  protected RadialTimePickerDialog.OnTimeSetListener finishTimeCallback = new RadialTimePickerDialog.OnTimeSetListener() {
    @Override public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
      update(currentSession.withFinishTime(hour, minute));
    }
  };

  public void onAwakeInBedClick(View view) {
    NumberPickerBuilder npb = new NumberPickerBuilder()
        .setFragmentManager(getChildFragmentManager())
        .setStyleResId(R.style.BetterPickersDialogFragment)
        .setLabelText(getResources().getString(R.string.minutes))
        .setDecimalVisibility(View.GONE)
        .setPlusMinusVisibility(View.GONE)
        .addNumberPickerDialogHandler(awakeInBedCallback);
    npb.show();
  }

  public void onAwakeOutOfBedClick(View v) {
    NumberPickerBuilder npb = new NumberPickerBuilder()
        .setFragmentManager(getChildFragmentManager())
        .setStyleResId(R.style.BetterPickersDialogFragment)
        .setLabelText(getResources().getString(R.string.minutes))
        .setDecimalVisibility(View.GONE)
        .setPlusMinusVisibility(View.GONE)
        .addNumberPickerDialogHandler(awakeOutOfBedCallback);
    npb.show();
  }

  public void onStartTimeClick(View view) {
    LocalTime time= currentSession.getStartTime();
    RadialTimePickerDialog dialog;
    dialog = RadialTimePickerDialog.newInstance(startTimeCallback, time.getHourOfDay(), time.getMinuteOfHour(), false);
    dialog.show(getChildFragmentManager(), null);
  }

  protected static class JodaDatePickerDialog extends CalendarDatePickerDialog {
    public static CalendarDatePickerDialog newInstance(CalendarDatePickerDialog.OnDateSetListener listener, DateTime date) {
      // JodaTime month is one-based, betterpickers is zero-based like Java Calendar
      return CalendarDatePickerDialog.newInstance(listener, date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth());
    }
  };

  protected CalendarDatePickerDialog.OnDateSetListener finishDateCallback = new CalendarDatePickerDialog.OnDateSetListener() {
    @Override public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int month, int day) {
      // JodaTime month is one-based, betterpickers is zero-based like Java Calendar
      currentSession.withFinishDate(year, month + 1, day);
      updateDisplay(currentSession);
    }
  };
  public void onFinishDateClick(View view) {
    DateTime time= currentSession.getFinishTime();
    CalendarDatePickerDialog calendarDatePickerDialog = JodaDatePickerDialog.newInstance(finishDateCallback, time);
    calendarDatePickerDialog.show(getChildFragmentManager(), null);
  }

  public void onFinishTimeClick(View view){
    DateTime time= currentSession.getFinishTime();
    RadialTimePickerDialog dialog;
    dialog = RadialTimePickerDialog.newInstance(finishTimeCallback, time.getHourOfDay(), time.getMinuteOfHour(), false);
    dialog.show(getChildFragmentManager(), null);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    try {
      mCallback = (SleepSessionChangeListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must implement SleepSessionChangeListener");
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    currentSession = ((SleepSessionEditActivityWithFragment)getActivity()).getSession();
    return inflater.inflate(R.layout.fragment_sleep_session_edit, container, false);
  }

  public SleepSession getCurrentSession() {
    return currentSession;
  }

  public void setCurrentSession(SleepSession session) {
    currentSession = session;
    updateDisplay(currentSession);
  }

  public View findViewById(int id) {
    return getView().findViewById(id);
  }

  private void update(SleepSession session) {
    mCallback.onSessionChanged(currentSession);
    updateDisplay(session);
  }

  private void updateDisplay(SleepSession session) {

    String display;
    SleepSession.Format formatter = new SleepSession.Format();
    Resources res = getResources();

    display = String.format(res.getString(R.string._edit_sleep_timeInBedAwake), session.getMinutesAwakeInBed());
    ((Button) findViewById(R.id.timeInBedAwake)).setText(display);

    display = String.format(res.getString(R.string._edit_sleep_timeOutOfBedAwake), session.getMinutesAwakeOutOfBed());
    ((Button) findViewById(R.id.timeOutOfBedAwake)).setText(display);

    display = String.format(res.getString(R.string._edit_sleep_startTime),  DateTimeFormat.shortTime().print(session.getStartTime()));
    ((Button) findViewById(R.id.startTime)).setText(display);

    display = String.format(res.getString(R.string._edit_sleep_finishTime), DateTimeFormat.shortTime().print(session.getFinishTime()));
    ((Button) findViewById(R.id.finishTime)).setText(display);

    display = String.format(res.getString(R.string._edit_sleep_finishDate), formatter.day(session));
    ((Button) findViewById(R.id.finishDate)).setText(display);

    TextView totalSleepDisplay = (TextView)findViewById(R.id.total_sleep_display);
    String[] hrMin = formatter.duration(session);
    display = String.format(res.getString(R.string._edit_sleep_duration), hrMin[0], hrMin[1]);
    totalSleepDisplay.setText(display);

    TextView efficiencyDisplay = (TextView)findViewById(R.id.efficiency_display);
    display = String.format(res.getString(R.string._edit_sleep_efficiency), formatter.efficiency(session));
    efficiencyDisplay.setText(display);
  }

  public interface SleepSessionChangeListener {
    void onSessionChanged(SleepSession update);
  }
}