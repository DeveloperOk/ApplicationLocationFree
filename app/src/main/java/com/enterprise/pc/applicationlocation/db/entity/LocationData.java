package com.enterprise.pc.applicationlocation.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by PC on 2018-03-30.
 */

@Entity(tableName = "locationdata")
public class LocationData {

    @PrimaryKey
    private long id;

    @ColumnInfo(name = "formattedTime")
    private String formattedTime;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    @ColumnInfo(name = "altitude")
    private double altitude;

    @ColumnInfo(name = "accuracy")
    private float accuracy;

    @ColumnInfo(name = "provider")
    private String provider;

    @ColumnInfo(name = "speed")
    private float speed;


    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFormattedTime() {
        return this.formattedTime;
    }

    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return this.altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public float getAccuracy() {
        return this.accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public String getProvider() {
        return this.provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public float getSpeed() {
        return this.speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }


    public LocationData(){
    }

    public LocationData(long id, String formattedTime, double latitude, double longitude, double altitude, float accuracy, String provider, float speed){

        this.id = id;

        this.formattedTime = formattedTime;

        this.latitude = latitude;

        this.longitude = longitude;

        this.altitude = altitude;

        this.accuracy = accuracy;

        this.provider = provider;

        this.speed = speed;

    }

    public LocationData(LocationData locationData){

        this.id = locationData.getId();

        this.formattedTime = locationData.getFormattedTime();

        this.latitude = locationData.getLatitude();

        this.longitude = locationData.getLongitude();

        this.altitude = locationData.getAltitude();

        this.accuracy = locationData.getAccuracy();

        this.provider = locationData.getProvider();

        this.speed = locationData.getSpeed();
    }

}
