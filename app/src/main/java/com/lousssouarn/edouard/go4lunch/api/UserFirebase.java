package com.lousssouarn.edouard.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lousssouarn.edouard.go4lunch.model.Restaurant;
import com.lousssouarn.edouard.go4lunch.model.User;

import java.util.List;


public class UserFirebase {

    public static final String COLLECTION_NAME = "users";

    // Collection reference
    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // Create
    public static Task<Void> createUser(String uId, String name, String email, String urlPicture, String goRestaurant, List<Restaurant> favorites ){
        User userToCreate = new User(name, email, urlPicture, goRestaurant, favorites);
        return UserFirebase.getUsersCollection().document(uId).set(userToCreate);
    }

    // Get
    public static Task<DocumentSnapshot> getUser(String uId){
        return UserFirebase.getUsersCollection().document(uId).get();
    }

    // Update
    public static Task<Void> updateGoRestaurant(String uId, String goRestaurant){
        return UserFirebase.getUsersCollection().document(uId).update("goRestaurant", goRestaurant);
    }

    public static Task<Void> updateFavoritesList(String uId, List<Restaurant> favorites){
        return UserFirebase.getUsersCollection().document(uId).update("favorites", favorites);
    }

    // Delete
    public static Task<Void> deleteUser(String uId){
        return UserFirebase.getUsersCollection().document(uId).delete();
    }
}
