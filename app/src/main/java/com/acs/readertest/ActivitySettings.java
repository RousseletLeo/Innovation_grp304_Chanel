package com.acs.readertest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class ActivitySettings extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Animation : l'engrenage qui tourne
        ImageView ic_settings = findViewById(R.id.imageView2);
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        ic_settings.startAnimation(rotateAnimation);
    }

    //Mettre en pause l'animation lorsqu'on quitte la page
    @Override
    protected void onPause() {
        super.onPause();
        ImageView gearImageView = findViewById(R.id.imageView2);
        gearImageView.clearAnimation();
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

    public Integer maj_autorisations(View view) {
        Spinner spinner = findViewById(R.id.sp_auto_salles);

        // Récupérer la valeur sélectionnée
        String niv_auto_salles = spinner.getSelectedItem().toString();

        int num = Integer.parseInt(niv_auto_salles.substring(7, 8));
        System.out.println(num);

        SharedPreferences prefs = getSharedPreferences("base_de_donnees", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("niv_auto_sett", num);
        editor.apply();

        Toast.makeText(this, "Changements sauvegardés !", Toast.LENGTH_SHORT).show();

        return num;
    }

    public void sauvegarder(View view) {
        Spinner spinner = findViewById(R.id.sp_langue);
        String langue = spinner.getSelectedItem().toString();
        SharedPreferences prefs = getSharedPreferences("base_de_donnees", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language", langue);
        editor.apply();

        Spinner spinner_2 = findViewById(R.id.sp_theme);
        String theme = spinner_2.getSelectedItem().toString();
        SharedPreferences prefs_2 = getSharedPreferences("base_de_donnees", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor_2 = prefs_2.edit();
        editor_2.putString("theme_spinner", theme);
        editor_2.apply();

        Toast.makeText(this, "Changements sauvegardés !", Toast.LENGTH_SHORT).show();
    }
}