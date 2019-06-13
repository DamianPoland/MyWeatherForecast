package com.wolfmobileapps.myweatherforecast;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static com.wolfmobileapps.myweatherforecast.FragmentToday.SHARED_PREFERENCES_CITY_NAME;
import static com.wolfmobileapps.myweatherforecast.MainActivity.SHARED_PREFERENCES_NAME;
import static com.wolfmobileapps.myweatherforecast.MainActivity.SHARED_PREFERENCES_WEATHER_5_DAYS;
import static com.wolfmobileapps.myweatherforecast.SettingsActivity.KEY_FOR_SHARED_PREF_SWITCH_UNITS;

public class FragmentTomorrow extends Fragment {

    private static final String TAG = "FragmentTomorrow";

    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    //views
    private View view;
    private TextView textViewCityTomorrow;
    private TextView textViewDayTomorrow;
    private TextView textViewDateTomorrow;
    private TextView textViewTemperatureTomorrow;
    private TextView textViewTemperatureMaxTomorrow;
    private TextView textViewTemperatureMinTomorrow;
    private TextView textViewSkyDescriptionTomorrow;
    private TextView textViewWindTomorrow;
    private TextView textViewPressureTomorrow;
    private ImageView imageViewSkyIconTomorrow;
    private ScrollView scrollViewForColourChangeTomorrow;
    private android.support.design.widget.AppBarLayout widgetAppBarLayoutTomorrow;

