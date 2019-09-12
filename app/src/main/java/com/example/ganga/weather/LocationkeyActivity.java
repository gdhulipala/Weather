package com.example.ganga.weather;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class LocationkeyActivity extends AppCompatActivity {


    private static final String LOG_TAG = MainActivity.class.getName();

    String USGS_REQUEST_URL = "http://dataservice.accuweather.com";
    String USGS_REQUEST_URL_CURRENT = "http://dataservice.accuweather.com";

    private final static String API_KEY = "tiuZzb4Q9PUDAFrVUsRB430irAgHvyI0";

    private static String PARAM_API_KEY = "apikey";

    ListView listView;

    ArrayAdapter<FiveDays> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locationkey);

        // Find a reference to the {@link ListView} in the layout
      listView = (ListView) findViewById(R.id.listView);


        // Create a new adapter that takes an empty list of earthquakes as input
        adapter = new FivedayAdapter(this, new ArrayList<FiveDays>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        listView.setAdapter(adapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected earthquake.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                FiveDays currentday = adapter.getItem(position);
                String mobilelink = currentday.getmLink();

            }
        });

        Intent intent = getIntent();
        String locationKey = intent.getStringExtra("KeyValue");

        Log.v(LOG_TAG, "Entered Location Key  " + locationKey);


        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("dataservice.accuweather.com")
                .appendPath("forecasts")
                .appendPath("v1")
                .appendPath("daily")
                .appendPath("5day")
                .appendPath(locationKey)
                .appendQueryParameter(PARAM_API_KEY, API_KEY);
        USGS_REQUEST_URL = builder.build().toString();

        Uri.Builder builder2 = new Uri.Builder();
        builder2.scheme("https")
                .authority("dataservice.accuweather.com")
                .appendPath("currentconditions")
                .appendPath("v1")
                .appendPath(locationKey)
                .appendQueryParameter(PARAM_API_KEY, API_KEY);
        USGS_REQUEST_URL_CURRENT = builder2.build().toString();

        Log.v(LOG_TAG, "url " + USGS_REQUEST_URL);

        Log.e(LOG_TAG, USGS_REQUEST_URL);
        LocationkeyActivity.FivedayAsyncTask task = new LocationkeyActivity.FivedayAsyncTask();
        task.execute(USGS_REQUEST_URL, USGS_REQUEST_URL_CURRENT);
        //Log.e(LOG_TAG, "THIS METHOD IS CALLED 1");

    }


    private class FivedayAsyncTask extends AsyncTask<String, Void, List<FiveDays>> {


        protected List<FiveDays> doInBackground(String... urls) {


            List<FiveDays> result = new ArrayList<>();
            List<FiveDays> result2 = new ArrayList<>();

            result = NetworkUtils.fetchEarthquakeData(urls[0]);
            Log.v(LOG_TAG, "Result   " + result.size());
            result2 = CurrentUtils.fetchEarthquakeData(urls[1]);
            Log.v(LOG_TAG, "Result 2  " + result2.size());
            Log.v(LOG_TAG, "Result  " + result.size());
            List<FiveDays> result3 = new ArrayList<>();
            result3.addAll(result);
            result3.addAll(result2);

            return result3;

        }


        @Override
        protected void onPostExecute(List<FiveDays> data) {


            adapter.clear();


            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
           if (data != null && !data.isEmpty()) {


                Log.v(LOG_TAG, "Entered Data is not null" );
                adapter.addAll(data);



            } if(data==null){

                Log.v(LOG_TAG, "Entered Data is null");
            }



        }


        }

    }


