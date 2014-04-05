package org.thinkbigthings.seebie.android;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import org.thinkbigthings.sleep.SleepSession;
import org.thinkbigthings.sleep.SleepSessionFormat;

public class DailyListingAdapter extends CursorAdapter {

  CursorReaderSleepSession singleSessionReader = new CursorReaderSleepSession();

  public DailyListingAdapter(Context context, Cursor c) {
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
    SleepSession session = singleSessionReader.read(cursor);
    SleepSessionFormat format = new SleepSessionFormat();
    ((TextView)view.findViewById(R.id.primaryListingRow)).setText(format.date(session));
    ((TextView)view.findViewById(R.id.secondaryListingRow)).setText("Slept " + format.summary(session));
  }

}
