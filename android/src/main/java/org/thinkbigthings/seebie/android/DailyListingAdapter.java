package org.thinkbigthings.seebie.android;

import android.content.Context;
import android.database.Cursor;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class DailyListingAdapter extends CursorAdapter {

  private GeneralDAO.CursorReader<SleepSession> reader = new GeneralDAO.CursorReader<SleepSession>() {
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

  public DailyListingAdapter(Context context, Cursor c) {
    // TODO use CursorLoader
    super(context, c);

  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    TextView button = new TextView(context);
    button.setGravity(Gravity.LEFT);
    button.setHeight(128);
    button.setTextSize(24);

    // if it's a button, set clickable/focusable/focusableInTouchMode to false

//    android:drawableRight="@android:drawable/ic_media_play"
    return button;
  }
  @Override
  public boolean isEnabled(int position)
  {
    return true;
  }

  @Override
  public void bindView(View view, Context context, Cursor cursor) {
    SleepSession session = reader.read(cursor);
    SleepSession.Format format = new SleepSession.Format();
    ((TextView)view).setText(format.title(session));
  }



}
