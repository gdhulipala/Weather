package com.example.ganga.weather;

/**
 * Created by ganga on 5/20/18.
 */

public class FiveDays {

    /** Magnitude of the earthquake */
    private String mDate;

    /** Location of the earthquake */
    private String mMax;

    private String mMin;

    private String mLink;

    private String mweathertext;

    private String mcurrenttemp;

    private String mhumidity;

    private String mdayphrase;

    private String mnightphrase;


    public FiveDays( ) {

    }


    public FiveDays( String date, String max, String min, String link, String dayphrase, String nightphrase) {

        mDate = date;
        mMax = max;
        mMin = min;
        mLink = link;
        mdayphrase = dayphrase;
        mnightphrase = nightphrase;


    }

    public FiveDays( String weathertext, String currenttemp) {

        mweathertext = weathertext;
        mcurrenttemp = currenttemp;
    }

    public FiveDays( String humidity) {

        mhumidity = humidity;

    }
    /**
     * Returns the location of the earthquake.
     */
    public String getmDate() {
        return mDate;
    }

    public String getMax() {
        return mMax;
    }

    public String getMin() {
        return mMin;
    }

    public String getmLink() {
        return mLink;
    }

    public String getmweathertext() {
        return mweathertext;
    }

    public String getmcurrenttemp() {
        return mcurrenttemp;
    }

    public String getmhumidity() {
        return mhumidity;
    }

    public String getmdayphrase() {
        return mdayphrase;
    }

    public String getmnightphrase() {
        return mnightphrase;
    }


}
