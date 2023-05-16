package com.acs.readertest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ActivityProfile extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences sharedPreferences = getSharedPreferences("base_de_donnees", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("nom_carte", "");
        String bio = sharedPreferences.getString("bio_carte", "");
        String email = sharedPreferences.getString("email_carte", "");
        String numTel = sharedPreferences.getString("num_tel_carte", "");
        String poste = sharedPreferences.getString("poste_carte", "");
        String nivAuto = sharedPreferences.getString("niv_auto_carte", "");

        TextView username_text_view = findViewById(R.id.username_textview);
        username_text_view.setText(username);
        TextView bio_text_view = findViewById(R.id.bio_textview);
        bio_text_view.setText(bio);
        TextView email_text_view = findViewById(R.id.email_textview);
        email_text_view.setText(email);
        TextView num_tel_text_view = findViewById(R.id.num_tel_textview);
        num_tel_text_view.setText(numTel);
        TextView poste_text_view = findViewById(R.id.poste_textview);
        poste_text_view.setText(poste);
        TextView niv_auto_text_view = findViewById(R.id.niv_auto_textview);
        niv_auto_text_view.setText(nivAuto);
    }

    public void launchActivityModifier(View view) {
        Intent intent_1234 = new Intent(this, ActivityModifier.class);
        startActivity(intent_1234);
    }
}