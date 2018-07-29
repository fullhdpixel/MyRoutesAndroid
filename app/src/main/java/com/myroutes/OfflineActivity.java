package com.myroutes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

public class OfflineActivity extends AppCompatActivity implements View.OnClickListener {
  Button selectFile;
  CheckBox checkbox;
  TextView filenameOfflineRoute;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_offline_routes);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    filenameOfflineRoute = (TextView) findViewById(R.id.filenameOfflineRoute);
    //Get Mbtiles value from sharedprefs
    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    String filePath = sharedPrefs.getString("mbtiles", "-1");
    String filename;
    if (!filePath.equals("-1")) {
      filename = filePath.substring(filePath.lastIndexOf("/") + 1);
      filenameOfflineRoute.setText(filename);
    }
    selectFile = (Button) findViewById(R.id.selectFile);
    selectFile.setOnClickListener(this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.toolbar_checkbox, menu);
    checkbox = (CheckBox) menu.findItem(R.id.menuShowDue).getActionView();

    String localized_enable_offline_maps = getString(R.string.action_enable);
    checkbox.setText(localized_enable_offline_maps);

    final Context context = this;
    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    checkbox.setChecked(sharedPrefs.getBoolean("offline", false));

    checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //set in sharedprefs
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit().putBoolean("offline", isChecked).apply();
      }
    });

    return true;
  }

  private static final int FILE_SELECT_CODE = 0;

  @Override
  public void onClick(View view) {
    if (view.getId() == R.id.selectFile) {
      //Select file

      new MaterialFilePicker()
          .withActivity(this)
          .withRequestCode(FILE_SELECT_CODE)
          .withFilterDirectories(true)
          .withHiddenFiles(true)
          .start();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == FILE_SELECT_CODE) {
      if (resultCode == RESULT_OK) {
        String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
        String filename = filePath.substring(filePath.lastIndexOf("/") + 1);
        //Check if mbtiles
        if (filename.length() != 0 && filePath.contains(".mbtiles")) {
          //set in sharedprefs
          SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
          sharedPrefs.edit().putString("mbtiles", filePath).apply();
          filenameOfflineRoute.setText(filename);
        } else {
          String localized_no_mbtiles_file = getString(R.string.no_mbtiles_file);
          Toast.makeText(getApplicationContext(), localized_no_mbtiles_file, Toast.LENGTH_SHORT).show();
        }
      }
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        this.finish();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
