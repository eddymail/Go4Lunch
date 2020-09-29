package com.lousssouarn.edouard.go4lunch.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.lousssouarn.edouard.go4lunch.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapViewFragment#} factory method to
 * create an instance of this fragment.
 */
public class MapViewFragment extends Fragment {

    private GoogleMap map;
    SupportMapFragment supportMapFragment;
    LocationManager locationManager;
    LocationListener locationListener;
    LatLng userLatLng;
    FloatingActionButton locationButton;


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

        locationButton = (FloatingActionButton) v.findViewById(R.id.fab_gps_location);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.moveCamera(CameraUpdateFactory.newLatLng(userLatLng));
            }
        });

        //Initialize map fragment
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);



        //Async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                map = googleMap;

                locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        // store user latLng
                        userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        // clear old location marker in google map
                        map.clear();
                        map.addMarker(new MarkerOptions().position(userLatLng).title("Your Location"));

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
                };

                //Asking for map permission with user / location permission

                askLocationPermission();
            }
        });

        return v;
}

    private void askLocationPermission() {
        Dexter.withActivity(getActivity()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                //getting user last location to set the default location marker in the map

                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                userLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                // clear old location marker in google map
                map.clear();
                map.addMarker(new MarkerOptions().position(userLatLng).title("Your Location"));
                map.moveCamera(CameraUpdateFactory.newLatLng(userLatLng));
                //Zoom on location
                float zoomLevel = 16.0f;
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, zoomLevel));
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.cancelPermissionRequest();
            }
        }).check();
    }

}