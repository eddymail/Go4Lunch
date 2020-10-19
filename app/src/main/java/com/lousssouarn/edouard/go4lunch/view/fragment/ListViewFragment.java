package com.lousssouarn.edouard.go4lunch.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.model.Place;
import com.lousssouarn.edouard.go4lunch.R;
import com.lousssouarn.edouard.go4lunch.utils.CurrentPlacesListener;
import com.lousssouarn.edouard.go4lunch.view.activities.MainActivity;
import com.lousssouarn.edouard.go4lunch.view.adapter.ListViewAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListViewFragment#} factory method to
 * create an instance of this fragment.
 */
public class ListViewFragment extends Fragment {
    //For data
    List<Place> places;
    RecyclerView recyclerView;
    MainActivity mainActivity;

   private CurrentPlacesListener currentPlacesListener = new CurrentPlacesListener() {
        @Override
        public void onPlaceList(List<Place> currentPlaces) {
            Log.e("Test", currentPlaces.size() + "");
            places = currentPlaces;
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_list_view, container, false);
        mainActivity.addCurrentPlacesListener(currentPlacesListener);

        recyclerView = v.findViewById(R.id.listRestaurant);
        ListViewAdapter adapter = new ListViewAdapter(getContext(),places);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        mainActivity.removeCurrentPlacesListener(currentPlacesListener);
    }

  /*  @Override
    public void onResume(){
        super.onResume();
        activity.recoverCurrentPlaces();
        adapter.notifyDataSetChanged();
    }
    */
}