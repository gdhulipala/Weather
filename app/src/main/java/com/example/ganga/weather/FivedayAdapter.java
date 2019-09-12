package com.example.ganga.weather;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import static com.example.ganga.weather.UtilsPlaceRadius.LOG_TAG;

/**
 * Created by ganga on 5/20/18.
 */

public class FivedayAdapter extends ArrayAdapter<FiveDays> {

    List<FiveDays> days;



    public FivedayAdapter(Context context, List<FiveDays> days) {
        super(context, 0,days);
        this.days = days;
    }

    @Override
    public int getCount(){

        return days.size()-1;

    }



    /**
     * Returns a list item view that displays information about the earthquake at the given position
     * in the list of earthquakes.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.listitem_new, parent, false);
        }

        // Find the earthquake at the given position in the list of earthquakes
        FiveDays currentday = getItem(position);
        Log.e(LOG_TAG, "Get Item Position " + position);


            //Find the TextView with view ID magnitude

        TextView dateText = (TextView) listItemView.findViewById(R.id.text_date);

        if (position==0) {

            Log.e(LOG_TAG, "Position 0  " + position);

            dateText.setText("Today");

        } if(position==1){

            Log.e(LOG_TAG, "Position 1 " + position);

            dateText.setText("Tomorrow");

        } if(position>1) {
            Log.e(LOG_TAG, "Else Position " + position);
            // Format the magnitude to show 1 decimal place
            String date = currentday.getmDate();
            // Display the magnitude of the current earthquake in that TextView
            dateText.setText(date);
        }

            TextView minimumText = (TextView) listItemView.findViewById(R.id.text_min);
            String tempmin = currentday.getMin();
            minimumText.setText(tempmin);

            // Find the TextView with view ID magnitude
            TextView maxText = (TextView) listItemView.findViewById(R.id.text_max);
            // Format the magnitude to show 1 decimal place

            String maximumText = currentday.getMax();
            maxText.setText(maximumText);

            // Find the TextView with view ID magnitude
            TextView unitsText = (TextView) listItemView.findViewById(R.id.text_units);

            unitsText.setText("\u2109");

            if (position == 0) {

                TextView weatherText = (TextView) listItemView.findViewById(R.id.text_weatherText);
                FiveDays currentdays = getItem(5);
                String textweather = currentdays.getmweathertext();
                weatherText.setText(textweather);
            }



        return listItemView;
    }


    /**
     * Return the formatted magnitude string showing 1 decimal place (i.e. "3.2")
     * from a decimal magnitude value.
     */
    private String formatMagnitude(double magnitude) {
        DecimalFormat magnitudeFormat = new DecimalFormat("0.0");
        return magnitudeFormat.format(magnitude);
    }


}

