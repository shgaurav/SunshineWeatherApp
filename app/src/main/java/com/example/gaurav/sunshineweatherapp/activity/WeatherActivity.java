package com.example.gaurav.sunshineweatherapp.activity;

import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gaurav.sunshineweatherapp.R;
import com.example.gaurav.sunshineweatherapp.model.DailyWeatherReport;
import com.google.android.gms.cast.TextTrackStyle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.vision.text.Text;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WeatherActivity extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {

    final String URL_BASE = "http://api.openweathermap.org/data/2.5/forecast";
    final String URL_CORD = "/?lat="; //"/?lat=9.867886&lon=76.326914";
    final String URL_UNITS = "&units=metric";
    final String URL_API_KEY = "&APPID=e913be4a61396aa50f205c384c132fd1";
    final int PERMISSION_LOCATION = 111;
    private ArrayList<DailyWeatherReport> weather_repory_list = new ArrayList<>();

    private ImageView weather_icon_mini;
    private TextView weather_date;
    private TextView current_temp;
    private TextView min_temp;
    private TextView city_country;
    private ImageView weather_icon;
    private TextView weather_condition;
    //9.867886, 76.326914

    private GoogleApiClient mGoogleApiClient;
    WeatherAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        weather_icon = (ImageView)findViewById(R.id.current_condition_img);
        weather_icon_mini = (ImageView)findViewById(R.id.title_image);
        weather_date = (TextView) findViewById(R.id.date);
        current_temp = (TextView) findViewById(R.id.current_temp);
        min_temp = (TextView) findViewById(R.id.min_temp);
        city_country = (TextView) findViewById(R.id.location_detail);
        weather_condition = (TextView) findViewById(R.id.current_condition_txt);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.content_weather_reports);
        adapter = new WeatherAdapter(weather_repory_list);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
    }

    //Making a get request to the API for weather data...
    public void download_weather_data(Location location) {

        final String FULL_CORDS = URL_CORD + location.getLatitude() + "&lon=" +location.getLongitude();
        Log.v("FUN2", "Cords: " + FULL_CORDS);
        //setting up the URL to be passed with the get request...
        final String url = URL_BASE + FULL_CORDS + URL_UNITS + URL_API_KEY;
        //Create a new JsonObjectRequest...
        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            //Function for checking proper response and working with the response appropiately...
            @Override
            public void onResponse(JSONObject response) {
                //Parse response to usefull data...
                try {
                    //Grabbing the object from the json returned...
                    JSONObject city = response.getJSONObject("city");
                    String city_name = city.getString("name"); //Grabbing the city name from the city object...
                    String country = city.getString("country");
                    Log.v("JSON", "City: " + city_name + "Country: " + country);

                    JSONArray list = response.getJSONArray("list");
                    for (int x = 0; x<40; x+=8){
                        JSONObject obj = list.getJSONObject(x); //Top object
                        JSONObject main = obj.getJSONObject("main");
                        Double currentTemp = main.getDouble("temp");
                        Double maxTemp = main.getDouble("temp_max");
                        Double minTemp = main.getDouble("temp_min");
                        int humidity = main.getInt("humidity");

                        JSONArray weatherArr = obj.getJSONArray("weather");
                        JSONObject weather = weatherArr.getJSONObject(0);
                        String weather_type = weather.getString("main");
                        String weather_desc = weather.getString("description");

                        JSONObject wind = obj.getJSONObject("wind");
                        Double wind_speed = wind.getDouble("speed");
                        Double wind_degree = wind.getDouble("deg");



                        String raw_date = obj.getString("dt_txt");

                        DailyWeatherReport report = new DailyWeatherReport(city_name, country, currentTemp.intValue(), maxTemp.intValue(), minTemp.intValue(),
                                weather_type, raw_date, raw_date , humidity, weather_desc, wind_speed.intValue(), wind_degree);

                        //adding the report(daily weather report) to the array list which will contain weather for 5 days as the loop iterates...
                        weather_repory_list.add(report);

                        Log.v("JSON", "Printing from class: " + report.getWeather_type());
                    }
                }catch (JSONException e){
                    Log.v("JSON", "EXC: " + e.getLocalizedMessage());
                }
                //Updating UI after downloading data from the API...
                updateUI();
                adapter.notifyDataSetChanged();
            }

            //Function that listens for error from the server if the get request fails...
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("FUN", "Err: " + error.getLocalizedMessage());
            }
        });

        Volley.newRequestQueue(this).add(jsonRequest);
    }

    public void updateUI(){
        //Update UI after weather is downloaded...

        if (weather_repory_list.size() > 0){
            DailyWeatherReport report = weather_repory_list.get(0);

            //swicth to check against different weather conditions and changing the images accordingly...
            switch(report.getWeather_type()) {
                case DailyWeatherReport.WEATHER_TYPE_CLOUDS:
                    weather_icon.setImageDrawable(getResources().getDrawable(R.drawable.cloudy));
                    weather_icon_mini.setImageDrawable(getResources().getDrawable(R.drawable.cloudy));
                    break;
                case DailyWeatherReport.WEATHER_TYPE_RAIN:
                    weather_icon.setImageDrawable(getResources().getDrawable(R.drawable.rainy));
                    weather_icon_mini.setImageDrawable(getResources().getDrawable(R.drawable.rainy));
                    break;
                case DailyWeatherReport.WEATHER_TYPE_SNOW:
                    weather_icon.setImageDrawable(getResources().getDrawable(R.drawable.snow));
                    weather_icon_mini.setImageDrawable(getResources().getDrawable(R.drawable.snow));
                    break;
                default:
                    weather_icon.setImageDrawable(getResources().getDrawable(R.drawable.sunny));
                    weather_icon_mini.setImageDrawable(getResources().getDrawable(R.drawable.sunny));
            }

            weather_date.setText(report.getRaw_date());
            current_temp.setText(Integer.toString(report.getCurrent_temp())+"°");
            min_temp.setText(Integer.toString(report.getMin_temp())+"°");
            city_country.setText(report.getCity_name() + ", " + report.getCountry());
            weather_condition.setText(report.getWeather_type());

        }

    }




    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION);
            Log.v("DONKEY", "Requesting Permission");
        } else {
            Log.v("DONKEY", "starting location services");
            startLocationServices();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        download_weather_data(location);
    }

    public void startLocationServices() {
        Log.v("DONKEY", "Starting Location Services Called");

        try {
            LocationRequest req = LocationRequest.create().setPriority(LocationRequest.PRIORITY_LOW_POWER);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, req, this);
            Log.v("DONKEY", "Asking for location updates");
        } catch (SecurityException exception) {
            //show dialog to user saying we cannot get location unless they give app permission.
            Toast.makeText(this, "You denied permission dummy", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationServices();
                    Toast.makeText(this, "Permission Given", Toast.LENGTH_LONG);
                } else {
                    //show a  dialog showing i cannot get you location without you granting permission.
                    Toast.makeText(this, "You denied permission dummy", Toast.LENGTH_LONG);
                }
            }
        }
    }

    public class WeatherAdapter extends RecyclerView.Adapter<WeatherReportViewHolder>{
        private ArrayList<DailyWeatherReport> weatherReports;

        public WeatherAdapter(ArrayList<DailyWeatherReport> weatherReports) {
            this.weatherReports = weatherReports;
        }

        @Override

        public WeatherReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_weather, parent, false);
            return new WeatherReportViewHolder(card);
        }

        @Override
        public void onBindViewHolder(WeatherReportViewHolder holder, int position) {
            DailyWeatherReport report = weatherReports.get(position);
            holder.updateUI(report);
        }

        @Override
        public int getItemCount() {
            return weatherReports.size();
        }
    }

    public class WeatherReportViewHolder extends RecyclerView.ViewHolder{

        private ImageView weatherIconL;
        private TextView weatherDateL;
        private TextView weatherConditionL;
        private TextView maxTempL;
        private TextView minTEmpL;
        private TextView humidityL;
        private TextView windSpeedL;
        private TextView weatherDescL;

        public WeatherReportViewHolder(View itemView) {
            super(itemView);

            weatherIconL = (ImageView)itemView.findViewById(R.id.lweather_icon);
            weatherDateL = (TextView)itemView.findViewById(R.id.lweather_day);
            weatherConditionL = (TextView)itemView.findViewById(R.id.lweather_condition);
            maxTempL = (TextView)itemView.findViewById(R.id.lmax_temp);
            minTEmpL = (TextView) itemView.findViewById(R.id.lmin_temp);
            humidityL = (TextView) itemView.findViewById(R.id.lhumidity);
            windSpeedL = (TextView) itemView.findViewById(R.id.lwind_speed);
            weatherDescL = (TextView) itemView.findViewById(R.id.lweather_desc);
        }

        public void updateUI(DailyWeatherReport report){

            weatherDateL.setText(report.getRaw_date2());
            weatherConditionL.setText(report.getWeather_type());
            weatherDescL.setText(report.getWeather_desc());
            maxTempL.setText("High: " + Integer.toString(report.getMax_temp())+"°");
            minTEmpL.setText("Low: "+ Integer.toString(report.getMin_temp())+"°");
            humidityL.setText("Humidity: " + Integer.toString(report.getHumidity())+ "%");
            windSpeedL.setText("Wind Speed: "+ Integer.toString(report.getWind_speed())+" m/hr");


            //swicth to check against different weather conditions and changing the images accordingly...
            switch(report.getWeather_type()) {
                case DailyWeatherReport.WEATHER_TYPE_CLOUDS:
                    weatherIconL.setImageDrawable(getResources().getDrawable(R.drawable.cloudy_mini));
                    break;
                case DailyWeatherReport.WEATHER_TYPE_RAIN:
                    weatherIconL.setImageDrawable(getResources().getDrawable(R.drawable.rainy_mini));
                    break;
                case DailyWeatherReport.WEATHER_TYPE_SNOW:
                    weatherIconL.setImageDrawable(getResources().getDrawable(R.drawable.snow_mini));
                    break;
                default:
                    weatherIconL.setImageDrawable(getResources().getDrawable(R.drawable.sunny_mini));
            }

        }
    }
}

