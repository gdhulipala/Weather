package com.example.ganga.weather;

/**
 * Created by ganga on 5/23/18.
 */

public class CurrentDay {



    private String mweathertext;

    private String mcurrenttemp;



    public CurrentDay( String weathertext, String currenttemp) {

        mweathertext = weathertext;
        mcurrenttemp = currenttemp;
    }

    public String getmweathertext() {
        return mweathertext;
    }

    public String getmcurrenttemp() {
        return mcurrenttemp;
    }
}
