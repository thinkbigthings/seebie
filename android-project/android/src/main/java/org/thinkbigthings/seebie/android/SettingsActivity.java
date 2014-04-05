package org.thinkbigthings.seebie.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.List;

public class SettingsActivity extends Activity {

  public static final String PROPERTY_KEY_1 = "property1";

  @SuppressLint("NewApi")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getActionBar().setDisplayHomeAsUpEnabled(true);
    setContentView(R.layout.activity_settings);

    SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
    String value = prefs.getString(PROPERTY_KEY_1, "");

    EditText editText = (EditText) findViewById(R.id.value_property_1);
    editText.getText().clear();
    editText.getText().append(value);
  }

  @Override
  public void onStop() {

    super.onStop();

    EditText editText = (EditText) findViewById(R.id.value_property_1);
    String value = editText.getText().toString();

    SharedPreferences.Editor prefs = getPreferences(Context.MODE_PRIVATE).edit();
    prefs.putString(PROPERTY_KEY_1, value);
    prefs.apply();
  }

}
