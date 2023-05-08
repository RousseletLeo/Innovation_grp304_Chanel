package com.acs.readertest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ActivityPageAccueil extends Activity {
    private static final String LOG_TAG =ActivityPageAccueil.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_accueil);

    }

    public void launchMainActivity(View view) {
        Log.d(LOG_TAG, "Boutton cliqué !");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}