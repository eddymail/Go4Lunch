package com.lousssouarn.edouard.go4lunch.view.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.libraries.places.api.model.Place;
import com.lousssouarn.edouard.go4lunch.R;

public class RestaurantDetailsActivity extends AppCompatActivity {

    TextView restaurantName;
    TextView restaurantAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        restaurantName = findViewById(R.id.details_name_txt);
        restaurantAddress = findViewById(R.id.details_address_txt);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = extras.getString("name", null);
            String address = extras.getString("address", null);

            restaurantName.setText(name);
            restaurantAddress.setText(address);
        }
    }
}