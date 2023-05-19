package com.acs.readertest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

public class ActivityCarte extends Activity {

    private BroadcastReceiver usbReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carte);

        //Animation de la carte
        ImageView carteImageView = findViewById(R.id.carteImageView);
        Animation carteAnimation = AnimationUtils.loadAnimation(this, R.anim.carte_animation);
        carteImageView.startAnimation(carteAnimation);

        boolean nfcReaderAvailable = getIntent().getBooleanExtra("nfc_reader_available", true);
        if (nfcReaderAvailable) {
            // Le lecteur NFC est disponible //

            // Données de la carte
            String nom_carte = String.valueOf(R.string.nom_carte);
            String email_carte = String.valueOf(R.string.email_carte);;
            String num_tel_carte = String.valueOf(R.string.num_tel_carte);;
            String poste_carte = String.valueOf(R.string.poste_carte);;
            int niveauAutorisation = R.string.niv_auto_carte;

            SharedPreferences prefs = getSharedPreferences("base_de_donnees", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("niv_auto_carte", niveauAutorisation);
            editor.apply();

            Intent intent999 = new Intent(ActivityCarte.this, ActivitySimpleMode.class);
            startActivity(intent999);
        } else {
            // Le lecteur NFC n'est pas disponible
            Toast.makeText(ActivityCarte.this, "Lecteur NFC débranché", Toast.LENGTH_SHORT).show();
            Intent intent986 = new Intent(ActivityCarte.this, ActivityLecteur.class);
            startActivity(intent986);
        }
    }
}