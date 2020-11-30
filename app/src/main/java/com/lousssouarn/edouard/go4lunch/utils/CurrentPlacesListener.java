package com.lousssouarn.edouard.go4lunch.utils;

import com.google.android.libraries.places.api.model.Place;

import java.util.List;

public interface CurrentPlacesListener {
    void onPlaceList(List<Place> currentPlaces);
}
