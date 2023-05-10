package com.acs.readertest;

import static com.acs.readertest.R.id.details_button;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ActivitySimpleMode extends Activity {

    private ImageView checkImage;
    private Button detailsButton;
    private boolean condition;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_mode);

        // Récupération des éléments de la vue
        checkImage = findViewById(R.id.check_image_view);
        detailsButton = findViewById(details_button);

        // Condition à vérifier
        condition = false; // Mettre à false pour tester la croix rouge

        // Affichage du check ou de la croix rouge
        if (condition) {
            checkImage.setImageResource(R.drawable.ic_check);
        } else {
            checkImage.setImageResource(R.drawable.ic_error);
            detailsButton.setVisibility(View.VISIBLE);
        }

        // Ajout d'un listener pour le bouton de détails
        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // AFFICHER LES DETAILS DES ERREURS
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