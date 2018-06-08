package com.example.nearme.Controller;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.nearme.Adapter.AdapterRecyclerNearme;
import com.example.nearme.Model.Interfaces.NearmeInterface;
import com.example.nearme.Model.PlaceModel;
import com.example.nearme.R;

import java.util.ArrayList;
import java.util.List;

public class NearmeController {
    Context context;
    PlaceModel placeModel;
    AdapterRecyclerNearme adapterRecyclerNearme;
    public NearmeController(Context context){
        this.context=context;
        placeModel= new PlaceModel();

    }
    public void getListPlaceController(RecyclerView recyclerViewNaerme, Location curLocation){
        final List<PlaceModel>  placeModelList= new ArrayList<>();
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(context);
        recyclerViewNaerme.setLayoutManager(layoutManager);
        adapterRecyclerNearme=new AdapterRecyclerNearme(context,placeModelList, R.layout.custom_layout_recycle_nearme);
        recyclerViewNaerme.setAdapter(adapterRecyclerNearme);
        NearmeInterface nearmeInterface =new NearmeInterface() {
            @Override
            public void getListPlaceModel(PlaceModel placeModel) {
                placeModelList.add(placeModel);
                adapterRecyclerNearme.notifyDataSetChanged();

            }
        };
        placeModel.getListPlace(nearmeInterface,curLocation);


    }
}
