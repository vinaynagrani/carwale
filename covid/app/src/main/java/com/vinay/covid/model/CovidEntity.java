package com.vinay.covid.model;

import com.google.gson.annotations.SerializedName;

public class CovidEntity {
    @SerializedName("country")
    private String country;
    @SerializedName("confirmed")
    private int confirmed;
    @SerializedName("recovered")
    private int recovered;
    @SerializedName("critical")
    private int critical;
    @SerializedName("deaths")
    private int deaths;
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(int confirmed) {
        this.confirmed = confirmed;
    }

    public int getRecovered() {
        return recovered;
    }

    public void setRecovered(int recovered) {
        this.recovered = recovered;
    }

    public int getCritical() {
        return critical;
    }

    public void setCritical(int critical) {
        this.critical = critical;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
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

    @Override
    public String toString() {
        return "CovidEntity{" +
                "country='" + country + '\'' +
                ", confirmed=" + confirmed +
                ", recovered=" + recovered +
                ", critical=" + critical +
                ", deaths=" + deaths +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    @Override
    public boolean equals(Object anotherObject) {
        if (!(anotherObject instanceof CovidEntity)) {
            return false;
        }
        CovidEntity covidEntity = (CovidEntity) anotherObject;
        return (this.country.equalsIgnoreCase(covidEntity.country));
    }

}
