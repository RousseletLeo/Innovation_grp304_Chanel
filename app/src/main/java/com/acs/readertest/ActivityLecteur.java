package com.acs.readertest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class ActivityLecteur extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecteur);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Veuillez brancher le lecteur")
                .setTitle("Lecteur déconnecté")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action à exécuter lorsque l'utilisateur clique sur "OK"
                        Intent intent45 = new Intent(ActivityLecteur.this, ActivityCarte.class);
                        startActivity(intent45);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}