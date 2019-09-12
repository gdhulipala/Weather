package com.example.ganga.weather;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganga on 5/20/18.
 */

public class NetworkUtils {




    /** Tag for the log messages */
   public static final String LOG_TAG = UtilsPlaceRadius.class.getSimpleName();


    public static List<FiveDays> fetchEarthquakeData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<FiveDays> names = extractFeatureFromJson(jsonResponse);

        //Return the {@link Event}
        return names;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);

        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    private static List<FiveDays> extractFeatureFromJson(String earthquakeJSON) {

        List<FiveDays> names = new ArrayList<>();

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }

        try {
            JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);

            JSONArray dailyforecast = baseJsonResponse.getJSONArray("DailyForecasts");

            if(dailyforecast.length()==0){

                Log.v(LOG_TAG, "No Location Key " );

                return null;
            }
            // For each earthquake in the earthquakeArray, create an {@link Earthquake} object
            for (int i = 0; i < dailyforecast.length(); i++) {

                // Get a single earthquake at position i within the list of earthquakes
                JSONObject object = dailyforecast.getJSONObject(i);
                String date = object.getString("Date");
                String mobileLink = object.getString("MobileLink");
                JSONObject temperature = object.getJSONObject("Temperature");
                JSONObject minimumtemp = temperature.getJSONObject("Minimum");
                int minTemp = minimumtemp.getInt("Value");
                String minTempstring = String.valueOf(minTemp);
                JSONObject maximumtemp = temperature.getJSONObject("Maximum");
                int maxtemp = maximumtemp.getInt("Value");
                String maxTempstring = String.valueOf(maxtemp);
                JSONObject day = object.getJSONObject("Day");
                String dayphrase = day.getString("IconPhrase");
                JSONObject night = object.getJSONObject("Night");
                String nightphrase = night.getString("IconPhrase");


                FiveDays days = new FiveDays(date, maxTempstring, minTempstring, mobileLink, dayphrase, nightphrase);

                names.add(days);
            }


        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }
        return names;
    }
}
