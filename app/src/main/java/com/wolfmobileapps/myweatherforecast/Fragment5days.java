package com.wolfmobileapps.myweatherforecast;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static com.wolfmobileapps.myweatherforecast.MainActivity.SHARED_PREFERENCES_NAME;
import static com.wolfmobileapps.myweatherforecast.MainActivity.SHARED_PREFERENCES_WEATHER_5_DAYS;
import static com.wolfmobileapps.myweatherforecast.SettingsActivity.KEY_FOR_SHARED_PREF_SWITCH_UNITS;


public class Fragment5days extends Fragment {

    private static final String TAG = "Fragment5days";

    private SharedPreferences shar;
    private ListView listView;
    private ArrayList<ItemsToArrayAdapter> list;
    private ArrayAdapterToItems adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment5days, container, false);

        //tworzenie shared preferences
        shar = getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        listView = view.findViewById(R.id.listViewWeatherItems);
        list = new ArrayList<>();

        //załadowanie danych do listy
        takeJsonObject5dayWeather();

        adapter = new ArrayAdapterToItems(getContext(), 0, list);
        listView.setAdapter(adapter);

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
                String description = objectOne.getString("description"); //dokładniejszy opis tego co wyżej
                String icon = objectOne.getString("icon");

                // pobranie danych o temperaturze
                JSONObject objectTemperature = currentObject.getJSONObject("main");
                String pressure = "" + Math.round(objectTemperature.getDouble("pressure")) + " hPa";
                String temp = "" + Math.round(objectTemperature.getDouble("temp"));

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
                    dateToShowWeekDay = new SimpleDateFormat("EEEE").format(new Date((dateTimeTomorrow) * 1000));
                }


                //pobranie danych wiatru
                JSONObject objectWind = currentObject.getJSONObject("wind");
                int windSpeedDownloadedTomorrow = objectWind.getInt("speed"); //w metric m/s a w imperial miles/hour
                String windSpeed5Days = "" + (windSpeedDownloadedTomorrow * 3600) / 1000; //przeliczenie jeśli jest metric i ma buć km/h
                // jeśli jest ustawiony imperial to zwyklę mph bez przeliczania
                if (shar.getBoolean(KEY_FOR_SHARED_PREF_SWITCH_UNITS, false)) {
                    windSpeed5Days = "" + windSpeedDownloadedTomorrow;
                }
                String windUnit = " km/h";
                if (shar.getBoolean(KEY_FOR_SHARED_PREF_SWITCH_UNITS, false)) { // ustawienie Units imperial jeśli jest w shared zapisany, domyślnie jest metric
                    windUnit = " mph";
                }
                long windSpeedDouble = Math.round(Double.parseDouble(windSpeed5Days));
                String windName = windSpeedDouble + windUnit;

                ItemsToArrayAdapter item = new ItemsToArrayAdapter(dateToShowWeekDay, dateToShowDate, temp, icon, description,pressure,windName);
                list.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public Fragment5days() {
        // Required empty public constructor
    }

    public static Fragment5days newInstance() {
        Fragment5days fragment = new Fragment5days();
        return fragment;
    }

}
