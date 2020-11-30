package com.lousssouarn.edouard.go4lunch.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.libraries.places.api.model.Place;
import com.lousssouarn.edouard.go4lunch.R;
import com.lousssouarn.edouard.go4lunch.view.activities.RestaurantDetailsActivity;

import java.util.List;

public class ListViewAdapter extends RecyclerView.Adapter{

    //For Data
    private Context context;
    private List<Place> places ;
    private RequestManager glide;

    public ListViewAdapter(Context context, List<Place> places, RequestManager glide) {
        this.context = context;
        this.places = places;
        this.glide = glide;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant, parent, false);
        ListViewViewHolder listViewViewHolder = new ListViewViewHolder(v);
        return listViewViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Place place = places.get(position);
        ListViewViewHolder listViewViewHolder = (ListViewViewHolder)holder;
        listViewViewHolder.updatePlace(this.places.get(position), glide);
        listViewViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent displayRestaurantDetailsIntent = new Intent(v.getContext(), RestaurantDetailsActivity.class);
                displayRestaurantDetailsIntent.putExtra("name",place.getName());
                displayRestaurantDetailsIntent.putExtra("address",place.getAddress());
                displayRestaurantDetailsIntent.putExtra("phone number",place.getPhoneNumber());
                displayRestaurantDetailsIntent.putExtra("web site", place.getWebsiteUri());
                v.getContext().startActivity(displayRestaurantDetailsIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return places.size();
    }


   static class ListViewViewHolder extends RecyclerView.ViewHolder {
            TextView name, address, hours, distance, numberRating;
            ImageView picture, workmatesIcon, star1, star2, star3;


        public ListViewViewHolder(@NonNull View itemView) {
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
            //hours.setText((CharSequence) place.getOpeningHours());
            //distance.setText(resultat);
            //numberRating.setText(place.getUserRatingsTotal());
            glide.load(place.getPhotoMetadatas()).apply(RequestOptions.centerCropTransform()).into(picture);
        }

    }
}
