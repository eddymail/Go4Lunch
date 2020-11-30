package com.lousssouarn.edouard.go4lunch.model;

class Restaurant {
    private String name;
    private String placeId;
    private String picture;
    private String address;
    private String openingHours;
    //private int distance;
    private String location;
    private double rating;

    public Restaurant(String name, String placeId, String picture, String address, String openingHours, String location, double rating) {
        this.name = name;
        this.placeId = placeId;
        this.picture = picture;
        this.address = address;
        this.openingHours = openingHours;
        this.location = location;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
