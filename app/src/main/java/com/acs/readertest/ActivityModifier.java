package com.acs.readertest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityModifier extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier);

    }

    public void sauvegarderModifs(View view) {
        EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText bioEditText = findViewById(R.id.bioEditText);
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText numTelEditText = findViewById(R.id.numTelEditText);
        EditText posteEditText = findViewById(R.id.posteEditText);
        EditText nivAutoEditText = findViewById(R.id.nivAutoEditText);

        SharedPreferences sharedPreferences = getSharedPreferences("base_de_donnees", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("nom_carte", usernameEditText.getText().toString());
        editor.putString("bio_carte", bioEditText.getText().toString());
        editor.putString("email_carte", emailEditText.getText().toString());
        editor.putString("num_tel_carte", numTelEditText.getText().toString());
        editor.putString("poste_carte", posteEditText.getText().toString());
        editor.putString("niv_auto_carte", nivAutoEditText.getText().toString());
        editor.apply();

        Toast.makeText(this, "Changements sauvegardés !", Toast.LENGTH_SHORT).show();

        Intent intent_1234 = new Intent(this, ActivityProfile.class);
        startActivity(intent_1234);
    }
}