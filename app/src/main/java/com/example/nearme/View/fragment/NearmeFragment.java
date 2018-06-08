package com.example.nearme.View.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.nearme.Controller.NearmeController;
import com.example.nearme.Model.PlaceModel;
import com.example.nearme.R;

public class NearmeFragment extends Fragment {
    NearmeController nearmeController;
    RecyclerView recyclerViewNearme;
    SharedPreferences sharedPreferences;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_nearme,container,false);
        recyclerViewNearme=view.findViewById(R.id.recyler_Nearme);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        sharedPreferences=getContext().getSharedPreferences("location", Context.MODE_PRIVATE);
        Location curLocation=new Location("");
        curLocation.setLatitude(Double.parseDouble(sharedPreferences.getString("laititude","0")));
        curLocation.setLongitude(Double.parseDouble(sharedPreferences.getString("longitude","0")));
        nearmeController= new NearmeController(getContext());
        nearmeController.getListPlaceController(recyclerViewNearme,curLocation);


    }
}
