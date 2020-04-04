package com.simonliebers.spritrechner.General;

public class Position {
    public double longitude;
    public double latitude;

    // create and initialize a point with given name and
    // (latitude, longitude) specified in degrees
    public Position(double latitude, double longitude) {
        this.latitude  = latitude;
        this.longitude = longitude;
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
}
