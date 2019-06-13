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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static com.wolfmobileapps.myweatherforecast.MainActivity.SHARED_PREFERENCES_NAME;
import static com.wolfmobileapps.myweatherforecast.MainActivity.SHARED_PREFERENCES_WEATHER_5_DAYS;
import static com.wolfmobileapps.myweatherforecast.MainActivity.SHARED_PREFERENCES_WEATHER_TODAY;
import static com.wolfmobileapps.myweatherforecast.SettingsActivity.KEY_FOR_SHARED_PREF_SWITCH_UNITS;


public class FragmentToday extends Fragment {
    public static final String TAG = "FragmentToday";
    public static final String SHARED_PREFERENCES_CITY_NAME = "city name";

    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    //views
    private View view;
    private TextView textViewCity;
    private TextView textViewDay;
    private TextView textViewDate;
    private TextView textViewTemperature;
    private TextView textViewTemperatureMax;
    private TextView textViewTemperatureMin;
    private TextView textViewSkyDescription;
    private TextView textViewWind;
    private TextView textViewPressure;
    private TextView textViewSun;
    private TextView textViewSunRise;
    private TextView textViewSunSet;
    private ImageView imageViewSkyIcon;
    private ScrollView scrollViewForColourChange;
    private android.support.design.widget.AppBarLayout widgetAppBarLayout;
    private LineChart lineChart;

    //dane z JsonObject
    private String main; //śnieg deszcz chmury itd
    private String description; //dokładniejszy opis tego co wyżej
    private String icon = "";
    private double temp;
    private String pressure = ""; // w hPa
    private String humidity; // w %
    private double temp_nim;
    private double temp_max;
    private String windSpeed = ""; //w metric km/h a w imperial miles/hour
    private String clouds; //zachmurzenie w %
    private long dateTime = 0;
    private long sunrise;
    private long sunset;
    private String cityName;

