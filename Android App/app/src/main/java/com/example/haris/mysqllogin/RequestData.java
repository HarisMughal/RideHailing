package com.example.haris.mysqllogin;

import java.io.Serializable;

/**
 * Created by Haris on 11/30/2018.
 */

public class RequestData implements Serializable {
    private int id;
    private double latitude;
    private double longitude;
    private double distance;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public double getDistance() {
        return distance;
    }

    public void setDistance(double lat2, double lon2) {
        double theta = longitude - lon2;
        double dist = Math.sin(deg2rad(latitude))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(latitude))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        distance =  (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
