package com.lousssouarn.edouard.go4lunch.view;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lousssouarn.edouard.go4lunch.R;
import com.lousssouarn.edouard.go4lunch.fragment.MapViewFragment;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    //FOR DATA
    LatLng searchLocationLatLng;
    MapFragment mapFragment;

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

    }

    private void initializePlaces(){
        String apiKey = getString(R.string.api_key);

        //Initialize Places. For simplicity, the API key is hard-coded.
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        // Create a new Places client instance.
        //PlacesClient placesClient = Places.createClient(this);

    }

    private void setupAutoCompleteSupportFragment() {
        // Initialize the AutocompleteSupportFragment
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.OPENING_HOURS, Place.Field.PHOTO_METADATAS, Place.Field.RATING));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                searchLocationLatLng = place.getLatLng();
                
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.e("Error", status.getStatusMessage());
            }
        });

    }
}