    //listy do wykresu
    private ArrayList<Entry> entries;
    private ArrayList<String> descriptionOfTable;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_fragment_today, container, false);

        //views
        textViewCity = view.findViewById(R.id.textViewCity);
        textViewDate = view.findViewById(R.id.textViewDate);
        textViewDay = view.findViewById(R.id.textViewDay);
        textViewTemperature = view.findViewById(R.id.textViewTemperature);
        textViewTemperatureMax = view.findViewById(R.id.textViewMaxTemp);
        textViewTemperatureMin = view.findViewById(R.id.textViewMinTemp);
        textViewSkyDescription = view.findViewById(R.id.textViewSkyDescription);
        imageViewSkyIcon = view.findViewById(R.id.imageViewSkyIcon);
        textViewWind = view.findViewById(R.id.textViewWind);
        textViewPressure = view.findViewById(R.id.textViewPreasure);
        textViewSunRise = view.findViewById(R.id.textViewSunRise);
        textViewSunSet = view.findViewById(R.id.textViewSuSet);
        lineChart = view.findViewById(R.id.lineChart);

        //tworzenie shared preferences
        shar = getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        takeJsonObjectActualWeather();

        makeChart();

        return view;
    }

    private void makeChart() {

        //utworzenie wykresu z punktami x i y na wykresie
        entries = new ArrayList<>();

        // utworzenie stringów z opisu na osi X wykresu
        descriptionOfTable = new ArrayList<>();

        //pobranie danych z shar pref i zapisanie w arraylistach
        //pobranie stringa ze shared pref w który był wcześniej zapisany json 5 days z neta ściągnięty
        String stringJsonObjectForSharedPref = shar.getString(SHARED_PREFERENCES_WEATHER_5_DAYS, "0");
        try {
            //przekszrałcenie stringa pobranego ze shared pref na jsonObject
            JSONObject jsonObject = new JSONObject(stringJsonObjectForSharedPref);

            JSONArray arrayList = jsonObject.getJSONArray("list");
            for (int i = 0; i < arrayList.length(); i++) {
                JSONObject currentObject = arrayList.getJSONObject(i);

                // pobranie danych o temperaturze
                JSONObject objectTemperature = currentObject.getJSONObject("main");
                long temp = Math.round(objectTemperature.getDouble("temp"));
                //zapisanie do arrayList
                entries.add(new Entry(i, temp));

                // pobranie daty
                long dateTimeTomorrow = currentObject.getLong("dt");
                //data - longa wziętego z bazy trzeba pomnożyć razy 1000 bo jest podana w unix system czyli w sekundach a nie milisekundach
                String dateToShowDate = "";
                String dateToShowWeekDay = "";
                if (dateTimeTomorrow != 0) {
                    dateToShowDate = new SimpleDateFormat("HH:mm").format(new Date((dateTimeTomorrow) * 1000));
                    if (shar.getBoolean(KEY_FOR_SHARED_PREF_SWITCH_UNITS, false)) { // jeśli jest ustawiony imperial to inny format daty
                        int timeToImperial = Integer.parseInt(new SimpleDateFormat("HH").format(new Date((dateTimeTomorrow) * 1000)));
                        if (timeToImperial>12) {
                            dateToShowDate = timeToImperial-12 + ":" + new SimpleDateFormat("mm").format(new Date((dateTimeTomorrow) * 1000)) + " PM";
                        }else{
                            dateToShowDate = new SimpleDateFormat("HH:mm").format(new Date((dateTimeTomorrow) * 1000)) + " AM";

                        }
                    }
                    dateToShowWeekDay = new SimpleDateFormat("EE").format(new Date((dateTimeTomorrow) * 1000));
                }

                //zapidanie stringa opisującego oś X do arraylist
                descriptionOfTable.add(dateToShowWeekDay + "\n" +dateToShowDate);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //utworzenie wykresu
        ValueFormatter form = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return ((int)value) + "°";
            }
        };
        LineDataSet dataSet = new LineDataSet(entries, getResources().getString(R.string.next_days_temperature));
        dataSet.setColor(Color.WHITE);
        dataSet.setFillColor(Color.GRAY);
        dataSet.setValueFormatter(form);
        dataSet.setLineWidth(2f); //grubość linii
        dataSet.setValueTextSize(10f); // wielkość tekstu
        dataSet.setValueTextColor(Color.WHITE);
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.setVisibleXRangeMaximum(15); //ustawia ile ma być widocznych maxymalnie punktów na początku
        lineChart.setExtraOffsets(10,0,0,0); // odsuniecie wykresu od każdej strony jeśli np napisy wchodzą
        lineChart.setBackgroundColor(Color.BLUE); //ustawienie koloru tła
        lineChart.getAxisRight().setEnabled(false); //wyłączenie prawej osi Y
        lineChart.getAxisLeft().setEnabled(false); //wyłączenie prawej osi Y
        lineChart.setExtraOffsets(15,10,10,5); //odsuniecie samego wykresu

        //ustawienie label w tabelce
        Legend legend = lineChart.getLegend();
        legend.setTextColor(Color.WHITE);
        legend.setTextSize(20f);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);

        //przekształcenie arrayList na tablicę stringów stringArray
        final String[] quarters = new String[descriptionOfTable.size()];
        for (int i = 0; i < descriptionOfTable.size(); i++) {
            quarters[i] = descriptionOfTable.get(i);
        }

        //wstawienie opisu osi X
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return quarters[(int) value];
            }
        };
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1); //ustawia odseępy
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // ustawia pozycję na wykresie
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setAxisLineWidth(2f); //szerokość linii
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setLabelRotationAngle(30f); //obrót textu
        xAxis.setValueFormatter(formatter);
    }


    // rozpakowuje danego Jsona z ActualWeather
    public void takeJsonObjectActualWeather() {

        //pobranie stringa ze shared pref w który był wcześniej zapisany json 5 days z neta ściągnięty
        String stringJsonObjectForSharedPref = shar.getString(SHARED_PREFERENCES_WEATHER_TODAY, "0");

        try {

            //przekszrałcenie stringa pobranego ze shared pref na jsonObject
            JSONObject jsonObject = new JSONObject(stringJsonObjectForSharedPref);
            Log.d(TAG, "takeJsonObjectActualWeather: " + jsonObject);

            //pobranie danych zaachmurzenia
            JSONArray arrayWeather = jsonObject.getJSONArray("weather");
            JSONObject objectOne = arrayWeather.getJSONObject(0);
            main = objectOne.getString("main");
            description = objectOne.getString("description");
            icon = objectOne.getString("icon");

            // pobranie danych o temperaturze
            JSONObject objectTemperature = jsonObject.getJSONObject("main");
            temp = objectTemperature.getDouble("temp");
            pressure = objectTemperature.getString("pressure");
            humidity = objectTemperature.getString("humidity");
            temp_nim = objectTemperature.getDouble("temp_min");
            temp_max = objectTemperature.getDouble("temp_max");

            //pobranie danych wiatru
            JSONObject objectWind = jsonObject.getJSONObject("wind");
            int windSpeedDownloaded = objectWind.getInt("speed");
            windSpeed = "" + (windSpeedDownloaded * 3600) / 1000; //przeliczenie jeśli jest metric i ma buć km/h
            // jeśli jest ustawiony imperial to zwyklę mph bez przeliczania
            if (shar.getBoolean(KEY_FOR_SHARED_PREF_SWITCH_UNITS, false)) {
                windSpeed = "" + windSpeedDownloaded;
            }

            // pobranie zachmurzenia
            JSONObject objectCouds = jsonObject.getJSONObject("clouds");
            clouds = objectCouds.getString("all");

            // pobranie daty
            dateTime = jsonObject.getLong("dt");

            //pobranie wschód i zachód słońca
            JSONObject objectSunriseSunset = jsonObject.getJSONObject("sys");
            sunrise = objectSunriseSunset.getLong("sunrise");
            sunset = objectSunriseSunset.getLong("sunset");

            //pobranie nazwy miasta i zapisaniedo shared pref
            cityName = jsonObject.getString("name");
            editor = shar.edit();
            editor.putString(SHARED_PREFERENCES_CITY_NAME, cityName);
            editor.apply();


            String toDisplay = main + "\n" + description + "\n" + icon + "\n" + temp + "\n" + pressure + "\n" + humidity + "\n" + temp_nim + "\n" + temp_max + "\n" + windSpeed + "\n" + clouds + "\n" + dateTime + "\n" + sunrise + "\n" + sunset + "\n" + cityName;

            Log.d(TAG, "takeJsonObjectActualWeather: " + toDisplay);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // dodanie danych do views
        loadAllInfoOnView();
    }

    //załadowanie wszystkich views z danymi
    public void loadAllInfoOnView() {
        //city Name
        textViewCity.setText(cityName);

        //data - longa wziętego z bazy trzeba pomnożyć razy 1000 bo jest podana w unix system czyli w sekundach a nie milisekundach
        if (dateTime != 0) {
            String dateToShowDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date((dateTime) * 1000));
            if (shar.getBoolean(KEY_FOR_SHARED_PREF_SWITCH_UNITS, false)) { // jeśli jest ustawiony imperial to inny format daty
                dateToShowDate = new SimpleDateFormat("MM.dd.yyyy").format(new Date((dateTime) * 1000));
            }
            textViewDate.setText(dateToShowDate);
            String dateToShowWeekDay = new SimpleDateFormat("EEEE").format(new Date((dateTime) * 1000));
            textViewDay.setText(dateToShowWeekDay);
        }

        // temperatury
        textViewTemperature.setText(Math.round(temp) + "°");
        textViewTemperatureMax.setText(Math.round(temp_max) + "°");
        textViewTemperatureMin.setText(Math.round(temp_nim) + "°");

        // sky description
        textViewSkyDescription.setText(description);

        // icon of sky
        if (!icon.equals("")) {
            switch (icon) {
                case "01d":
                    imageViewSkyIcon.setImageDrawable(getResources().getDrawable(R.drawable.sunny_transparent));
                    break;
                case "02d":
                    imageViewSkyIcon.setImageDrawable(getResources().getDrawable(R.drawable.partly_sunny_transparent));
                    break;
                case "03d":
                    imageViewSkyIcon.setImageDrawable(getResources().getDrawable(R.drawable.cloudy_transparent));
                    break;
                case "04d":
                    imageViewSkyIcon.setImageDrawable(getResources().getDrawable(R.drawable.cloudy_transparent));
                    break;
                case "09d":
                    imageViewSkyIcon.setImageDrawable(getResources().getDrawable(R.drawable.raining_transparent));
                    break;
                case "10d":
                    imageViewSkyIcon.setImageDrawable(getResources().getDrawable(R.drawable.raining_transparent));
                    break;
                case "11d":
                    imageViewSkyIcon.setImageDrawable(getResources().getDrawable(R.drawable.thunderstorms_transparent));
                    break;
                case "13d":
                    imageViewSkyIcon.setImageDrawable(getResources().getDrawable(R.drawable.snowing_transparent));
                    break;
                case "50d":
                    imageViewSkyIcon.setImageDrawable(getResources().getDrawable(R.drawable.cloudy_transparent));
                    break;
                case "01n":
                    imageViewSkyIcon.setImageDrawable(getResources().getDrawable(R.drawable.moon_transparent));
                    break;
                case "02n":
                    imageViewSkyIcon.setImageDrawable(getResources().getDrawable(R.drawable.moon_transparent));
                    break;
                case "03n":
                    imageViewSkyIcon.setImageDrawable(getResources().getDrawable(R.drawable.cloudy_transparent));
                    break;
                case "04n":
                    imageViewSkyIcon.setImageDrawable(getResources().getDrawable(R.drawable.cloudy_transparent));
                    break;
                case "09n":
                    imageViewSkyIcon.setImageDrawable(getResources().getDrawable(R.drawable.raining_transparent));
                    break;
                case "10n":
                    imageViewSkyIcon.setImageDrawable(getResources().getDrawable(R.drawable.raining_transparent));
                    break;
                case "11n":
                    imageViewSkyIcon.setImageDrawable(getResources().getDrawable(R.drawable.thunderstorms_transparent));
                    break;
                case "13n":
                    imageViewSkyIcon.setImageDrawable(getResources().getDrawable(R.drawable.snowing_transparent));
                    break;
                case "50n":
                    imageViewSkyIcon.setImageDrawable(getResources().getDrawable(R.drawable.cloudy_transparent));
                    break;
            }
        }

        // wind
        String windUnit = "km/h";
        if (shar.getBoolean(KEY_FOR_SHARED_PREF_SWITCH_UNITS, false)) { // ustawienie Units imperial jeśli jest w shared zapisany, domyślnie jest metric
            windUnit = "mph";
        }
        Log.d(TAG, "loadAllInfoOnView: windSpeed: " + windSpeed);
        if (windSpeed.equals("")) {
            return;
        }
        long windSpeedDouble = Math.round(Double.parseDouble(windSpeed));
        String windName = getResources().getString(R.string.wind) + "\n" + windSpeedDouble + windUnit;
        textViewWind.setText(windName);

        //preassure
        long pressureDouble = Math.round(Double.parseDouble(pressure));
        String pressureName = getResources().getString(R.string.pressure) + "\n" + pressureDouble + "hPa";
        textViewPressure.setText(pressureName);

        // sunrise and sunset - longa wziętego z bazy trzeba pomnożyć razy 1000 bo jest podana w unix system czyli w sekundach a nie milisekundach
        String sunriseToShow = new SimpleDateFormat("HH:mm").format(new Date((sunrise) * 1000));
        String sunriseName = getResources().getString(R.string.sunrise) + "\n" + sunriseToShow;
        String sunsetToShow = new SimpleDateFormat("HH:mm").format(new Date((sunset) * 1000));
        String sunsetName = getResources().getString(R.string.sunset) + "\n" + sunsetToShow;
        if (shar.getBoolean(KEY_FOR_SHARED_PREF_SWITCH_UNITS, false)) { // ustawienie Units imperial jeśli jest w shared zapisany, domyślnie jest metric
            sunriseName = getResources().getString(R.string.sunrise) + "\n" + sunriseToShow + " AM";
            String sunsetToShowHH = new SimpleDateFormat("HH").format(new Date((sunset) * 1000));
            int sunsetIntMinus12 = Integer.parseInt(sunsetToShowHH) - 12;
            String sunsetToShowmm = new SimpleDateFormat("mm").format(new Date((sunset) * 1000));
            sunsetName = getResources().getString(R.string.sunset) + "\n" + sunsetIntMinus12 + ":" + sunsetToShowmm + " PM";
        }
        textViewSunRise.setText(sunriseName);
        textViewSunSet.setText(sunsetName);
    }

    public FragmentToday() {
        // Required empty public constructor
    }

    public static FragmentToday newInstance() {
        FragmentToday fragment = new FragmentToday();
        return fragment;
    }

}
