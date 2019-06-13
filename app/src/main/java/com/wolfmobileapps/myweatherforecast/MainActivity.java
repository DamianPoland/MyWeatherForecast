package com.wolfmobileapps.myweatherforecast;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.wolfmobileapps.myweatherforecast.ui.main.SectionsPagerAdapter;

import org.json.JSONObject;

import java.util.Locale;

import static com.wolfmobileapps.myweatherforecast.SettingsActivity.KEY_FOR_SHARED_PREF_SWITCH_UNITS;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = "MainActivity";

    public static final String SHARED_PREFERENCES_NAME = "Shared Pref My Weather";
    public static final String SHARED_PREFERENCES_WEATHER_TODAY = "Shared Pref Weather Today";
    public static final String SHARED_PREFERENCES_WEATHER_5_DAYS = "Shared Pref Weather 5 Days";
    public static final String KEY_SHARED_PREFERENCES_CITY_LATITUDE = "Shared Pref city latitude";
    public static final String KEY_SHARED_PREFERENCES_CITY_LONGITUDE = "Shared Pref city longitude";

    // stała do permissions
    public static final int PERMISSION_ALL = 101;

    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    //views
    private TextView textViewWeatingForGPS;
    private ProgressBar progressBarWeatingForGPS;

    // string do Permissions
    private String[] permissions;

    //do lokalizacji
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        //views
        textViewWeatingForGPS = findViewById(R.id.textViewWeatingForGPS);
        progressBarWeatingForGPS = findViewById(R.id.progressBarWeatingForGPS);

        //zapytanie o permision
        permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (!hasPermissions(MainActivity.this, permissions)) {
            ActivityCompat.requestPermissions(MainActivity.this, permissions, PERMISSION_ALL);
        }

        //tworzenie shared preferences
        shar = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        //ustawienie napisu że czeka na gps za pierwszym razem
        if (shar.getString(SHARED_PREFERENCES_WEATHER_TODAY, "empty").equals("empty")) {
            textViewWeatingForGPS.setVisibility(View.VISIBLE);
            progressBarWeatingForGPS.setVisibility(View.VISIBLE);
        }

        //ustawienie widoczności dla menu options
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // stringi w razie czego do sprawdzenia  i wyświetlenia na tex view
//        String url = "https://vpic.nhtsa.dot.gov/api/vehicles/getallmakes?format=json"; // przykładowy z jsonem
//        String url = "http://api.openweathermap.org/data/2.5/weather?q=London&APPID=25af582c6ef05f38c33a169a50a70ec5"; // przykładowy z OpenWeatherMap
    }

    @Override
    protected void onStart() {
        super.onStart();
        // przy każdym otwarciu danego activity będzie pobierał z neta jsona aktualnego z pogadami
        makeUrlForDownloadWeather();

        //właczeni location managera gdy jest z powrotem z background
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 2000, this);
            Log.d(TAG, "onStart: locationManer requestLocationUpdates");
        }
    }

    //pobranie jsonobject przy pierwszym uruchomieniu
    @Override
    protected void onResume() {
        super.onResume();
        if ((shar.getString(SHARED_PREFERENCES_WEATHER_TODAY, "").equals("")) && (hasPermissions(MainActivity.this, permissions))) {
            makeUrlForDownloadWeather();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");

        //wyłaczeni location managera gdy jest on background
        if (locationManager != null) {
            locationManager.removeUpdates(this);
            Log.d(TAG, "onStop: locationManager removedUpdates");
        }
    }


    // zrobienie urk do ćiągnięcia actual weather i 5 days weather i potem wywołanie metod do zapidania ściągniętego stringa so shared preferences
    private void makeUrlForDownloadWeather() {

        //zapytanie o permision  - jeśli nie ma permission to nic nie robi
        if (!hasPermissions(MainActivity.this, permissions)) {
            ActivityCompat.requestPermissions(MainActivity.this, permissions, PERMISSION_ALL);
            //Toast.makeText(MainActivity.this, getResources().getString(R.string.permission_not_accepted), Toast.LENGTH_LONG).show();
            //ustawienie textu gdy nie ma permissions
            textViewWeatingForGPS.setText("");
            textViewWeatingForGPS.setText(getResources().getString(R.string.permissions_not_accepted_view));
            return;
        }
        // sprawdzenie czy GPS jest włączony
        if (!checkEnabledGPS()) {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.disabled_gps), Toast.LENGTH_LONG).show();
            //ustawienie textu gdy nie ma włączonego gps z pierwszym razem
            textViewWeatingForGPS.setText("");
            textViewWeatingForGPS.setText(getResources().getString(R.string.weating_for_gps_sygnal));
            return;
        }

        //ustawienie textu gdy szyka gps za pierwszym razem - potem robi go visibility gone on LocationChanged i go nie widać
        textViewWeatingForGPS.setText("");
        textViewWeatingForGPS.setText(getResources().getString(R.string.weating_for_gps_sygnal));

        //sprawdzeni czy jest połaczenie z internetem
        if (!isOnline()) {
            Toast.makeText(this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }


        // jeśli permissions są zaakceptowane to utworzy instancję Location managera
        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            //odświerzanie tylkko gdy sie zmieni dana lokalizacja o 2 km - nieważny czas - jest to dodatkowo zrobione w onStart!!!
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 2000, this);
            Log.d(TAG, "onStart: locationManer requestLocationUpdates");
        }


        //utworzenie stringów z url do pogody aktualnej (aktualizacja co 10 minut) i 5 dni do przodu (aktualizacja co 3 godziny)
        String scheme = "http://";
        String pathToActualWeather = "api.openweathermap.org/data/2.5/weather";
        String pathToForecastWeather = "api.openweathermap.org/data/2.5/forecast";
        String cityLatitude = shar.getString(KEY_SHARED_PREFERENCES_CITY_LATITUDE, "empty"); //domyślnie dla Londynu "51.507351"
        String cityLongitude = shar.getString(KEY_SHARED_PREFERENCES_CITY_LONGITUDE, "empty"); //domyślnie dla Londynu "-0.127758"
        if (cityLatitude.equals("empty") || cityLongitude.equals("empty")) {
            return;
        }
        String units = "&units=metric"; // standard czyli metric jest w Cencjuszch
        String language = ""; // potem jest if do tego aby zmienić napolski
        String keyForOpenWeatherMapAcces = "&APPID=25af582c6ef05f38c33a169a50a70ec5";

        // ustawienie Units imperial jeśli jest w shared zapisany, domyślnie jest metric
        if (shar.getBoolean(KEY_FOR_SHARED_PREF_SWITCH_UNITS, false)) {
            units = "&units=imperial"; //temp w Farentheit
        }

        //ustawienie języka
        String currencLanguage = Locale.getDefault().getDisplayLanguage();
        if (currencLanguage.equals("polski")) {
            language = "&lang=pl"; // bez tego jes po angielsku
        }

        // stringi końcowe
        String urlActualWeather = scheme + pathToActualWeather + "?lat=" + cityLatitude + "&lon=" + cityLongitude + units + language + keyForOpenWeatherMapAcces;
        String urlForecast5days = scheme + pathToForecastWeather + "?lat=" + cityLatitude + "&lon=" + cityLongitude + units + language + keyForOpenWeatherMapAcces;

        //ściąga JsonObject z podaeno stringa url i zapisuje do shared preferences
        downloadJsonObjectFromStringURL(urlActualWeather, 0);
        downloadJsonObjectFromStringURL(urlForecast5days, 1);

    }

    //ściąga JsonObject z podaeno stringa url i zapisuje do shared preferences
    private void downloadJsonObjectFromStringURL(String url, final int separator) {
        RequestQueue queue = Volley.newRequestQueue(this); // utworzenie requst - może być inne np o stringa lub JsonArrray
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                // zapisanie JsonObject w stringu i dodanie to shared preferences
                String resp = response.toString();
                if (separator == 0) {
                    editor = shar.edit();
                    editor.putString(SHARED_PREFERENCES_WEATHER_TODAY, resp);
                    editor.apply();
                    //Toast.makeText(MainActivity.this, "connected actual", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onResponse actual: " + response.toString());
                    refreshFragmentAfterdownloadedData();

                } else {
                    editor = shar.edit();
                    editor.putString(SHARED_PREFERENCES_WEATHER_5_DAYS, resp);
                    editor.apply();
                    //Toast.makeText(MainActivity.this, "connected to 5 days", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onResponse 5 days: " + response.toString());
                    refreshFragmentAfterdownloadedData();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // do something when don"t getJSONObject
                Toast.makeText(MainActivity.this, getResources().getString(R.string.error_not_respond), Toast.LENGTH_LONG).show();
            }
        });
        queue.add(jsonObjectRequest); //wywołanie klasy
    }

    //aktualizacja fragmentu po zaktualizowaniu danych
    private void refreshFragmentAfterdownloadedData() {
        Fragment fragmentToday = new FragmentToday();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentToday, fragmentToday, "FragmentToday");
        ft.commit();

        Fragment fragment5days = new Fragment5days();
        FragmentTransaction ft5days = getSupportFragmentManager().beginTransaction();
        ft5days.replace(R.id.fragment5days, fragment5days, "Fragment5days");
        ft5days.commit();
    }


    // górne menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSettings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.menuInfo:
                startActivity(new Intent(MainActivity.this, InfoActivity.class));
                break;
            case R.id.menuRefresh:
                makeUrlForDownloadWeather();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //metoda do sprawdzenia czy są nadane permisssions
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    // sprawdzenie czy GPS jest właczony
    private boolean checkEnabledGPS() {
        LocationManager manager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.e("TAG", "Gps already enabled");
            return false;
        }
        return true;
    }

    //sprawdzenie czy jest włączony internet
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onLocationChanged(Location location) {
        String lat = "" + location.getLatitude();
        String lng = "" + location.getLongitude();
        editor = shar.edit();
        editor.putString(KEY_SHARED_PREFERENCES_CITY_LATITUDE, lat);
        editor.putString(KEY_SHARED_PREFERENCES_CITY_LONGITUDE, lng);
        editor.apply();
        makeUrlForDownloadWeather();
        Log.d(TAG, "onLocationChanged: " + "lat: " + lat + "lng: " + lng);

        //wyłaczenie text view z weating for gps sygnal po pierwszym uruchomieniu
        textViewWeatingForGPS.setVisibility(View.GONE);
        progressBarWeatingForGPS.setVisibility(View.GONE);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged: GPS working");

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}