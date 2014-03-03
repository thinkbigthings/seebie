package org.thinkbigthings.seebie.android;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.thinkbigthings.sleep.SleepSessionAverage;

import java.util.List;

public class WeeklyListingAdapter extends ArrayAdapter<SleepSessionAverage> {

  // TODO may need to specify the textview id
  public WeeklyListingAdapter(Context context,  int resource, int textViewResourceId, List<SleepSessionAverage> objects) {
    super(context, resource, textViewResourceId, objects);
  }

  @Override
  public View getView (int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(getContext());
    View rowView = inflater.inflate(R.layout.activity_daily_listing_row, parent, false);

    SleepSessionAverage averages = this.getItem(position);

    ((TextView)rowView.findViewById(R.id.primaryListingRow)).setText(String.valueOf(averages.getLatestDateTime().getDayOfMonth()));
    ((TextView)rowView.findViewById(R.id.secondaryListingRow)).setText("Slept " + averages.getAverageMinutesSleeping());

    return rowView;
  }

}
