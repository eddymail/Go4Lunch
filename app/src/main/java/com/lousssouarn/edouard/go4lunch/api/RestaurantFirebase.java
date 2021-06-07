package com.lousssouarn.edouard.go4lunch.api;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lousssouarn.edouard.go4lunch.model.Restaurant;
import com.lousssouarn.edouard.go4lunch.model.User;

import java.util.List;

public class RestaurantFirebase {

    public static  final String COLLECTION_NAME = "restaurants";

    // Collection reference
    public static CollectionReference getRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // Create
    public static Task<Void> createRestaurant (String restaurantId, String name, String address,List<User> usersLikes,List<User> usersGoes){
        Restaurant restaurantToCreate = new Restaurant(restaurantId, name, address, usersLikes,usersGoes);
        return RestaurantFirebase.getRestaurantsCollection().document(restaurantId).set(restaurantToCreate);
    }

    // Get
    public static Task<DocumentSnapshot> getRestaurant(String restaurantId){
        return RestaurantFirebase.getRestaurantsCollection().document(restaurantId).get();
    }

    // Update
    public static Task<Void> updateUsersLikesList(String restaurantId, List<User> usersLikes){
        return RestaurantFirebase.getRestaurantsCollection().document(restaurantId).update("usersLikes", usersLikes );
    }

    public static Task<Void> updateUsersGoesList(String restaurantId, List<User> usersGoes){
        return RestaurantFirebase.getRestaurantsCollection().document(restaurantId).update("usersGoes", usersGoes);
    }

    // Delete
    public static Task<Void> deleteRestaurant(String restaurantId){
        return RestaurantFirebase.getRestaurantsCollection().document(restaurantId).delete();
    }
}
