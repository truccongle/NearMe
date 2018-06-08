package com.example.nearme.Adapter;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.nearme.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Activity context;


    public CustomInfoWindowAdapter(Activity context){
        this.context=context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view= context.getLayoutInflater().inflate(R.layout.custom_info_window,null);
        TextView tvName=view.findViewById(R.id.tv_Name);
        TextView tvAddress=view.findViewById(R.id.tv_Address);

        tvName.setText(marker.getTitle());
        tvAddress.setText(marker.getSnippet());

        return view;
    }
}
