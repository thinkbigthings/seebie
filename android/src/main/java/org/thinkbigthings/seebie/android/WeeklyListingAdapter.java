package org.thinkbigthings.seebie.android;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import org.joda.time.DateTimeConstants;
import org.thinkbigthings.sleep.SleepSession;
import org.thinkbigthings.sleep.SleepSessionAverage;
import org.thinkbigthings.sleep.SleepSessionFormat;

public class WeeklyListingAdapter extends CursorAdapter {

  private GeneralDAO.CursorReader<SleepSession> singleSessionReader = new GeneralDAO.CursorReader<SleepSession>() {
    @Override
    public SleepSession read(Cursor cursor) {
      Long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.SleepSession._ID));
      Long ft = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.SleepSession.COLUMN_NAME_FINISH_TIME));
      Long am = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.SleepSession.COLUMN_NAME_ALL_MINUTES));
      Long ai = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.SleepSession.COLUMN_NAME_MINUTES_AWAKE_IN));
      Long ao = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.SleepSession.COLUMN_NAME_MINUTES_AWAKE_OUT));
      return new SleepSession(id, ft, am, ai, ao);
    }
  };

  // assumes records returned by the Cursor are sorted by date
  private GeneralDAO.CursorReader<SleepSessionAverage> weekReader = new GeneralDAO.CursorReader<SleepSessionAverage>() {
    @Override
    public SleepSessionAverage read(Cursor cursor) {
      SleepSessionAverage averages = new SleepSessionAverage();
      do {
        if(cursor.isLast()) {
          break;
        }
        SleepSession session = singleSessionReader.read(cursor);
        averages.with(session);
        int monday = DateTimeConstants.MONDAY;
        int currentDay = session.getFinishTime().getDayOfWeek();
        if( currentDay == monday) {
          break;
        }
      }
      while(cursor.moveToNext());

      return averages;
    }
  };

  public WeeklyListingAdapter(Context context, Cursor c) {
    // TODO use CursorLoader
    super(context, c);
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    // if it's a button, set clickable/focusable/focusableInTouchMode to false (otherwise it grabs click events from listview)
    // use LayoutInflater when inflating inside an adapter
    LayoutInflater inflater = LayoutInflater.from(context);
    return inflater.inflate(R.layout.activity_daily_listing_row, parent, false);
  }

  @Override
  public void bindView(View view, Context context, Cursor cursor) {
    SleepSessionAverage averages = weekReader.read(cursor);
    ((TextView)view.findViewById(R.id.primaryListingRow)).setText("Week Ending " + averages.getLatestDateTime());
    ((TextView)view.findViewById(R.id.secondaryListingRow)).setText("Slept " + averages.getAverageMinutesSleeping()+"m");
  }

}
