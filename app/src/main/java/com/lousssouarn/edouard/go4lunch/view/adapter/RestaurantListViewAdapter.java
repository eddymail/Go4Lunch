package com.lousssouarn.edouard.go4lunch.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.libraries.places.api.model.Place;
import com.lousssouarn.edouard.go4lunch.R;
import com.lousssouarn.edouard.go4lunch.view.activities.RestaurantDetailsActivity;

import java.util.List;

public class RestaurantListViewAdapter extends RecyclerView.Adapter{

    //For Data
    private Context context;
    private List<Place> places ;
    private RequestManager glide;

    public RestaurantListViewAdapter(Context context, List<Place> places, RequestManager glide) {
        this.context = context;
        this.places = places;
        this.glide = glide;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant, parent, false);
        RestaurantListViewViewHolder restaurantListViewViewHolder = new RestaurantListViewViewHolder(v);
        return restaurantListViewViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Place place = places.get(position);
        RestaurantListViewViewHolder restaurantListViewViewHolder = (RestaurantListViewViewHolder)holder;
        restaurantListViewViewHolder.updatePlace(this.places.get(position), glide);
        restaurantListViewViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent displayRestaurantDetailsIntent = new Intent(v.getContext(), RestaurantDetailsActivity.class);
                displayRestaurantDetailsIntent.putExtra("PlaceId",place.getId());
                v.getContext().startActivity(displayRestaurantDetailsIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }


   static class RestaurantListViewViewHolder extends RecyclerView.ViewHolder {
            TextView name, address, hours, distance, numberRating;
            ImageView picture, workmatesIcon, star1, star2, star3;

        public RestaurantListViewViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_restaurant_name_txt);
            address = itemView.findViewById(R.id.item_restaurant_address_txt);
            hours = itemView.findViewById(R.id.item_restaurant_hours_txt);
            distance = itemView.findViewById(R.id.item_restaurant_distance_txt);
            numberRating = itemView.findViewById(R.id.item_restaurant_number_rating_txt);
            picture = itemView.findViewById(R.id.item_restaurant_picture);
            workmatesIcon = itemView.findViewById(R.id.item_restaurant_workmates_icon);
            star1 =itemView.findViewById(R.id.item_restaurant_star_1);
            star2 =itemView.findViewById(R.id.item_restaurant_star_2);
            star3 =itemView.findViewById(R.id.item_restaurant_star_3);
        }

       private void updatePlace(Place place, RequestManager glide) {

                name.setText(place.getName());
                address.setText(place.getAddress());
                updateHours(place);
                //distance.setText(resultat);
                //numberRating.setText(place.getUserRatingsTotal());
                glide.load(place.getPhotoMetadatas()).apply(RequestOptions.centerCropTransform()).into(picture);

        }

        private void updateDistance(){


        }

        private void updateHours(Place place){
            if(place.isOpen() != null && place.isOpen())
            {
                hours.setText(R.string.list_restaurants_open_now);
            } else {
                hours.setText(R.string.list_restaurants_close_now);
            }
        }
    }
}
