package com.wolfmobileapps.myweatherforecast;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ArrayAdapterToItems extends ArrayAdapter<ItemsToArrayAdapter> {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemsToArrayAdapter currentItem = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_to_array_adapter, parent, false);
        }
        TextView day = convertView.findViewById(R.id.textViewDay5Days);
        TextView date = convertView.findViewById(R.id.textViewDate5Days);
        TextView temp = convertView.findViewById(R.id.textViewTemperature5Days);
        TextView desc = convertView.findViewById(R.id.textViewDescription5Days);
        TextView wind = convertView.findViewById(R.id.textViewWind5Days);
        TextView pressure = convertView.findViewById(R.id.textViewPressure5Days);

        day.setText(currentItem.getDay());
        date.setText(currentItem.getDate());
        temp.setText(currentItem.getTemp() + "°");
        desc.setText(currentItem.getDescription());
        wind.setText(currentItem.getWind());
        pressure.setText(currentItem.getPressure());

        ImageView icon = convertView.findViewById(R.id.imageView5Days);
        String iconString = currentItem.getIcon();

        if (!iconString.equals("")) {
            switch (iconString) {
                case "01d":
                    icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.sunny_transparent));
                    break;
                case "02d":
                    icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.partly_sunny_transparent));
                    break;
                case "03d":
                    icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.cloudy_transparent));
                    break;
                case "04d":
                    icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.cloudy_transparent));
                    break;
                case "09d":
                    icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.raining_transparent));
                    break;
                case "10d":
                    icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.raining_transparent));
                    break;
                case "11d":
                    icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.thunderstorms_transparent));
                    break;
                case "13d":
                    icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.snowing_transparent));
                    break;
                case "50d":
                    icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.cloudy_transparent));
                    break;
                case "01n":
                    icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.moon_transparent));
                    break;
                case "02n":
                    icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.moon_transparent));
                    break;
                case "03n":
                    icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.cloudy_transparent));
                    break;
                case "04n":
                    icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.cloudy_transparent));
                    break;
                case "09n":
                    icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.raining_transparent));
                    break;
                case "10n":
                    icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.raining_transparent));
                    break;
                case "11n":
                    icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.thunderstorms_transparent));
                    break;
                case "13n":
                    icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.snowing_transparent));
                    break;
                case "50n":
                    icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.cloudy_transparent));
                    break;
            }
        }
        return convertView;
    }

    public ArrayAdapterToItems(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

}
