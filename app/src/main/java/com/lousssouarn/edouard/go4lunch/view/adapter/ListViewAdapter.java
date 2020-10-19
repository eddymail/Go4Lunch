package com.lousssouarn.edouard.go4lunch.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.model.Place;
import com.lousssouarn.edouard.go4lunch.R;

import java.util.List;

public class ListViewAdapter extends RecyclerView.Adapter{

    //For Data
    Context context;
    private List<Place> places ;

    public ListViewAdapter(Context context, List<Place> places) {
        this.context = context;
        this.places = places;
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
        ListViewViewHolder listViewViewHolder = (ListViewViewHolder)holder;
        listViewViewHolder.updatePlace(this.places.get(position));
        listViewViewHolder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, "Item Selected", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }


    class ListViewViewHolder extends RecyclerView.ViewHolder {
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

       private void updatePlace(Place place) {
            name.setText(place.getName());
            address.setText(place.getAddress());
            //hours.setText((CharSequence) place.getOpeningHours());
            //holder.distance.setText(place.);
            //numberRating.setText(place.getUserRatingsTotal());
            //glide.load(place.getPhotoMetadatas()).apply(RequestOptions.centerCropTransform()).into(picture);
        }
    }
}
