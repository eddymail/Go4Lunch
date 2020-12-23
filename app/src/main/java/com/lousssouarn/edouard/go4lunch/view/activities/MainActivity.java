package com.lousssouarn.edouard.go4lunch.view.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.lousssouarn.edouard.go4lunch.R;
import com.lousssouarn.edouard.go4lunch.api.UserFirebase;
import com.lousssouarn.edouard.go4lunch.model.User;
import com.lousssouarn.edouard.go4lunch.utils.CurrentPlacesListener;
import com.lousssouarn.edouard.go4lunch.utils.PlaceListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements PlaceSelectionListener, OnCompleteListener<FindCurrentPlaceResponse>, NavigationView.OnNavigationItemSelectedListener {

    // For Data
    Place place = null;
    User currentUser;
    static HashMap <Place, LatLng> hashMap = new HashMap<>();
    List<PlaceListener> placeListeners = new ArrayList<>();
    List<Place> currentPlaces = new ArrayList<>();
    List<CurrentPlacesListener> currentPlacesListeners = new ArrayList<>();
    String uId;

    // The entry point to the Places API.
    private PlacesClient placesClient;
    // For Design
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private NavController navController;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configureToolBar();
        configureDrawerLayout();
        configureNavigationView();

        getUser();
        initializePlaces();
        setupAutoCompleteSupportFragment();
        recoverCurrentPlaces();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.menu_drawer_lunch :
                showLunch();
                break;
            case R.id.menu_drawer_settings :

                break;
            case R.id.menu_drawer_logout :
                logOut();
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;

    }
    private void showLunch()
    {
        if (currentUser.getGoRestaurant() != null)
        {
            Intent intent = new Intent(MainActivity.this, RestaurantDetailsActivity.class);
            intent.putExtra("goRestaurant", currentUser.getGoRestaurant());
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.main_activity_no_choose_restaurant), Toast.LENGTH_LONG).show();
        }
    }

    private void logOut()
    {
        AuthUI.getInstance().signOut(this).addOnSuccessListener(this, aVoid ->
        {
            if (FirebaseAuth.getInstance().getCurrentUser() == null)
            {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.main_activity_success_log_out), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                startActivity(intent);
            }
        });
    }

    private void configureNavigationView() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        navController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void configureDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureToolBar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void updateNavigationHeader(User currentUser)
    {
        final View headerView = navigationView.getHeaderView(0);
        TextView name = headerView.findViewById(R.id.nav_header_name_txt);
        TextView email = headerView.findViewById(R.id.nav_header_email_txt);
        ImageView illustrationUser = headerView.findViewById(R.id.nav_header_image_view);
        name.setText(currentUser.getName());
        email.setText(currentUser.getEmail());
        if (currentUser.getUrlPicture() != null)
        {
            Glide.with(this).load(currentUser.getUrlPicture()).circleCrop().into(illustrationUser);
        }
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

    private void setupAutoCompleteSupportFragment() {
        // Initialize the AutocompleteSupportFragment
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID,
                Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS,
                Place.Field.OPENING_HOURS,
                Place.Field.PHOTO_METADATAS,
                Place.Field.PHONE_NUMBER,
                Place.Field.WEBSITE_URI,
                Place.Field.RATING,
                Place.Field.TYPES
                ));

        autocompleteFragment.setOnPlaceSelectedListener(this);

    }

    //Action when a place is selected
    @Override
    public void onPlaceSelected(@NonNull Place place) {

        for (PlaceListener listener : placeListeners) {

            if (place.getTypes().contains(Place.Type.RESTAURANT) )

            listener.onPlace(place);
            hashMap.put(place, place.getLatLng());

            Log.e("Hashmap",hashMap.size() + " /");
        }

        MainActivity.this.place = place;

        currentPlaces.clear();
        initializePlaces();
        recoverCurrentPlaces();

    }

    @Override
    public void onError(@NonNull Status status) {
        Log.e("Error", status.getStatusMessage());

    }


    public void recoverCurrentPlaces() {
        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                //Place.Field.ADDRESS,
                Place.Field.ID,
                //Place.Field.OPENING_HOURS,
                //Place.Field.PHONE_NUMBER,
                //Place.Field.WEBSITE_URI,
                //Place.Field.RATING,
                Place.Field.PHOTO_METADATAS,
                Place.Field.TYPES
                );
        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);
        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);

            Log.e("Test","recoverCurrentPlaces lancée");

            placeResponse.addOnCompleteListener(this);

        }
    }

    //Called when a Task completes
    @Override
    public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {

        Log.e("Test","onComplete start");

        if (task.isSuccessful()) {
            // Task completed successfully
            FindCurrentPlaceResponse response = task.getResult();

            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                final Place currentPlace = placeLikelihood.getPlace();
                //Check if place is a restaurant
                if (currentPlace.getTypes().contains(Place.Type.RESTAURANT) ) {
                    currentPlaces.add(placeLikelihood.getPlace());
                    Log.e("Test", "currentPlaces size " + currentPlaces.size());

                    hashMap.put(placeLikelihood.getPlace(), placeLikelihood.getPlace().getLatLng());
                    Log.e("Test","Hashmap size "+ hashMap.size());
                }
            }
            for (CurrentPlacesListener listener : currentPlacesListeners) {
                listener.onPlaceList(currentPlaces);
            }
        }
        else
            {
            // Task failed with an exception
            Exception exception = task.getException();

            Log.e("Test","onComplete exception: TASK FAILED ");
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
                updateNavigationHeader(currentUser);
            }
        });

    }


    // Listeners
    public void addPlaceListener(PlaceListener listener) {
        placeListeners.add(listener);
        if (place != null) {
            listener.onPlace(place);
        }
    }

    public void removePlaceListener(PlaceListener listener) {
        placeListeners.remove(listener);
    }

    public void addCurrentPlacesListener(CurrentPlacesListener listener) {
        currentPlacesListeners.add(listener);
        if (currentPlaces != null) {
            listener.onPlaceList(currentPlaces);
        }
    }

    public void removeCurrentPlacesListener(CurrentPlacesListener listener) {
        currentPlacesListeners.remove(listener);
    }

    @Override
    public void onBackPressed() {
        // 5 - Handle back click to close menu
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}