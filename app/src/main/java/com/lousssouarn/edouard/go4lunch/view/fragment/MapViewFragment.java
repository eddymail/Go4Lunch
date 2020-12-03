package com.lousssouarn.edouard.go4lunch.view.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lousssouarn.edouard.go4lunch.R;
import com.lousssouarn.edouard.go4lunch.utils.CurrentPlacesListener;
import com.lousssouarn.edouard.go4lunch.utils.PlaceListener;
import com.lousssouarn.edouard.go4lunch.view.activities.MainActivity;
import com.lousssouarn.edouard.go4lunch.view.activities.RestaurantDetailsActivity;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapViewFragment#} factory method to
 * create an instance of this fragment.
 */
public class MapViewFragment extends Fragment implements LocationListener, View.OnClickListener {

    public static final int PERMS_CALL_ID = 1234;
    private GoogleMap map;
    private SupportMapFragment supportMapFragment;
    private LocationManager locationManager;
    private View mapView;

    private Math math;
    public Location lastLocation;
    private LatLng userLatLng;
    private List<Place> places;
    private Place selectedPlace;

    private FloatingActionButton locationButton;

    private MainActivity mainActivity;

    private final PlaceListener placeListener = new PlaceListener() {
        @Override
        public void onPlace(Place place) {
            Log.e("Test", place.getName() + " place");
            selectedPlace = place;
        }
    };

    private final CurrentPlacesListener currentPlacesListener = new CurrentPlacesListener() {
        @Override
        public void onPlaceList(List<Place> currentPlaces) {
            Log.e("Test", currentPlaces.size() + " restaurants ");
            places = currentPlaces;
        }
    };

    public MapViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Initialize view
        View v = inflater.inflate(R.layout.fragment_map_view, container, false);

        locationButton = v.findViewById(R.id.fab_gps_location);

        locationButton.setOnClickListener(this);

        mainActivity.addPlaceListener(placeListener);
        mainActivity.addCurrentPlacesListener(currentPlacesListener);

        //Initialize map fragment
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapView = supportMapFragment.getView();


        return v;

    }

    private void checkPermissions() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, PERMS_CALL_ID);
            return;
        }

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
        }
        if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 10000, 0, this);
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);
        }

        loadMap();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMS_CALL_ID) {
            checkPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    public void loadMap() {

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.style_json);
                map.setMapStyle(mapStyleOptions);

                //getting user last location to set the default location marker in the map
                lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                userLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                //clear old location marker in google map

                map.clear();

                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(final Marker marker) {

                        Log.e("Test", "CLIC");

                     /*   Intent displayRestaurantDetailsIntent = new Intent(getContext(), RestaurantDetailsActivity.class);
                        displayRestaurantDetailsIntent.putExtra("name", marker.getTitle());
                        displayRestaurantDetailsIntent.putExtra("address", marker.getPosition());
                        //displayRestaurantDetailsIntent.putExtra("phone number",place.getPhoneNumber());
                        //displayRestaurantDetailsIntent.putExtra("web site", place.getWebsiteUri());
                        getContext().startActivity(displayRestaurantDetailsIntent);
                        //marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_green));

                        // Return false to indicate that we have not consumed the event and that we wish
                        // for the default behavior to occur (which is for the camera to move such that the
                        // marker is centered and for the marker's info window to open, if it has one).*/
                        return false;
                    }
                });

                if (selectedPlace == null) {
                    displayUserLocation();
                } else {
                    displayCurrentPlaces();
                    Log.e("Test", "affiche selectedPlace");
                }

            }
        });

    }

     public float getDistanceFromPlace(Place place) {

        Location placeLocation = new Location("restaurant");

        placeLocation.setLatitude(place.getLatLng().latitude);
        placeLocation.setLongitude(place.getLatLng().longitude);

        float distanceResult = (float) (lastLocation.distanceTo(placeLocation) / 1000.0);

        return distanceResult;

       }

    public void displayUserLocation() {

        if (map == null) {
            return;
        }
        //map.clear();
        map.addMarker(new MarkerOptions().position(userLatLng).title("Your Location"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 16));
    }


    public void displayCurrentPlaces() {
        if (map == null) {
            return;
        }

        if (selectedPlace != null) {

            map.addMarker(new MarkerOptions()
                    .position(selectedPlace.getLatLng())
                    .title(selectedPlace.getName())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_orange)));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedPlace.getLatLng(), 16));

            getDistanceFromPlace(selectedPlace);

            Log.e("Test",selectedPlace.getName() + getDistanceFromPlace(selectedPlace) + " m" );

        } else {

            if (!places.isEmpty()) {
                for (Place place : places) {
                    if (place.getTypes().contains(Place.Type.RESTAURANT)) {
                        map.addMarker(new MarkerOptions()
                                .position(place.getLatLng())
                                .title(place.getName())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_orange)));
                    }
                    //getDistanceFromPlace(place);
                }
            } else {
                Log.e("Test", places.size() + " places est vide ");
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (map != null) {
            //store user latitude and longitude
            LatLng newUserLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            //clear old location marker in google map
            map.clear();
            //Log.e("Test", "onLocationChanged Actif");

            displayCurrentPlaces();

            map.addMarker(new MarkerOptions().position(newUserLatLng).title("Your Location"));
        }
    }

    @Override
    public void onClick(View v) {
        map.moveCamera(CameraUpdateFactory.newLatLng(userLatLng));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onResume() {
        super.onResume();
        checkPermissions();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainActivity.removePlaceListener(placeListener);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }
}