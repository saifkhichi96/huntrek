package com.aspirasoft.huntrek.model.collectibles;

import java.io.Serializable;

/**
 * Created by saifkhichi96 on 23/12/2017.
 */

public class TreasureChest implements Serializable {

    public static final float RANGE = 2500.f;
    private final int id;
    private double longitude;
    private double latitude;
    private int value;

    public TreasureChest(int id) {
        this.id = id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }
}