package com.acs.readertest;

import static com.acs.readertest.R.id.details_button;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ActivitySimpleMode extends Activity {
    private ImageView checkImage;
    private Button detailsButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_mode);

        // Récupération des éléments de la vue
        checkImage = findViewById(R.id.check_image_view);
        detailsButton = findViewById(details_button);

        // Données de la carte
        SharedPreferences prefs_2 = getSharedPreferences("base_de_donnees", Context.MODE_PRIVATE);
        int niveauAutorisation = prefs_2.getInt("niv_auto_carte", 0);

        // Lecture de la base de données
        SharedPreferences prefs = getSharedPreferences("base_de_donnees", Context.MODE_PRIVATE);
        int niv_auto_sett = prefs.getInt("niv_auto_sett", 3);

        // Affichage du check ou de la croix rouge
        if (niveauAutorisation >= niv_auto_sett) {
            checkImage.setImageResource(R.drawable.ic_check);
        } else {
            checkImage.setImageResource(R.drawable.ic_error);
        }
        detailsButton.setVisibility(View.VISIBLE);


        // Ajout d'un listener pour le bouton de détails
        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // AFFICHER LES DETAILS DES ERREURS
                Intent intent32 = new Intent(ActivitySimpleMode.this, ActivityProfile.class);
                startActivity(intent32);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_simple, menu);
        return true;
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