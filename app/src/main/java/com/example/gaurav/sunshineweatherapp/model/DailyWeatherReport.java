package com.example.gaurav.sunshineweatherapp.model;

import android.provider.ContactsContract;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Gaurav on 2017-05-03.
 */

public class DailyWeatherReport {
    private String city_name;
    private String country;
    private int current_temp;
    private int max_temp;
    private int min_temp;
    private String weather_type;
    private String raw_date;
    private String raw_date2;
    private int humidity;
    private String weather_desc;
    private int wind_speed;

    public static final String WEATHER_TYPE_CLOUDS = "Clouds";
    public static final String WEATHER_TYPE_CLEAR = "Clear";
    public static final String WEATHER_TYPE_RAIN = "Rain";
    public static final String WEATHER_TYPE_SNOW = "Snow";

    public DailyWeatherReport(String city_name, String country, int current_temp, int max_temp, int min_temp, String weather_type, String raw_date, String raw_date2, int humidity, String weather_desc, int wind_speed, Double wind_degree) {
        this.city_name = city_name;
        this.country = country;
        this.current_temp = current_temp;
        this.max_temp = max_temp;
        this.min_temp = min_temp;
        this.weather_type = weather_type;
        this.raw_date = rawDate_to_formattedDate(raw_date);
        this.raw_date2 = rawDate_to_formattedDay(raw_date2);
        this.humidity = humidity;
        this.weather_desc = weather_desc;
        this.wind_speed = wind_speed;
    }

    public int getHumidity() {
        return humidity;
    }

    public String getWeather_desc() {
        return weather_desc;
    }

    public int getWind_speed() {
        return wind_speed;
    }

    public String rawDate_to_formattedDate (String rawDate){
        //convert raw date to formatted date...
        //Original format 2017-05-05 00:00:00
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat resultFormat = new SimpleDateFormat("EEE dd MMM");
        String dateString = "";

        try {
            Date date = originalFormat.parse(rawDate);
            dateString = resultFormat.format(date);


        } catch (ParseException e) {
            Log.v("Date", e.toString());
        }
        //returned format: Today Thur 04 Mayra
        return "Today " + dateString;
    }


    public String rawDate_to_formattedDay (String rawDate){
        //convert raw date to formatted date...
        //Original format 2017-05-05 00:00:00
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat resultFormat = new SimpleDateFormat("EEEE");
        String dateString = "";
        try {
            Date date = originalFormat.parse(rawDate);
            dateString = resultFormat.format(date);

        } catch (ParseException e) {
            Log.v("Date", e.toString());
        }
        //returned format: Today Thur 04 May
        return dateString;

    }

    public String getCity_name() {
        return city_name;
    }

    public String getCountry() {
        return country;
    }

    public int getCurrent_temp() {
        return current_temp;
    }

    public int getMax_temp() {
        return max_temp;
    }

    public int getMin_temp() {
        return min_temp;
    }

    public String getWeather_type() {
        return weather_type;
    }

    public String getRaw_date() {
        return raw_date;
    }

    public String getRaw_date2() {
        return raw_date2;
    }
}
