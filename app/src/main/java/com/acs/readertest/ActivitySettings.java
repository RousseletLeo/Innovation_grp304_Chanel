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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ActivitySettings extends Activity {
    TextView messageView;
    Context context;
    Resources resources;
    private static final String SELECTED_ITEM = "SELECTED_ITEM";
    private Spinner mSpinner;
    private int mSelectedItem = 0;

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
        mSpinner = findViewById(R.id.sp_langue);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_langues, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        if (savedInstanceState != null) {
            mSelectedItem = savedInstanceState.getInt(SELECTED_ITEM);
            mSpinner.setSelection(mSelectedItem);
        }

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedItem = position;
                // Do something with the selected item
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_ITEM, mSelectedItem);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSelectedItem = savedInstanceState.getInt(SELECTED_ITEM);
    }

    //Mettre en pause l'animation lorsqu'on quitte la page
    @Override
    protected void onPause() {
        super.onPause();
        ImageView gearImageView = findViewById(R.id.imageView2);
        gearImageView.clearAnimation();
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
}