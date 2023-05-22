package com.acs.readertest.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.acs.readertest.MainActivity;
import com.acs.readertest.R;

public class ActivityPageAccueil extends Activity {
    private static final String LOG_TAG =ActivityPageAccueil.class.getSimpleName();
    private static final String LOG_TAG_2 =ActivityPageAccueil.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_accueil);

    }

    public void launchMainActivity(View view) {
        Log.d(LOG_TAG, "Mode avancé lancé !");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void launchActivitySimpleMode(View view) {
        Log.d(LOG_TAG_2, "Bienvenue dans CHANEL Companion !");
        Intent intent2 = new Intent(this, ActivityLecteur.class);
        startActivity(intent2);
    }


}