    //dane z JsonObject
    private String mainTomorrow; //śnieg deszcz chmury itd
    private String descriptionTomorrow; //dokładniejszy opis tego co wyżej
    private String iconTomorrow = "";
    private double tempTomorrow;
    private String pressureTomorrow = ""; // w hPa
    private String humidityTomorrow; // w %
    private double temp_nimTomorrow;
    private double temp_maxTomorrow;
    private String windSpeedTomorrow = ""; //w metric km/h a w imperial miles/hour
    private String cloudsTomorrow; //zachmurzenie w %
    private long dateTimeTomorrow = 0;
    private String cityNameTomorrow;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fragment_tomorrow, container, false);

        //change background colot
        int backgroundColorOfThisView = getResources().getColor(R.color.color_background_second_fragment);
        scrollViewForColourChangeTomorrow = view.findViewById(R.id.scrollViewForColourChangeTomorrow);
        scrollViewForColourChangeTomorrow.setBackgroundColor(backgroundColorOfThisView);

        //views
        textViewCityTomorrow = view.findViewById(R.id.textViewCityTomorrow);
        textViewDateTomorrow = view.findViewById(R.id.textViewDateTomorrow);
        textViewDayTomorrow = view.findViewById(R.id.textViewDayTomorrow);
        textViewTemperatureTomorrow = view.findViewById(R.id.textViewTemperatureTomorrow);
        textViewTemperatureMaxTomorrow = view.findViewById(R.id.textViewMaxTempTomorrow);
        textViewTemperatureMinTomorrow = view.findViewById(R.id.textViewMinTempTomorrow);
        textViewSkyDescriptionTomorrow = view.findViewById(R.id.textViewSkyDescriptionTomorrow);
        imageViewSkyIconTomorrow = view.findViewById(R.id.imageViewSkyIconTomorrow);
        textViewWindTomorrow = view.findViewById(R.id.textViewWindTomorrow);
        textViewPressureTomorrow = view.findViewById(R.id.textViewPreasureTomorrow);

        //tworzenie shared preferences
        shar = getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        takeJsonObject5dayWeather();

        return view;
    }

    // rozpakowuje danego Jsona 5 day weather ( trochę się rózni od ActualWeather)
    private void takeJsonObject5dayWeather() {

        //pobranie stringa ze shared pref w który był wcześniej zapisany json 5 days z neta ściągnięty
        String stringJsonObjectForSharedPref = shar.getString(SHARED_PREFERENCES_WEATHER_5_DAYS, "0");

        try {

            //przekszrałcenie stringa pobranego ze shared pref na jsonObject
            JSONObject jsonObject = new JSONObject(stringJsonObjectForSharedPref);

            JSONArray arrayList = jsonObject.getJSONArray("list");
            for (int i = 0; i < arrayList.length(); i++) {
                JSONObject currentObject = arrayList.getJSONObject(i);

                //pobranie danych zaachmurzenia
                JSONArray arrayWeather = currentObject.getJSONArray("weather");
                JSONObject objectOne = arrayWeather.getJSONObject(0);
                mainTomorrow = objectOne.getString("main"); //śnieg deszcz chmury itd
                descriptionTomorrow = objectOne.getString("description"); //dokładniejszy opis tego co wyżej
                iconTomorrow = objectOne.getString("icon");

                // pobranie danych o temperaturze
                JSONObject objectTemperature = currentObject.getJSONObject("main");
                tempTomorrow = objectTemperature.getDouble("temp");
                pressureTomorrow = objectTemperature.getString("pressure"); // w hPa
                humidityTomorrow = objectTemperature.getString("humidity"); // w %
                temp_nimTomorrow = objectTemperature.getDouble("temp_min");
                temp_maxTomorrow = objectTemperature.getDouble("temp_max");

                //pobranie danych wiatru
                JSONObject objectWind = currentObject.getJSONObject("wind");
                int windSpeedDownloadedTomorrow = objectWind.getInt("speed"); //w metric m/s a w imperial miles/hour
                windSpeedTomorrow = "" + (windSpeedDownloadedTomorrow * 3600) / 1000; //przeliczenie jeśli jest metric i ma buć km/h
                // jeśli jest ustawiony imperial to zwyklę mph bez przeliczania
                if (shar.getBoolean(KEY_FOR_SHARED_PREF_SWITCH_UNITS, false)) {
                    windSpeedTomorrow = "" + windSpeedDownloadedTomorrow;
                }

                // pobranie zachmurzenia
                JSONObject objectCouds = currentObject.getJSONObject("clouds");
                cloudsTomorrow = "" + objectCouds.getString("all"); //zachmurzenie w %

                // pobranie daty
                dateTimeTomorrow = currentObject.getLong("dt");

                //pobranie wschód i zachód słońca
                //BRAK wzgędem ActualWeather !!!

                //pobranie nazwy miasta
                //BRAK względem ActualWeather

                String toDisplay = mainTomorrow + "\n" + descriptionTomorrow + "\n" + iconTomorrow + "\n" + tempTomorrow + "\n" + pressureTomorrow + "\n" + humidityTomorrow + "\n" + temp_nimTomorrow + "\n" + temp_maxTomorrow + "\n" + windSpeedTomorrow + "\n" + cloudsTomorrow + "\n" + dateTimeTomorrow + "\n\n\n";
                Log.d(TAG, "takeJsonObject5dayWeather: " + toDisplay);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        loadAllInfoOnViewTomorrow();
    }

    //załadowanie wszystkich views z danymi
    public void loadAllInfoOnViewTomorrow() {

        //city Name
        cityNameTomorrow = shar.getString(SHARED_PREFERENCES_CITY_NAME, "");
        textViewCityTomorrow.setText(cityNameTomorrow);

        //data - longa wziętego z bazy trzeba pomnożyć razy 1000 bo jest podana w unix system czyli w sekundach a nie milisekundach
        if (dateTimeTomorrow != 0) {
            String dateToShowDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date((dateTimeTomorrow) * 1000));
            if (shar.getBoolean(KEY_FOR_SHARED_PREF_SWITCH_UNITS, false)) { // jeśli jest ustawiony imperial to inny format daty
                dateToShowDate = new SimpleDateFormat("MM.dd.yyyy").format(new Date((dateTimeTomorrow) * 1000));
            }
            textViewDateTomorrow.setText(dateToShowDate);
            String dateToShowWeekDay = new SimpleDateFormat("EEEE").format(new Date((dateTimeTomorrow) * 1000));
            textViewDayTomorrow.setText(dateToShowWeekDay);
        }

        // temperatury
        textViewTemperatureTomorrow.setText(Math.round(tempTomorrow) + "°");
        textViewTemperatureMaxTomorrow.setText(Math.round(temp_maxTomorrow) + "°");
        textViewTemperatureMinTomorrow.setText(Math.round(temp_nimTomorrow) + "°");

        // sky description
        textViewSkyDescriptionTomorrow.setText(descriptionTomorrow);

        // icon of sky
        if (!iconTomorrow.equals("")) {
            switch (iconTomorrow) {
                case "01d":
                    imageViewSkyIconTomorrow.setImageDrawable(getResources().getDrawable(R.drawable.sunny_transparent));
                    break;
                case "02d":
                    imageViewSkyIconTomorrow.setImageDrawable(getResources().getDrawable(R.drawable.partly_sunny_transparent));
                    break;
                case "03d":
                    imageViewSkyIconTomorrow.setImageDrawable(getResources().getDrawable(R.drawable.cloudy_transparent));
                    break;
                case "04d":
                    imageViewSkyIconTomorrow.setImageDrawable(getResources().getDrawable(R.drawable.cloudy_transparent));
                    break;
                case "09d":
                    imageViewSkyIconTomorrow.setImageDrawable(getResources().getDrawable(R.drawable.raining_transparent));
                    break;
                case "10d":
                    imageViewSkyIconTomorrow.setImageDrawable(getResources().getDrawable(R.drawable.raining_transparent));
                    break;
                case "11d":
                    imageViewSkyIconTomorrow.setImageDrawable(getResources().getDrawable(R.drawable.thunderstorms_transparent));
                    break;
                case "13d":
                    imageViewSkyIconTomorrow.setImageDrawable(getResources().getDrawable(R.drawable.snowing_transparent));
                    break;
                case "50d":
                    imageViewSkyIconTomorrow.setImageDrawable(getResources().getDrawable(R.drawable.cloudy_transparent));
                    break;
                case "01n":
                    imageViewSkyIconTomorrow.setImageDrawable(getResources().getDrawable(R.drawable.moon_transparent));
                    break;
                case "02n":
                    imageViewSkyIconTomorrow.setImageDrawable(getResources().getDrawable(R.drawable.moon_transparent));
                    break;
                case "03n":
                    imageViewSkyIconTomorrow.setImageDrawable(getResources().getDrawable(R.drawable.cloudy_transparent));
                    break;
                case "04n":
                    imageViewSkyIconTomorrow.setImageDrawable(getResources().getDrawable(R.drawable.cloudy_transparent));
                    break;
                case "09n":
                    imageViewSkyIconTomorrow.setImageDrawable(getResources().getDrawable(R.drawable.raining_transparent));
                    break;
                case "10n":
                    imageViewSkyIconTomorrow.setImageDrawable(getResources().getDrawable(R.drawable.raining_transparent));
                    break;
                case "11n":
                    imageViewSkyIconTomorrow.setImageDrawable(getResources().getDrawable(R.drawable.thunderstorms_transparent));
                    break;
                case "13n":
                    imageViewSkyIconTomorrow.setImageDrawable(getResources().getDrawable(R.drawable.snowing_transparent));
                    break;
                case "50n":
                    imageViewSkyIconTomorrow.setImageDrawable(getResources().getDrawable(R.drawable.cloudy_transparent));
                    break;
            }
        }

        // wind
        String windUnit = "km/h";
        if (shar.getBoolean(KEY_FOR_SHARED_PREF_SWITCH_UNITS, false)) { // ustawienie Units imperial jeśli jest w shared zapisany, domyślnie jest metric
            windUnit = "mph";
        }
        if (windSpeedTomorrow.equals("")){
            return;
        }
        long windSpeedDouble = Math.round(Double.parseDouble(windSpeedTomorrow));
        String windName = getResources().getString(R.string.wind) + "\n" + windSpeedDouble + windUnit;
        textViewWindTomorrow.setText(windName);

        //preassure
        long pressureDouble = Math.round(Double.parseDouble(pressureTomorrow));
        String pressureName = getResources().getString(R.string.pressure) + "\n" + pressureDouble + "hPa";
        textViewPressureTomorrow.setText(pressureName);
    }

    public FragmentTomorrow() {
        // Required empty public constructor
    }

    public static FragmentTomorrow newInstance() {
        FragmentTomorrow fragment = new FragmentTomorrow();
        return fragment;
    }

}
