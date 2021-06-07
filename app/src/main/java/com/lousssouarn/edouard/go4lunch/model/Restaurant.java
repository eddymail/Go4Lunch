package com.lousssouarn.edouard.go4lunch.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Restaurant {
    private String restaurantId;
    private String name;
    private String address;
    private List<User> usersLikes;
    private List<User> usersGoes;

    public Restaurant() { }

    public Restaurant(String restaurantId, String name, String address, List<User> usersLikes, List<User> usersGoes) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.address = address;
        this.usersLikes = usersLikes;
        this.usersGoes = usersGoes;
    }


    // Getter


    public String getRestaurantId() {
        return restaurantId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public List<User> getUsersLikes() {
        return usersLikes;
    }

    public List<User> getUsersGoes() {
        return usersGoes;
    }

    // Setter

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setUsersLikes(List<User> usersLikes) {
        this.usersLikes = usersLikes;
    }

    public void setUsersGoes(List<User> usersGoes) {
        this.usersGoes = usersGoes;
    }
}
