package com.wolfmobileapps.myweatherforecast;


import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.ads.InterstitialAd;

import java.io.IOException;
import java.util.List;

import static com.wolfmobileapps.myweatherforecast.MainActivity.KEY_FOR_SHARED_PREF_SWITCH_CITY;
import static com.wolfmobileapps.myweatherforecast.MainActivity.KEY_FOR_SHARED_PREF_SWITCH_CITY_AND_COUNTRY_NAME;
import static com.wolfmobileapps.myweatherforecast.MainActivity.KEY_FOR_SHARED_PREF_SWITCH_CITY_NAME;
import static com.wolfmobileapps.myweatherforecast.MainActivity.KEY_SHARED_PREFERENCES_CITY_LATITUDE;
import static com.wolfmobileapps.myweatherforecast.MainActivity.KEY_SHARED_PREFERENCES_CITY_LONGITUDE;
import static com.wolfmobileapps.myweatherforecast.MainActivity.SHARED_PREFERENCES_NAME;
import static com.wolfmobileapps.myweatherforecast.MainActivity.advertisementIntedidtialID;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";

    public static final String KEY_FOR_SHARED_PREF_SWITCH_UNITS = "switchModeUnits";

    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    private TextView textViewUnits;
    private Switch switchUnits;

    private EditText editTextCity;
    private TextView textViewCityAdded;
    private Switch switchCity;
    private Button buttonCitySearch;
    private ProgressBar progressBarSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        textViewUnits = findViewById(R.id.textViewUnits);
        switchUnits = findViewById(R.id.switchUnits);
        editTextCity = findViewById(R.id.editTextCity);
        textViewCityAdded = findViewById(R.id.textViewCityAdded);
        switchCity = findViewById(R.id.switchCity);
        buttonCitySearch = findViewById(R.id.buttonCitySearch);
        progressBarSearch = findViewById(R.id.progressBarSearch);

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

        switchCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchCity.isChecked()) {
                    //zapisanie do shar wyłączenia city serch
                    editor = shar.edit();//wywołany edytor do zmian
                    editor.putBoolean(KEY_FOR_SHARED_PREF_SWITCH_CITY, true); // nadanie wartości dla wyłączonego switcha
                    editor.apply(); // musi być na końcu aby zapisać zmiany w shar
                    //wyczyszczenie textView z nazwą miasta
                    textViewCityAdded.setText("");
                    editTextCity.setText("");

                } else {
                    //zapisanie do shar włączenia city search
                    editor = shar.edit();//wywołany edytor do zmian
                    editor.putBoolean(KEY_FOR_SHARED_PREF_SWITCH_CITY, false); // nadanie wartości dla włączonego switcha
                    editor.apply(); // musi być na końcu aby zapisać zmiany w shar
                }
            }
        });

        // właczenie switcha jeśli nie był wcześniej zapisany w shar
        switchCity.setChecked(shar.getBoolean(KEY_FOR_SHARED_PREF_SWITCH_CITY, true));


        buttonCitySearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewCityAdded.setText("");
                // pobranie textu z edit text
                String cityName = editTextCity.getText().toString();
                //wywołanie metody do pobrania koordynatów danego miasta
                String cityAndCountryName = getCityCoordinates(cityName);
                // jeśli nie znalazł miasta to ustawi text że nie ma takiego miasta
                if (cityAndCountryName.equals(getResources().getString(R.string.NotFound))) {
                    textViewCityAdded.setText(getResources().getString(R.string.NotFound));
                    // wyłączenie progres bara
                    progressBarSearch.setVisibility(View.GONE);
                    return;
                }
                // jeśli znalazł miasto to ustawi jego nazwę w text View i zapisze do shared pref
                textViewCityAdded.setText(cityAndCountryName);
                editor = shar.edit();//wywołany edytor do zmian
                editor.putString(KEY_FOR_SHARED_PREF_SWITCH_CITY_AND_COUNTRY_NAME, cityAndCountryName);
                editor.putString(KEY_FOR_SHARED_PREF_SWITCH_CITY_NAME, cityName);
                editor.apply();
                editTextCity.setText("");

                // wyłaczenie switcha żeby nie działał wg aktualnej pozycji gps
                editor = shar.edit();//wywołany edytor do zmian
                editor.putBoolean(KEY_FOR_SHARED_PREF_SWITCH_CITY, false);
                editor.apply();
                switchCity.setChecked(false);
            }
        });
    }

    //pobranie przez geocoodera lat i lng z nazwy miasta
    private String getCityCoordinates(String cityName) {

        // włączenie progres bara
        progressBarSearch.setVisibility(View.VISIBLE);

        //uruchomienie geocodera
        if (Geocoder.isPresent()) {
            try {
                Geocoder gc = new Geocoder(this);
                List<Address> addresses = gc.getFromLocationName(cityName, 1); // get the found Address Objects
                if (addresses.size() == 0) {
                    return getResources().getString(R.string.NotFound);
                }
                Address address = addresses.get(0);
                String lat = "" + address.getLatitude();
                String lng = "" + address.getLongitude();
                String countryName = address.getCountryName();
                String cityAndCountryName = cityName + ", " + countryName;

                //zapisanie do shared pref lat i lng
                editor = shar.edit();//wywołany edytor do zmian
                editor.putString(KEY_SHARED_PREFERENCES_CITY_LATITUDE, lat);
                editor.putString(KEY_SHARED_PREFERENCES_CITY_LONGITUDE, lng);
                editor.apply();

                // wyłączenie progres bara
                progressBarSearch.setVisibility(View.GONE);
                return cityAndCountryName;
            } catch (IOException e) {
                // handle the exception
            } catch (Exception e) { //if wyżej załatwia sprawę ale to d;a bezpieczeństwaw dodano
                Log.d(TAG, "getCityCoordinates: exception catched");
            }
        }

        // wyłączenie progres bara
        progressBarSearch.setVisibility(View.GONE);
        return getResources().getString(R.string.NotFound);
    }
}
