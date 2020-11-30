package com.lousssouarn.edouard.go4lunch.view.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.lousssouarn.edouard.go4lunch.R;
import com.lousssouarn.edouard.go4lunch.utils.CurrentPlacesListener;
import com.lousssouarn.edouard.go4lunch.utils.PlaceListener;
import com.lousssouarn.edouard.go4lunch.view.adapter.ListViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements PlaceSelectionListener, OnCompleteListener<FindCurrentPlaceResponse> {

    private static final String TAG = null;
    //FOR DATA
    Place place = null;

    List<PlaceListener> placeListeners = new ArrayList<>();
    List<Place> currentPlaces = new ArrayList<>();
    List<CurrentPlacesListener> currentPlacesListeners = new ArrayList<>();

    ListViewAdapter adapter = null;

    // The entry point to the Places API.
    private PlacesClient placesClient;

    //FOR DESIGN
    BottomNavigationView bottomNavigationView;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        navController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        initializePlaces();
        setupAutoCompleteSupportFragment();
        recoverCurrentPlaces();

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
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS,
                Place.Field.OPENING_HOURS,
                Place.Field.PHOTO_METADATAS,
                Place.Field.RATING));

        autocompleteFragment.setOnPlaceSelectedListener(this);

    }

    //Action when a place is selected
    @Override
    public void onPlaceSelected(@NonNull Place place) {
        for (PlaceListener listener : placeListeners) {
            listener.onPlace(place);
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
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);
        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);

            placeResponse.addOnCompleteListener(this);

        }
    }

    //Called when a Task completes
    @Override
    public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
        if (task.isSuccessful()) {
            // Task completed successfully
            FindCurrentPlaceResponse response = task.getResult();
            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                currentPlaces.add(placeLikelihood.getPlace());
            }
            for (CurrentPlacesListener listener : currentPlacesListeners) {
                listener.onPlaceList(currentPlaces);
            }
        } else {
            // Task failed with an exception
            Exception exception = task.getException();
        }
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

}