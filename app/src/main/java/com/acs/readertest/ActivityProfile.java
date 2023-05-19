package com.acs.readertest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ActivityProfile extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


    }

    public void launchActivityModifier(View view) {
        Intent intent_1234 = new Intent(this, ActivityModifier.class);
        startActivity(intent_1234);
    }
}