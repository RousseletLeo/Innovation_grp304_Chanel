package com.acs.readertest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class ActivityLecteur extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecteur);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.veuillez_brancher_le_lecteur)
                .setTitle(R.string.lecteur_deco)
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