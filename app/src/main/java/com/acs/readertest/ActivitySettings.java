package com.acs.readertest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class ActivitySettings extends Activity {

    private static final String LOG_TAG =ActivitySimpleMode.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Animation : l'engrenage qui tourne
        ImageView ic_settings = findViewById(R.id.imageView2);
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        ic_settings.startAnimation(rotateAnimation);
    }

    //Mettre en pause l'animation lorsqu'on quitte la page
    @Override
    protected void onPause() {
        super.onPause();
        ImageView gearImageView = findViewById(R.id.imageView2);
        gearImageView.clearAnimation();
    }


    public void launchActivitySettings(View view) {
        Log.d(LOG_TAG, "Boutton des paramètres cliqué !");
        Intent intent = new Intent(this, ActivitySettings.class);
        startActivity(intent);
    }
}