package com.example.nearme.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.example.nearme.Adapter.CustomInfoWindowAdapter;
import com.example.nearme.R;
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

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {
    private GoogleMap mMap;
    FloatingSearchView mSearchView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referencePlace = database.getReference().child("places");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSearchView=findViewById(R.id.floating_search_view);

        sreachView();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_Add_Place);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Bạn muốn thêm địa chỉ còn thiếu ?", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void sreachView() {

        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                // automatically handle clicks on the Home/Up button, so long
                // as you specify a parent activity in AndroidManifest.xml.
                int id = item.getItemId();

                //noinspection SimplifiableIfStatement
                if (id == R.id.action_voice) {
                    Toast.makeText(getApplicationContext(),"Voice",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_MAP_TYPE_NORMAL) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            return true;
        }
        else if(id==R.id.action_MAP_TYPE_HYBRID){
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            return true;
        }
        else if (id==R.id.action_MAP_TYPE_SATELLITE){
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            return true;
        }
        else if (id==R.id.action_MAP_TYPE_TERRAIN){
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void loadMaker() {
        referencePlace.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Double lat = Double.valueOf(dataSnapshot.child("lat").getValue().toString());
                Double lng = Double.valueOf(dataSnapshot.child("lng").getValue().toString());
                LatLng latLng = new LatLng(lat, lng);
                String name = dataSnapshot.child("name").getValue().toString();
                Log.d("Data",name);
                String type =dataSnapshot.child("type").getValue().toString();
                String address=dataSnapshot.child("address").getValue().toString();
                Log.d("Data",type);
                BitmapDescriptor icon;
                if (type.equals("coffee")){
                    icon=BitmapDescriptorFactory.fromResource(R.drawable.coffee_n_tea);
                }
                else if (type.equals("gas")){
                    icon=BitmapDescriptorFactory.fromResource(R.drawable.hotels);
                }
                else icon=BitmapDescriptorFactory.fromResource(R.drawable.place_map64);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng)
                        .title(name).snippet(address).icon(icon);


//                mMap.addMarker(new MarkerOptions().title(name).snippet(name)
//                        .icon(icon)
//                        .position(latLng).flat(true)
//                        .rotation(0));
                CameraPosition position=CameraPosition.builder().target(latLng)
                        .zoom(13)
                        .bearing(3)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position),1500,null);
                CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(DrawerActivity.this);
                mMap.setInfoWindowAdapter(adapter);
                mMap.addMarker(markerOptions).showInfoWindow();
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Intent intent= new Intent(getApplicationContext(),PlaceDetailActivity.class);
                        intent.putExtra("name",marker.getTitle().toString());
                        startActivity(intent);
//                        Picasso.get().load()
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        loadMaker();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
