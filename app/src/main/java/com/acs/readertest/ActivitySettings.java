package com.acs.readertest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class ActivitySettings extends Activity {
    TextView messageView;
    Context context;
    Resources resources;


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
        //

        //Remettre la valeur de la *LANGUE* sélectionnée lors du nouveau chargement de la page
        final Spinner spinnerLangue = findViewById(R.id.sp_langue);
        spinnerLangue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // Récupérer la valeur sélectionnée
                String selectedLangue = spinnerLangue.getSelectedItem().toString();

                // Enregistrer la valeur sélectionnée dans les SharedPreferences
                SharedPreferences prefs = getSharedPreferences("base_de_donnees", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("langue_select", selectedLangue);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Ne rien faire
            }
        });
        // Restaurer la sélection précédente depuis les SharedPreferences
        SharedPreferences prefs = getSharedPreferences("base_de_donnees", Context.MODE_PRIVATE);
        String selectedLangue = prefs.getString("langue_select", "Français");

        // Trouver l'index de la valeur dans le Spinner et le sélectionner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_langues, android.R.layout.simple_spinner_item);
        int position = adapter.getPosition(selectedLangue);
        spinnerLangue.setSelection(position);


        //Remettre la valeur du *THEME* sélectionnée lors du nouveau chargement de la page
        final Spinner spinnerTheme = findViewById(R.id.sp_theme);
        spinnerTheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // Récupérer la valeur sélectionnée
                String selectedTheme = spinnerTheme.getSelectedItem().toString();

                // Enregistrer la valeur sélectionnée dans les SharedPreferences
                SharedPreferences prefs_2 = getSharedPreferences("base_de_donnees", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs_2.edit();
                editor.putString("theme_select", selectedTheme);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Ne rien faire
            }
        });
        // Restaurer la sélection précédente depuis les SharedPreferences
        SharedPreferences prefs_2 = getSharedPreferences("base_de_donnees", Context.MODE_PRIVATE);
        String selectedTheme = prefs_2.getString("theme_select", "Automatique");
        // Trouver l'index de la valeur dans le Spinner et le sélectionner
        ArrayAdapter<CharSequence> adapter_2 = ArrayAdapter.createFromResource(this, R.array.spinner_themes, android.R.layout.simple_spinner_item);
        int position_2 = adapter_2.getPosition(selectedTheme);
        spinnerTheme.setSelection(position_2);



        //Remettre la valeur des *NOTIFICATIONS* sélectionnée lors du nouveau chargement de la page
        Switch switch1 = findViewById(R.id.sw_notification);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // Enregistrer l'état du Switch dans les SharedPreferences
                SharedPreferences prefs = getSharedPreferences("base_de_donnees", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("etat_switch_notifs", isChecked);
                editor.apply();
            }
        });
        // Restaurer l'état précédent depuis les SharedPreferences
        SharedPreferences prefs_21 = getSharedPreferences("base_de_donnees", Context.MODE_PRIVATE);
        boolean switch1State = prefs_21.getBoolean("switch1_state", false);
        // Appliquer l'état restauré au Switch
        switch1.setChecked(switch1State);




        //Remettre la valeur du *NIVEAU D'AUTORISATION* sélectionnée lors du nouveau chargement de la page
        final Spinner spinnerAuto = findViewById(R.id.sp_auto_salles);
        spinnerAuto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // Récupérer la valeur sélectionnée
                String selectedAuto = spinnerAuto.getSelectedItem().toString();

                // Enregistrer la valeur sélectionnée dans les SharedPreferences
                SharedPreferences prefs_3 = getSharedPreferences("base_de_donnees", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs_3.edit();
                editor.putString("auto_select", selectedAuto);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Ne rien faire
            }
        });
        // Restaurer la sélection précédente depuis les SharedPreferences
        SharedPreferences prefs_3 = getSharedPreferences("base_de_donnees", Context.MODE_PRIVATE);
        String selectedAuto = prefs_3.getString("auto_select", "Niveau 1");
        // Trouver l'index de la valeur dans le Spinner et le sélectionner
        ArrayAdapter<CharSequence> adapter_3 = ArrayAdapter.createFromResource(this, R.array.spinner_items_values, android.R.layout.simple_spinner_item);
        int position_3 = adapter_3.getPosition(selectedAuto);
        spinnerAuto.setSelection(position_3);
    }



    //Mettre en pause l'animation lorsqu'on quitte la page
    @Override
    protected void onPause() {
        super.onPause();
        ImageView gearImageView = findViewById(R.id.imageView2);
        gearImageView.clearAnimation();
    }


    public void sauvegarder(View view) {
        Spinner spinner = findViewById(R.id.sp_langue);
        String langue = spinner.getSelectedItem().toString();

        Log.d("TAG", langue);

        SharedPreferences prefs = getSharedPreferences("strings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language", langue);
        editor.apply();

        Spinner spinner_2 = findViewById(R.id.sp_theme);
        String theme = spinner_2.getSelectedItem().toString();
        SharedPreferences prefs_2 = getSharedPreferences("strings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor_2 = prefs_2.edit();
        editor_2.putString("theme_spinner", theme);
        editor_2.apply();

        context = LocaleHelper.setLocale(ActivitySettings.this, "en");
        resources = context.getResources();

        Toast.makeText(this, "Changements sauvegardés !", Toast.LENGTH_SHORT).show();

        recreate();
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
}