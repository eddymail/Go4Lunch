package com.lousssouarn.edouard.go4lunch.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private String email;
    private String urlPicture;
    private String goRestaurant;
    private List<Restaurant> favorites;

    public User(){}


    public User(String name, String email, String urlPicture, String goRestaurant, List<Restaurant> favorites) {
        this.name = name;
        this.email = email;
        this.urlPicture = urlPicture;
        this.goRestaurant = goRestaurant;
        this.favorites = favorites;
    }

    // Getter
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    //@ServerTimestamp
    public String getGoRestaurant() {
        return goRestaurant;
    }

    public List<Restaurant> getFavorites() {
        return favorites;
    }


    // Setter
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public void setGoRestaurant(String goRestaurant) {
        this.goRestaurant = goRestaurant;
    }

    public void setFavorites(List<Restaurant> favorites) {
        this.favorites = favorites;
    }


}
