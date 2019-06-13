package com.wolfmobileapps.myweatherforecast;

public class ItemsToArrayAdapter {

    private String day;
    private String date;
    private String temp;
    private String icon;
    private String description;
    private String pressure;
    private String wind;



    public ItemsToArrayAdapter(String day, String date, String temp, String icon, String description, String pressure, String wind) {
        this.day = day;
        this.date = date;
        this.temp = temp;
        this.icon = icon;
        this.description = description;
        this.pressure = pressure;
        this.wind = wind;
    }
    public String getPressure() {
        return pressure;
    }

    public String getWind() {
        return wind;
    }

    public String getDay() {
        return day;
    }

    public String getDate() {
        return date;
    }

    public String getTemp() {
        return temp;
    }

    public String getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }
}
