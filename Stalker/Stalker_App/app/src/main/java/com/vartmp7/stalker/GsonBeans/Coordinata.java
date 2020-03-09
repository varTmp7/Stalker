package com.vartmp7.stalker.GsonBeans;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Coordinata {
    private double latitude;//y
    private double longitude; //x

    public Coordinata() {
    }

    public Coordinata(double latitudine, double longitude) {
        this.latitude = latitudine;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(float longoitude) {
        this.longitude = longoitude;
    }

    @NonNull
    @Override
    public String toString() {
        return "\nLongitude(x): " + getLongitude() +
                "\nLatitude(y): " + getLatitude();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Coordinata) {
            Coordinata c = (Coordinata) obj;
            return c.longitude == this.longitude && c.latitude == this.latitude;
        }
        return false;
    }
    private double rad(double x){
        return x*Math.PI/180;
    }

    double getDistanceTo(final Coordinata c){

        long R = 6378137; // Earth’s mean radius in meter
        double dLat = rad(c.getLatitude() - getLatitude());
        double dLong = rad(c.getLongitude() - getLongitude());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(rad(getLatitude())) * Math.cos(rad(c.getLatitude())) *
                        Math.sin(dLong / 2) * Math.sin(dLong / 2);
        double c1 = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c1;
        return d; // returns the distance in meter
    };
}
