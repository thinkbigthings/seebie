package org.thinkbigthings.seebie.android;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import org.thinkbigthings.sleep.SleepSessionAverage;

import java.util.List;

public class WeeklyListingAdapter extends ArrayAdapter {

  // TODO may need to specify the textview id
  public WeeklyListingAdapter(Context context, int resource, List<SleepSessionAverage> objects) {
    super(context, resource, objects);
  }

  public View getView (int position, View convertView, ViewGroup parent) {
//    LayoutInflater inflater = LayoutInflater.from(context);
//    return inflater.inflate(R.layout.activity_daily_listing_row, parent, false);

//    SleepSession session = reader.read(cursor);
//    SleepSessionFormat format = new SleepSessionFormat();
//    ((TextView)view.findViewById(R.id.primaryListingRow)).setText(format.date(session));
//    ((TextView)view.findViewById(R.id.secondaryListingRow)).setText("Slept " + format.summary(session));
//
    return null;
  }

}
