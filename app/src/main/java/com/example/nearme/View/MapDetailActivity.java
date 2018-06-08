package com.example.nearme.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nearme.Adapter.AdapterComment;
import com.example.nearme.Model.PlaceModel;
import com.example.nearme.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MapDetailActivity extends AppCompatActivity implements OnMapReadyCallback{
    TextView txtToolbarName,txtName,txtType,txtAddress,txtDistance,txtTime,txtStatus,txtPhone,txtPrice,txtTotalImg,txtTotalComent,txtTotalCheckin,txtTotalSave;
    ImageView imgPlace,imgVerified;
    Toolbar toolbar;
    GoogleMap mMap;
    SharedPreferences sharedPreferences;
    Location curLocation;
    Button btnCall,btnDirection,btnReport,btnShare,btnComment;
    BitmapDescriptor icon;
    RecyclerView recyclerViewComment;
    MapFragment mapFragment;
    String like;
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("places");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_detail);
        txtToolbarName=findViewById(R.id.txt_Toolbar_Name);
        txtName=findViewById(R.id.txt_Name_Place);
        txtAddress=findViewById(R.id.txt_Address);
        txtTime=findViewById(R.id.txt_Time);
        toolbar=findViewById(R.id.toolbarDetail);
        btnCall=findViewById(R.id.btn_Call);
        btnDirection=findViewById(R.id.btn_Direction);
        btnReport=findViewById(R.id.btn_Report);
        btnComment=findViewById(R.id.btn_Comment);
        btnShare=findViewById(R.id.btn_Share);
        txtDistance=findViewById(R.id.txt_Distance);
        txtStatus=findViewById(R.id.txt_Status);
        txtPhone=findViewById(R.id.txt_Phone);
        imgVerified=findViewById(R.id.img_Verified);
        txtPrice=findViewById(R.id.txt_Price);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        imgPlace=findViewById(R.id.img_Place);
        recyclerViewComment=findViewById(R.id.recyclerCommentDetail);
        txtType=findViewById(R.id.txt_Type);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        sharedPreferences=getSharedPreferences("location",Context.MODE_PRIVATE);
        curLocation=new Location("");
        curLocation.setLatitude(Double.parseDouble(sharedPreferences.getString("laititude","0")));
        curLocation.setLongitude(Double.parseDouble(sharedPreferences.getString("longitude","0")));



    }

    private void LoadData() {
        databaseReference.addChildEventListener(new ChildEventListener() {
            Bundle bundle=getIntent().getExtras();
            final String name =bundle.getString("name");


            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                String place=dataSnapshot.child("name").getValue().toString();
                String address=dataSnapshot.child("address").getValue().toString();
                double latitude= (double) dataSnapshot.child("latitude").getValue();
                double longitude= (double) dataSnapshot.child("longitude").getValue();
                LatLng latLng=new LatLng(latitude,longitude);
                String like=dataSnapshot.child("like").getValue().toString();
                String open=dataSnapshot.child("open").getValue().toString();
                String close=dataSnapshot.child("close").getValue().toString();
                String price=dataSnapshot.child("pirce").getValue().toString();
                String phone=dataSnapshot.child("phone").getValue().toString();
                String type=dataSnapshot.child("type").getValue().toString();
                boolean verified= (boolean) dataSnapshot.child("verified").getValue();
                String link=dataSnapshot.child("photo").getValue().toString();

                if (name.equals(dataSnapshot.child("name").getValue().toString())){
                    txtToolbarName.setText(place);
                    txtName.setText(place);
                    txtAddress.setText("Địa chỉ: "+address);
                    txtPrice.setText("Giá dịch vụ: "+price);
                    txtTime.setText("Time: "+open+"-"+close);
                    txtPhone.setText("Phone:"+phone);
                    txtType.setText("Loại: "+type);
                    if (verified==true){
                        imgVerified.setVisibility(View.VISIBLE);
                    }
                    Location location=new Location("");
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);
                    double distance= curLocation.distanceTo(location)/1000;
                    txtDistance.setText("Khoảng cách: "+String.format("%.1f",distance)+" km");



                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

                    String now = dateFormat.format(calendar.getTime());
                    try {
                        Date dateNow = dateFormat.parse(now);
                        Date dateOpen = dateFormat.parse(open);
                        Date dateClose = dateFormat.parse(close);

                        if(dateNow.after(dateOpen) && dateNow.before(dateClose)){
                            //gio mo cua
                            txtStatus.setText("Đang mở cửa");
                        }else{
                            //dong cua
                            txtStatus.setText("Đã đóng cửa");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    StorageReference storagePhoto= FirebaseStorage.getInstance().getReference().child("photo").child(link);
                    long ONE_MEGABYTE=1024*1024;
                    storagePhoto.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                            imgPlace.setImageBitmap(bitmap);
                        }
                    });
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
                        icon=BitmapDescriptorFactory.fromResource(R.drawable.health_medical);
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
                    mMap.addMarker(new MarkerOptions().icon(icon).position(latLng).flat(true).rotation(0)
                    );
                    CameraPosition position=CameraPosition.builder().target(latLng)
                            .zoom(14).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position),1500,null);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        btnCall.setOnClickListener(new View.OnClickListener() {
            Bundle bundle=getIntent().getExtras();
            final String name =bundle.getString("name");
            @Override
            public void onClick(View v) {
               databaseReference.addChildEventListener(new ChildEventListener() {
                   @Override
                   public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                       if (name.equals(dataSnapshot.child("name").getValue().toString())){
                           String phone=dataSnapshot.child("phone").getValue().toString();
                           Intent intent =new Intent(Intent.ACTION_DIAL);
                           if (phone.trim().isEmpty()){
                               intent.setData(Uri.parse("tel:0969352676"));
                               Toast.makeText(getApplicationContext(),"Số điện thoại bị lỗi,Vui lòng liên hệ Admin",Toast.LENGTH_SHORT).show();
                           }else {
                               intent.setData(Uri.parse("tel:"+phone));
                           }
                           startActivity(intent);
                       }
                   }

                   @Override
                   public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                   }

                   @Override
                   public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                   }

                   @Override
                   public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });
            }
        });
        btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=getIntent().getExtras();
                final String name =bundle.getString("name");
               databaseReference.addChildEventListener(new ChildEventListener() {
                   @Override
                   public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                       if (name.equals(dataSnapshot.child("name").getValue().toString())){
                           double lat = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
                           double lng= Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());
                           String direction="google.navigation:q="+lat+","+lng;
                           Uri uri=Uri.parse(direction);
                           Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                           intent.setPackage("com.google.android.apps.maps");
                           startActivity(intent);
                       }
                   }

                   @Override
                   public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                   }

                   @Override
                   public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                   }

                   @Override
                   public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });
            }
        });
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=getIntent().getExtras();
                final String name =bundle.getString("name");
              databaseReference.addChildEventListener(new ChildEventListener() {
                  @Override
                  public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                      if (name.equals(dataSnapshot.child("name").getValue().toString())) {
                          String placeID = dataSnapshot.getKey();
                          Intent intent = new Intent(Intent.ACTION_SEND);
                          intent.setType("message/rfc822");
                          intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"truccongle@gmail.com"});
                          intent.putExtra(Intent.EXTRA_SUBJECT, "Địa điểm sai thông tin - [" + placeID + "]");
                          intent.putExtra(Intent.EXTRA_TEXT, "Xin chào,\n\nĐịa điểm " + placeID + " có thông tin không chính xác so với thực tế.\nHi vọng các bạn kiểm tra lại.\n\nXin cảm ơn.");
                          intent.setPackage("com.google.android.gm");
                          if (intent.resolveActivity(getPackageManager()) != null)
                              startActivity(intent);
                          else
                              Toast.makeText(MapDetailActivity.this, "Gmail App is not installed", Toast.LENGTH_SHORT).show();
                      }
                  }

                  @Override
                  public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                  }

                  @Override
                  public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                  }

                  @Override
                  public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError databaseError) {

                  }
              });
            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=getIntent().getExtras();
                final String name =bundle.getString("name");
                databaseReference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (name.equals(dataSnapshot.child("name").getValue().toString())) {
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            String place = dataSnapshot.child("name").getValue().toString();
                            String address = dataSnapshot.child("address").getValue().toString();
                            String phone = dataSnapshot.child("phone").getValue().toString();
                            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Ứng dụng Near me");
                            intent.putExtra(android.content.Intent.EXTRA_TEXT, "Địa điểm:" + name + "\n\nĐịa Chỉ:" + address + "\n\nSố điện thoại:" + phone);

                            startActivity(Intent.createChooser(intent, "Share via"));
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        LoadData();


    }


}

