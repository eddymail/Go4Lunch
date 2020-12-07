package com.lousssouarn.edouard.go4lunch.view.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
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
    private Place place = null;

    private List<PlaceListener> placeListeners = new ArrayList<>();
    private List<Place> currentPlaces = new ArrayList<>();
    private List<CurrentPlacesListener> currentPlacesListeners = new ArrayList<>();

    private ListViewAdapter adapter = null;
    private StringBuilder result;

    // The entry point to the Places API.
    private PlacesClient placesClient;

    //FOR DESIGN
    private BottomNavigationView bottomNavigationView;
    private NavController navController;
    private EditText queryText;
    private Toolbar toolbar;
    private TextView searchResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        navController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        queryText = findViewById(R.id.toolbar_edit_txt);
        searchResult = findViewById(R.id.result_search);

        configureToolbar();
        initializePlaces();
       // setupAutoCompleteSupportFragment();
        recoverCurrentPlaces();

    }

    /**
     * Configure the Toolbar {@link Toolbar}
     */
    private void configureToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.toolbar_search) {
           getPlacePrediction();
        }
        return super.onOptionsItemSelected(item);
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

   /* private void setupAutoCompleteSupportFragment() {
        // Initialize the AutocompleteSupportFragment
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS,
                Place.Field.OPENING_HOURS,
                Place.Field.PHOTO_METADATAS,
                Place.Field.TYPES,
                Place.Field.RATING));

        autocompleteFragment.setOnPlaceSelectedListener(this);

    }*/

    //Action when a place is selected
    @Override
    public void onPlaceSelected(@NonNull Place place) {
        for (PlaceListener listener : placeListeners) {

          //  if (place.getTypes().contains(Place.Type.RESTAURANT) )

            listener.onPlace(place);
        }

        MainActivity.this.place = place;

        currentPlaces.clear();
        initializePlaces();
        //recoverCurrentPlaces();
    }

    @Override
    public void onError(@NonNull Status status) {
        Log.e("Error", status.getStatusMessage());
    }


    public void recoverCurrentPlaces() {
        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.PHOTO_METADATAS, Place.Field.TYPES);
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

                final Place currentPlace = placeLikelihood.getPlace();

                //Check if place is a restaurant
                if (currentPlace.getTypes().contains(Place.Type.RESTAURANT) ) {
                    currentPlaces.add(placeLikelihood.getPlace());
                }

            }
            for (CurrentPlacesListener listener : currentPlacesListeners) {
                listener.onPlaceList(currentPlaces);
            }
        } else {
            // Task failed with an exception
            Exception exception = task.getException();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

public void getPlacePrediction() {
    Toast.makeText(this, queryText.getText().toString(), Toast.LENGTH_SHORT).show();

    AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

    RectangularBounds bounds = RectangularBounds.newInstance(
            new LatLng(-33.880490, 151.184363),
            new LatLng(-33.858754, 151.229596));

    FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder().setLocationBias(bounds)
            .setOrigin(new LatLng(-33.8749937, 151.2041382))
            .setCountry("FR")
            .setTypeFilter(TypeFilter.ESTABLISHMENT)
            .setSessionToken(token)
            .setQuery(queryText.getText().toString())
            .build();

    placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
        result = new StringBuilder();
        for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {

            // Filter restaurant from establishment
            if (prediction.getPlaceTypes().contains(Place.Type.RESTAURANT))
            {
              String placeID = prediction.getPlaceId();

              // Recover the place
              getPlaceById(placeID);


            }
            /* result.append(" ").append(prediction.getFullText(null) + "\n");
            Log.i(TAG, prediction.getPlaceId());
            Log.i(TAG, prediction.getPrimaryText(null).toString());
            Toast.makeText(this, prediction.getPrimaryText(null) + "-" + prediction.getSecondaryText(null), Toast.LENGTH_SHORT).show();*/

            // Add the place to listener
            onPlaceSelected(place);
        }



    }).addOnFailureListener((exception) -> {
        if (exception instanceof ApiException) {
            ApiException apiException = (ApiException) exception;
            Log.e(TAG, "Place not found: " + apiException.getStatusCode());
        }
    });
}

    private void getPlaceById(String placeId) {

        // Specify the fields to return.
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        // Construct a request object, passing the place ID and fields array.
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            place = response.getPlace();
            Log.i("Test", "Place found: " + place.getName());
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + exception.getMessage());
                final int statusCode = apiException.getStatusCode();
                // TODO: Handle error with given status code.
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

}