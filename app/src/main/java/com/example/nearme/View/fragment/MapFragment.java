package com.example.nearme.View.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.example.nearme.Adapter.CustomInfoWindowAdapter;
import com.example.nearme.Model.PlaceModel;
import com.example.nearme.R;
import com.example.nearme.View.DrawerActivity;
import com.example.nearme.View.MapDetailActivity;
import com.example.nearme.View.PlaceDetailActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referencePlace = database.getReference().child("places");
    BitmapDescriptor icon;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

    private void loadMaker() {
        referencePlace.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Double lat = (Double) dataSnapshot.child("latitude").getValue();
                Double lng = (Double) dataSnapshot.child("longitude").getValue();
                LatLng latLng = new LatLng(lat, lng);
                String name = (String) dataSnapshot.child("name").getValue();
                String type = (String) dataSnapshot.child("type").getValue();
                Log.d("Type",type);
                String address = (String)dataSnapshot.child("address").getValue();
                if (type.equals("COFFEE")) {
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.coffee_n_tea);
                } else if (type.equals("HOTEL")) {
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.hotels);
                }
                  else if (type.equals("RESTAURANT")){
                    icon=BitmapDescriptorFactory.fromResource(R.drawable.restaurants);
                }
                else if (type.equals("BAR")){
                    icon=BitmapDescriptorFactory.fromResource(R.drawable.bars);
                }
                else if (type.equals("SCHOOL")){
                    icon=BitmapDescriptorFactory.fromResource(R.drawable.schools);
                }
                else if (type.equals("SALOON")){
                    icon=BitmapDescriptorFactory.fromResource(R.drawable.saloon);
                }
                else if (type.equals("SHOPPING")){
                    icon=BitmapDescriptorFactory.fromResource(R.drawable.shopping);
                }
                else if (type.equals("SPORT")){
                    icon=BitmapDescriptorFactory.fromResource(R.drawable.sports);
                }
                else if (type.equals("HOSPITAL")){
                    icon=BitmapDescriptorFactory.fromResource(R.drawable.medical);
                }
                else if (type.equals("GAME")){
                    icon=BitmapDescriptorFactory.fromResource(R.drawable.computers);
                }
                else if (type.equals("KARAOKE")){
                    icon=BitmapDescriptorFactory.fromResource(R.drawable.karaoke);
                }
                else if (type.equals("ATM")){
                    icon=BitmapDescriptorFactory.fromResource(R.drawable.atm);
                }
                else if (type.equals("SERVICE")){
                    icon=BitmapDescriptorFactory.fromResource(R.drawable.services);
                }

                else {icon = BitmapDescriptorFactory.fromResource(R.drawable.defaultt);}

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng)
                        .title(name).snippet(address).icon(icon);

                final CameraPosition position = CameraPosition.builder().target(latLng)
                        .zoom(14)
                        .bearing(3)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1500, null);
                CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(getActivity());
                mMap.setInfoWindowAdapter(adapter);
                mMap.addMarker(markerOptions).showInfoWindow();
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Intent intent = new Intent(getContext(), MapDetailActivity.class);
                        intent.putExtra("name", marker.getTitle().toString());
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        loadMaker();

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);


    }
}
