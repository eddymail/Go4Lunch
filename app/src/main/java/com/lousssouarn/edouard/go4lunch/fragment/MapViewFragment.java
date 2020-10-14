package com.lousssouarn.edouard.go4lunch.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lousssouarn.edouard.go4lunch.R;
import com.lousssouarn.edouard.go4lunch.utils.PlaceListener;
import com.lousssouarn.edouard.go4lunch.view.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapViewFragment#} factory method to
 * create an instance of this fragment.
 */
public class MapViewFragment extends Fragment implements LocationListener {

    public static final int PERMS_CALL_ID = 1234;
    private GoogleMap map;
    private SupportMapFragment supportMapFragment;
    LocationManager locationManager;
    LatLng userLatLng;
    LatLng latLngPlace;
    String name;
    FloatingActionButton locationButton;
    View mapView;
    private MainActivity mainActivity;
    private PlaceListener placeListener = new PlaceListener() {
        @Override
        public void onPlace(Place place) {
           latLngPlace =  place.getLatLng();
           name = place.getName();
        }
    };

    public MapViewFragment() {
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        checkPermissions();
    }

    private void checkPermissions() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, PERMS_CALL_ID);
            return;
        }

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
        }
        if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 10000, 0, this);
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
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

    @Override
    public void onPause() {
        super.onPause();

        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Initialize view
        View v = inflater.inflate(R.layout.fragment_map_view, container, false);

        locationButton = v.findViewById(R.id.fab_gps_location);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.moveCamera(CameraUpdateFactory.newLatLng(userLatLng));
            }
        });

        mainActivity.addListener(placeListener);

        //Initialize map fragment
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapView = supportMapFragment.getView();
        return v;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        mainActivity.removeListener(placeListener);
    }

    @SuppressLint("MissingPermission")
    public void loadMap() {
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                /*MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.google_style);
                map.setMapStyle(mapStyleOptions);
                 */
                //getting user last location to set the default location marker in the map
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                userLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                //clear old location marker in google map
                map.clear();
                map.addMarker(new MarkerOptions().position(userLatLng).title("Your Location"));
                if (latLngPlace == null) {
                    map.addMarker(new MarkerOptions().position(userLatLng).title("Your Location"));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 16));
                } else {
                    map.addMarker(new MarkerOptions().position(latLngPlace).title(name));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngPlace, 16));
                }
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        if ( map != null) {
            //store user latitude and longitude
            LatLng newUserLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            //clear old location marker in google map
            map.clear();
            map.addMarker(new MarkerOptions().position(newUserLatLng).title("Your Location"));
        }
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
}