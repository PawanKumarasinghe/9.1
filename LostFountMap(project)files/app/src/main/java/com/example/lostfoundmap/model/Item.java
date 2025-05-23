package com.example.lostfoundmap.model;

public class Item {
    private String title;
    private double latitude;
    private double longitude;

    public Item(String title, double latitude, double longitude) {
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}

