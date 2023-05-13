package com.acs.readertest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class ActivityCarte extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carte);

        Intent intent986 = new Intent(ActivityCarte.this, ActivitySimpleMode.class);
        startActivity(intent986);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent2 = new Intent(this, ActivitySettings.class);
                startActivity(intent2);
                return true;
            case R.id.action_help:
                Intent intent3 = new Intent(this, ActivityHelp.class);
                startActivity(intent3);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}