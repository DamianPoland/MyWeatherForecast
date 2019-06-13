package com.wolfmobileapps.myweatherforecast;


import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;


import static com.wolfmobileapps.myweatherforecast.MainActivity.SHARED_PREFERENCES_NAME;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";

    public static final String KEY_FOR_SHARED_PREF_SWITCH_UNITS = "switchModeUnits";

    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    private TextView textViewUnits;
    private Switch switchUnits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        textViewUnits = findViewById(R.id.textViewUnits);
        switchUnits = findViewById(R.id.switchUnits);

        //ustawienie górnej nazwy i strzałki do powrotu
        getSupportActionBar().setTitle(getResources().getString(R.string.settings)); //ustawia nazwę na górze
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // ustawia strzałkę

        //tworzenie shared preferences
        shar = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        // ustawienie texstu na Units
        if (shar.getBoolean(KEY_FOR_SHARED_PREF_SWITCH_UNITS, false)) {
            textViewUnits.setText(getResources().getString(R.string.units_imperial));
        } else {
            textViewUnits.setText(getResources().getString(R.string.units_metric));
        }

        // właczenie switcha jeśli był wcześniej zapisany w shar jako włączony
        switchUnits.setChecked(shar.getBoolean(KEY_FOR_SHARED_PREF_SWITCH_UNITS, false));

        switchUnits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchUnits.isChecked()) {
                    //zapisanie do shar włączenia Drive Mode
                    editor = shar.edit();//wywołany edytor do zmian
                    editor.putBoolean(KEY_FOR_SHARED_PREF_SWITCH_UNITS, true); // nadanie wartości dla włączonego switcha czyli unit imperial
                    editor.apply(); // musi być na końcu aby zapisać zmiany w shar
                    //ustawienie textu na imperial
                    textViewUnits.setText(getResources().getString(R.string.units_imperial));
                } else {
                    //zapisanie do shar wYłączenia Drive Mode
                    editor = shar.edit();//wywołany edytor do zmian
                    editor.putBoolean(KEY_FOR_SHARED_PREF_SWITCH_UNITS, false); // nadanie wartości dla włączonego switcha czyli unit metric
                    editor.apply(); // musi być na końcu aby zapisać zmiany w shar
                    //ustawienie textu na walk
                    textViewUnits.setText(getResources().getString(R.string.units_metric));
                }
            }
        });

    }

}
