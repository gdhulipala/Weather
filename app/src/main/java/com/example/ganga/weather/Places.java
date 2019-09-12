package com.example.ganga.weather;

/**
 * Created by ganga on 5/20/18.
 */

public class Places {

    /** Magnitude of the earthquake */
    private String mName;

    /** Location of the earthquake */
    private String mPlace;

    private String mAddress;

    private int mrating;


    public Places( String id, int rating) {

        mPlace = id;
        mrating = rating;


    }
    /**
     * Returns the location of the earthquake.
     */
    public String getId() {
        return mPlace;
    }

    public int getRating() {
        return mrating;
    }



}
