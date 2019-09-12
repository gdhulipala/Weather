package com.example.ganga.weather;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Geocoder geocoder;
    List<Address> addresses;
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    String lattitude, longitude;
    EditText text;
    NetworkInfo networkInfo;
    SharedPreferences sharedprefs;
    ProgressBar search;
    ImageView look;
    int rad = 5000;
    String editText;
    ListView earthquakeListView;
    Uri buidurl;
    String KeyNumber;
    String area;
    String city;
    List<FiveDays> result3;
    String country;

    private static final String LOG_TAG = MainActivity.class.getName();

    String USGS_REQUEST_URL = "https://dataservice.accuweather.com/locations/v1/cities/search";
    String USGS_REQUEST_URL_FORECAST = "http://dataservice.accuweather.com";
    String USGS_REQUEST_URL_CURRENT = "http://dataservice.accuweather.com";
    String USGS_REQUEST_URL_HUMIDITY = "https://api.openweathermap.org";

    private final static String API_KEY = "xxxxxxx";
    private final static String API_KEY_HUMIDITY = "xxxxxxxxx";

    private static String PARAM_API_KEY = "apikey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button newButton = findViewById(R.id.button);
        newButton.setVisibility(View.GONE);


        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        /*if (networkInfo != null && networkInfo.isConnected()) {

            TextView nointernet = (TextView) findViewById(R.id.nonet);
            nointernet.setVisibility(View.GONE);

        } else {

            TextView nointernet = (TextView) findViewById(R.id.nonet);
            nointernet.setVisibility(View.VISIBLE);
        }*/

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }

        MainActivity.RequestAsyncTask task = new MainActivity.RequestAsyncTask();
        task.execute(buidurl.toString());
        Log.v(LOG_TAG, "url " + buidurl.toString());
        Log.e(LOG_TAG, "THIS METHOD IS CALLED 1");
    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);
                Log.e(LOG_TAG, lattitude);
                getDetails(latti, longi);

                Log.v(LOG_TAG, "Cordinates: " + latti + longi);

            } else {

                Toast.makeText(this, "Unable to Trace your location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void getDetails(Double lat, Double lon){

        geocoder = new Geocoder(this, Locale.getDefault());

        try{

            addresses = geocoder.getFromLocation(lat, lon, 1);

            city = addresses.get(0).getAdminArea();

            String address = addresses.get(0).getAddressLine(0);

            country = addresses.get(0).getCountryName();

            area = addresses.get(0).getLocality();

            Log.v(LOG_TAG, "Address: " + area +  city);



            /*SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            String location = sharedPrefs.getString(
                    getString(R.string.settings_city_key),
                    "White Plains NY US");*/

            buidurl = Uri.parse(USGS_REQUEST_URL).buildUpon()
                    .appendQueryParameter("q", area + " " + city + " "+ country)
                    .appendQueryParameter(PARAM_API_KEY, API_KEY)
                    .build();


        } catch(IOException e){

            e.printStackTrace();
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class RequestAsyncTask extends AsyncTask<String, Void, List<Places>> {


        protected List<Places> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            List<Places> result = UtilsPlaceRadius.fetchEarthquakeData(urls[0]);
            return result;
        }


        @Override
        protected void onPostExecute(List<Places> data) {
            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (data != null && !data.isEmpty()) {
                Log.v(LOG_TAG, "Entered Data is not null" );
                Places locationKey = data.get(0);
                KeyNumber = locationKey.getId();

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("dataservice.accuweather.com")
                        .appendPath("forecasts")
                        .appendPath("v1")
                        .appendPath("daily")
                        .appendPath("5day")
                        .appendPath(KeyNumber)
                        .appendQueryParameter(PARAM_API_KEY, API_KEY);
                USGS_REQUEST_URL_FORECAST = builder.build().toString();

                Uri.Builder builder2 = new Uri.Builder();
                builder2.scheme("https")
                        .authority("dataservice.accuweather.com")
                        .appendPath("currentconditions")
                        .appendPath("v1")
                        .appendPath(KeyNumber)
                        .appendQueryParameter(PARAM_API_KEY, API_KEY);
                USGS_REQUEST_URL_CURRENT = builder2.build().toString();

                Log.v(LOG_TAG, "url " + USGS_REQUEST_URL);

                Uri.Builder builder3 = new Uri.Builder();
                builder3.scheme("https")
                        .authority("api.openweathermap.org")
                        .appendPath("data")
                        .appendPath("2.5")
                        .appendPath("weather")
                        .appendQueryParameter("q", area + "," + city + ","+ country)
                        .appendQueryParameter(PARAM_API_KEY, API_KEY_HUMIDITY);
                USGS_REQUEST_URL_HUMIDITY = builder3.build().toString();

                Log.e(LOG_TAG, USGS_REQUEST_URL);
                MainActivity.CurrentDayAsyncTask task = new MainActivity.CurrentDayAsyncTask();
                task.execute(USGS_REQUEST_URL_FORECAST, USGS_REQUEST_URL_CURRENT, USGS_REQUEST_URL_HUMIDITY);

            } if(data==null){

                Log.v(LOG_TAG, "Entered Data is null");
            }
        }
    }

    private class CurrentDayAsyncTask extends AsyncTask<String, Void, List<FiveDays>> {


        protected List<FiveDays> doInBackground(String... urls) {

            List<FiveDays> result = new ArrayList<>();
            List<FiveDays> result2 = new ArrayList<>();
            List<FiveDays> result4 = new ArrayList<>();

            result = NetworkUtils.fetchEarthquakeData(urls[0]);
            Log.v(LOG_TAG, "Result   " + result.size());
            result2 = CurrentUtils.fetchEarthquakeData(urls[1]);
            Log.v(LOG_TAG, "Result 2  " + result2.size());
            result4 = Utils.fetchEarthquakeData(urls[2]);
            Log.v(LOG_TAG, "Result  " + result.size());
            result3 = new ArrayList<>();
            result3.addAll(result);
            result3.addAll(result2);
            result3.addAll(result4);

            return result3;
        }


        @Override
        protected void onPostExecute(List<FiveDays> data) {
            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (data != null && !data.isEmpty()) {
                Log.v(LOG_TAG, "Entered Data is not null" );

                ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar);
                progress.setVisibility(View.GONE);

                TextView loadingpage = (TextView) findViewById(R.id.textView);
                loadingpage.setVisibility(View.GONE);

                Button newButton = findViewById(R.id.button);
                newButton.setVisibility(View.VISIBLE);


                TextView cityName = (TextView) findViewById(R.id.textView2);
                cityName.setText(area+ ", " + city);

                TextView units = (TextView) findViewById(R.id.text_units);
                units.setText("\u2109");

                TextView weatherText = (TextView) findViewById(R.id.text_weathertext);
                FiveDays currentdays = data.get(5);
                String textweather = currentdays.getmweathertext();
                weatherText.setText(textweather);

                TextView currenttempText = (TextView) findViewById(R.id.text_currenttemp);
                FiveDays currentdays1 = data.get(5);
                String currenttemp = currentdays1.getmcurrenttemp();
                currenttempText.setText(currenttemp);

                TextView mintemptext = (TextView) findViewById(R.id.text_mintemp);
                FiveDays currentdays2 = data.get(0);
                String mintemp= currentdays2.getMin();
                mintemptext.setText(mintemp);

                TextView maxtemptext = (TextView) findViewById(R.id.text_maxtemp);
                FiveDays currentdays3 = data.get(0);
                String maxtemp= currentdays3.getMax();
                maxtemptext.setText(maxtemp);

                TextView currenthumid = (TextView) findViewById(R.id.text_percentrain);
                FiveDays currentdays4 = data.get(6);
                String rainchance= currentdays4.getmhumidity();
                currenthumid.setText(rainchance);

                TextView nightweathertext = (TextView) findViewById(R.id.text_nightweathertext);
                FiveDays currentdays5 = data.get(0);
                String nightphrase= currentdays5.getmnightphrase();
                nightweathertext.setText(nightphrase);

                TextView dayweathertext = (TextView) findViewById(R.id.text_dayweathertext);
                FiveDays currentdays6 = data.get(0);
                String dayphrase= currentdays6.getmdayphrase();
                dayweathertext.setText(dayphrase);


            } if(data==null){

                Log.v(LOG_TAG, "Entered Data is null");
            }
        }
    }

    public void fiveday(View v){

        Intent intent = new Intent(MainActivity.this, LocationkeyActivity.class);
        intent.putExtra("KeyValue", KeyNumber);
        startActivity(intent);


    }

    public void hourlycast(View v){

        FiveDays currentday = result3.get(0);

        String mlink = currentday.getmLink();

        Uri mlinkuri = Uri.parse(mlink);

        // Create a new intent to view the earthquake URI
        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, mlinkuri);

        // Send the intent to launch a new activity
        startActivity(websiteIntent);


    }



}

