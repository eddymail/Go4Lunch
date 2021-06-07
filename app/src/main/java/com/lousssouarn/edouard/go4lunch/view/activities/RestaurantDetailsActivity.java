package com.lousssouarn.edouard.go4lunch.view.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.lousssouarn.edouard.go4lunch.R;
import com.lousssouarn.edouard.go4lunch.api.RestaurantFirebase;
import com.lousssouarn.edouard.go4lunch.api.UserFirebase;
import com.lousssouarn.edouard.go4lunch.model.Restaurant;
import com.lousssouarn.edouard.go4lunch.model.User;
import com.lousssouarn.edouard.go4lunch.view.adapter.WorkmatesListRestaurantDetailsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RestaurantDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private PlacesClient placesClient;
    private RecyclerView recyclerView;
    private WorkmatesListRestaurantDetailsAdapter adapter;

    private List<User> users;
    private List<Restaurant> favorites;
    private List<User> usersLikes = new ArrayList<>();
    private List<User> usersGoes = new ArrayList<>();
    private Restaurant restaurant, restaurantFromFireStore;
    private User currentUser;
    private String uId, phoneNumber, restaurantId;
    private Uri webSite;

    private LatLng placeLatLng;
    private Place displayPlace;
    private Location placeLocation = new Location("restaurant");
    private TextView name, address;
    private ImageView picture;
    private ImageButton callButton, webSiteButton, likeButton;
    private FloatingActionButton choiceButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        name = findViewById(R.id.details_name_txt);
        address = findViewById(R.id.details_address_txt);
        picture = findViewById(R.id.restaurant_details_picture);
        callButton = findViewById(R.id.details_call_button);
        webSiteButton = findViewById(R.id.details_website_button);
        likeButton = findViewById(R.id.details_like_button);
        choiceButton = findViewById(R.id.detail_choice_fab);
        recyclerView = findViewById(R.id.details_restaurant_recyclerview);

        initializePlaces();
        recoverPlace();
        displayRecyclerView();
        updateActivity();

        callButton.setOnClickListener(this);
        webSiteButton.setOnClickListener(this);
        likeButton.setOnClickListener(this);
        choiceButton.setOnClickListener(this);

    }

    private void updateActivity() {
        getCurrentUser();
        getUser();
    }

    private void displayChoice(User currentUser) {
        Log.e("Resto", "goRestaurant = "  + currentUser.getGoRestaurant() + " display = " + displayPlace.getId());
        if (currentUser.getGoRestaurant().equals(displayPlace.getId())) {
            choiceButton.setImageResource(R.drawable.ic_go_restaurant);
            choiceButton.setSelected(true);
            Log.e("Resto", "true");
        } else {
            choiceButton.setImageResource(R.drawable.ic_not_go_restaurant);
            choiceButton.setSelected(false);
            Log.e("Resto", "false");
        }

    }

    // Place
    private void recoverPlace() {

        if (getIntent() != null) {
            placeLatLng = getIntent().getExtras().getParcelable("Position");
        }
        for (Map.Entry<Place, LatLng> entry : MainActivity.hashMap.entrySet()) {
            Place place = entry.getKey();
            LatLng hashMapPlaceLatLng = entry.getValue();
            if ( placeLatLng != null && placeLatLng.equals(hashMapPlaceLatLng)) {
                displayPlace = place;
                name.setText(displayPlace.getName());
                address.setText(displayPlace.getAddress());
                recoverPicture(displayPlace);
                phoneNumber = displayPlace.getPhoneNumber();
                webSite = displayPlace.getWebsiteUri();
                placeLocation.setLatitude(hashMapPlaceLatLng.latitude);
                placeLocation.setLongitude(hashMapPlaceLatLng.longitude);
                restaurantId = displayPlace.getId();
            }
        }
        createRestaurantInFirebase(displayPlace);
        getUsersGoesListFromFireStore(displayPlace);
    }

    private void initializePlaces() {
        String apiKey = getString(R.string.api_key);
        //Initialize Places. For simplicity, the API key is hard-coded.
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        // Construct a PlacesClient
        placesClient = Places.createClient(this);
    }

    private void createRestaurantInFirebase(Place displayPlace) {
        if(displayPlace != null)
            restaurant = new Restaurant(displayPlace.getId(),displayPlace.getName(), displayPlace.getAddress(), usersLikes, usersGoes);
        RestaurantFirebase.createRestaurant(restaurant.getRestaurantId(), restaurant.getName(), restaurant.getAddress(),restaurant.getUsersLikes(), restaurant.getUsersGoes());
    }

    private List<User> getUsersGoesListFromFireStore(Place displayPlace) {
        restaurantId = displayPlace.getId();
        RestaurantFirebase.getRestaurant(restaurantId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                restaurantFromFireStore = documentSnapshot.toObject(Restaurant.class);
            }
        });
        if(restaurantFromFireStore != null){
            users = restaurantFromFireStore.getUsersGoes();
        }
        else
        {
            users = usersGoes;
        }

        return users;
    }

    public void recoverPicture(Place displayPlace) {
        FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(Objects.requireNonNull(displayPlace.getPhotoMetadatas()).get(0))
                .build();
        placesClient.fetchPhoto(photoRequest).addOnSuccessListener(
                new OnSuccessListener<FetchPhotoResponse>() {
                    @Override
                    public void onSuccess(FetchPhotoResponse response) {
                        Bitmap photo = response.getBitmap();
                        picture.setImageBitmap(photo);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        exception.printStackTrace();
                    }
                });
    }

    // RecyclerView

    private void configureRecyclerview() {

        //getUsersGoesListFromFireStore(displayPlace);
        Log.e( "Recyclerview", " " + users.size());
        adapter = new WorkmatesListRestaurantDetailsAdapter(users, Glide.with(this), getParent());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getParent()));
    }

    private void displayRecyclerView() {
        
        if (users == null) {
            recyclerView.setVisibility(View.GONE);
        } else{
            configureRecyclerview();
        }
    }

    // User
    /**
     * Use it for recover the current user from Firebase
     */
    protected FirebaseUser getCurrentUser() { return FirebaseAuth.getInstance().getCurrentUser();}

    /**
     * Use it for recover the current user from FireStore
     */
    private void getUser(){
        uId  = getCurrentUser().getUid();
        Log.e("User", "UID :" + uId);
        UserFirebase.getUser(uId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
             currentUser = documentSnapshot.toObject(User.class);
                Log.e("User", "CurrentUser :" + currentUser);
                Log.e("User", "User name :" + currentUser.getName());
                if(currentUser != null)
                    displayChoice(currentUser);
            }
        });
    }

    // Click Listener
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.details_call_button:
                if (phoneNumber != null) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phoneNumber));
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.details_restaurant_no_phone), Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.details_website_button:
                if (webSite != null) {
                    startActivity(new Intent(Intent.ACTION_VIEW, webSite));
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.details_restaurant_no_website), Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.details_like_button:

                if(currentUser.getFavorites() == null) {
                    favorites = new ArrayList<>();
                    favorites.add(restaurant);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.details_restaurant_added_favorites), Toast.LENGTH_LONG).show();
                    Log.e("Resto", "La liste a été créée " + "Taille liste :" + favorites.size());
                    UserFirebase.updateFavoritesList(uId, favorites);

                } else {
                    favorites = currentUser.getFavorites();
                    Log.e("Resto", "La liste existe et contient:" + favorites.size());
                    if (favorites.contains(restaurant)) {
                        favorites.remove(restaurant);
                        UserFirebase.updateFavoritesList(uId, favorites);
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.details_restaurant_removed_favorites), Toast.LENGTH_LONG).show();
                        Log.e("Resto", "Le restaurant était dans la liste et a été supprimé");
                    } else {
                        favorites.add(restaurant);
                        UserFirebase.updateFavoritesList(uId, favorites);
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.details_restaurant_added_favorites), Toast.LENGTH_LONG).show();
                        Log.e("Resto", "La liste était déjà créée et le restaurant ajouté");
                    }
                }
                break;

            case R.id.detail_choice_fab:

                getCurrentUser();

                if(!choiceButton.isSelected() && uId != null) {
                    UserFirebase.updateGoRestaurant(uId,displayPlace.getId());
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.details_restaurant_choose_for_lunch), Toast.LENGTH_LONG).show();
                    choiceButton.setImageResource(R.drawable.ic_go_restaurant);
                    choiceButton.setSelected(true);

                    Log.e("Details", "Passe par if " + choiceButton.isSelected() );

                    usersGoes = restaurant.getUsersGoes();
                    Log.e("Resto", "userGoes = " + usersGoes.size());
                    usersGoes.add(currentUser);
                    Log.e("Resto", "userGoes = " + usersGoes.size());
                    RestaurantFirebase.updateUsersGoesList(restaurantId, usersGoes);
                    displayRecyclerView();
                }
                else if (choiceButton.isSelected())
                {
                    UserFirebase.updateGoRestaurant(uId,"");
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.details_restaurant_no_longer_choose_for_lunch), Toast.LENGTH_LONG).show();
                    choiceButton.setImageResource(R.drawable.ic_not_go_restaurant);
                    choiceButton.setSelected(false);
                    Log.e("Details", "Passe par else");

                    usersGoes = restaurant.getUsersGoes();
                    usersGoes.remove(currentUser);
                    RestaurantFirebase.updateUsersGoesList(restaurantId, usersGoes);
                    displayRecyclerView();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        //updateActivity();
        super.onResume();
    }
}