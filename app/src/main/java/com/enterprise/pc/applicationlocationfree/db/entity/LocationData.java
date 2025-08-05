package com.enterprise.pc.applicationlocationfree.db.entity;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by PC on 2018-03-30.
 */

@Entity(tableName = "locationdata")
public class LocationData implements Parcelable{

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

    @ColumnInfo(name = "bearing")
    private float bearing;

    @ColumnInfo(name = "verticalAccuracyMeters")
    private float verticalAccuracyMeters;

    @ColumnInfo(name = "speedAccuracyMetersPerSecond")
    private float speedAccuracyMetersPerSecond;

    @ColumnInfo(name = "bearingAccuracyDegrees")
    private float bearingAccuracyDegrees;

    @ColumnInfo(name = "information")
    private String information;

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

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public float getVerticalAccuracyMeters() {
        return verticalAccuracyMeters;
    }

    public void setVerticalAccuracyMeters(float verticalAccuracyMeters) {
        this.verticalAccuracyMeters = verticalAccuracyMeters;
    }

    public float getSpeedAccuracyMetersPerSecond() {
        return speedAccuracyMetersPerSecond;
    }

    public void setSpeedAccuracyMetersPerSecond(float speedAccuracyMetersPerSecond) {
        this.speedAccuracyMetersPerSecond = speedAccuracyMetersPerSecond;
    }

    public float getBearingAccuracyDegrees() {
        return bearingAccuracyDegrees;
    }

    public void setBearingAccuracyDegrees(float bearingAccuracyDegrees) {
        this.bearingAccuracyDegrees = bearingAccuracyDegrees;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public LocationData(){
    }

    public LocationData(long id, String formattedTime, double latitude, double longitude,
                        double altitude, float accuracy, String provider, float speed,
                        float bearing, float verticalAccuracyMeters, float speedAccuracyMetersPerSecond, float bearingAccuracyDegrees,
                        String information){

        this.id = id;

        this.formattedTime = formattedTime;

        this.latitude = latitude;

        this.longitude = longitude;

        this.altitude = altitude;

        this.accuracy = accuracy;

        this.provider = provider;

        this.speed = speed;

        this.bearing = bearing;

        this.verticalAccuracyMeters = verticalAccuracyMeters;

        this.speedAccuracyMetersPerSecond = speedAccuracyMetersPerSecond;

        this.bearingAccuracyDegrees = bearingAccuracyDegrees;

        this.information = information;

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

        this.bearing = locationData.getBearing();

        this.verticalAccuracyMeters = locationData.getVerticalAccuracyMeters();

        this.speedAccuracyMetersPerSecond = locationData.getSpeedAccuracyMetersPerSecond();

        this.bearingAccuracyDegrees = locationData.getBearingAccuracyDegrees();

        this.information = locationData.getInformation();

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {

        out.writeLong(id);
        out.writeString(formattedTime);
        out.writeDouble(latitude);
        out.writeDouble(longitude);
        out.writeDouble(altitude);
        out.writeFloat(accuracy);
        out.writeString(provider);
        out.writeFloat(speed);
        out.writeFloat(bearing);
        out.writeFloat(verticalAccuracyMeters);
        out.writeFloat(speedAccuracyMetersPerSecond);
        out.writeFloat(bearingAccuracyDegrees);
        out.writeString(information);

    }

    private LocationData(Parcel in) {

        id = in.readLong();
        formattedTime = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        altitude = in.readDouble();
        accuracy = in.readFloat();
        provider = in.readString();
        speed = in.readFloat();
        bearing = in.readFloat();
        verticalAccuracyMeters = in.readFloat();
        speedAccuracyMetersPerSecond = in.readFloat();
        bearingAccuracyDegrees = in.readFloat();
        information = in.readString();

    }

    public static final Parcelable.Creator<LocationData> CREATOR
            = new Parcelable.Creator<LocationData>() {

        @Override
        public LocationData createFromParcel(Parcel in) {
            return new LocationData(in);
        }

        @Override
        public LocationData[] newArray(int size) {
            return new LocationData[size];
        }
    };


}
