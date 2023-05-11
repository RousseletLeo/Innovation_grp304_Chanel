package com.acs.readertest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ActivityCarte extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carte);

        Intent intent986 = new Intent(ActivityCarte.this, ActivitySimpleMode.class);
        startActivity(intent986);
    }

}