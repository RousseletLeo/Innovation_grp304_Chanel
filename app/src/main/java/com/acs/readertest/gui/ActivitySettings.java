package com.acs.readertest.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.acs.readertest.R;

public class ActivitySettings extends Activity {

    private static final String LOG_TAG = ActivitySimpleMode.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void launchActivitySettings(View view) {
        Log.d(LOG_TAG, "Boutton des paramètres cliqué !");
        Intent intent = new Intent(this, ActivitySettings.class);
        startActivity(intent);
    }
}