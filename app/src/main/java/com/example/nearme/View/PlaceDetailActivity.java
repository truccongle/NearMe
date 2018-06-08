package com.example.nearme.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PlaceDetailActivity extends AppCompatActivity implements OnMapReadyCallback,View.OnClickListener {
        TextView txtToolbarName,txtName,txtType,txtAddress,txtDistance,txtTime,txtStatus,txtPhone,txtPrice,txtTotalImg,txtTotalComent,txtTotalCheckin,txtTotalSave;
        ImageView imgPlace,imgVerified;
        Toolbar toolbar;
        GoogleMap mMap;
        Button btnCall,btnDirection,btnReport,btnShare,btnComment;
    SharedPreferences sharedPreferences;
    Location curLocation;
    BitmapDescriptor icon;
        AdapterComment adapterComment;
    RecyclerView recyclerViewComment;
    MapFragment mapFragment;
    PlaceModel placeModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        placeModel=getIntent().getParcelableExtra("place");

        txtToolbarName=findViewById(R.id.txt_Toolbar_Name);
        txtName=findViewById(R.id.txt_Name_Place);
        txtAddress=findViewById(R.id.txt_Address);
        txtTime=findViewById(R.id.txt_Time);
        btnCall=findViewById(R.id.btn_Call);
        btnDirection=findViewById(R.id.btn_Direction);
        btnReport=findViewById(R.id.btn_Report);
        btnComment=findViewById(R.id.btn_Comment);
        btnShare=findViewById(R.id.btn_Share);
        imgVerified=findViewById(R.id.img_Verified);
        toolbar=findViewById(R.id.toolbarDetail);
        txtStatus=findViewById(R.id.txt_Status);
        txtPhone=findViewById(R.id.txt_Phone);
        txtPrice=findViewById(R.id.txt_Price);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        txtDistance=findViewById(R.id.txt_Distance);
        imgPlace=findViewById(R.id.img_Place);
        recyclerViewComment=findViewById(R.id.recyclerCommentDetail);
        txtType=findViewById(R.id.txt_Type);
        sharedPreferences=getSharedPreferences("location", Context.MODE_PRIVATE);
        curLocation=new Location("");
        curLocation.setLatitude(Double.parseDouble(sharedPreferences.getString("laititude","0")));
        curLocation.setLongitude(Double.parseDouble(sharedPreferences.getString("longitude","0")));
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        btnShare.setOnClickListener(this);
        btnCall.setOnClickListener(this);
        btnComment.setOnClickListener(this);
        btnReport.setOnClickListener(this);
        btnDirection.setOnClickListener(this);

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        txtToolbarName.setText(placeModel.getName());
        txtName.setText(placeModel.getName());
        txtAddress.setText("Địa chỉ: "+placeModel.getAddress());
        txtPrice.setText("Giá dịch vụ: "+placeModel.getPirce()+" VNĐ");
        txtTime.setText("Time: "+placeModel.getOpen()+"-"+placeModel.getClose());
        txtPhone.setText("Phone:"+placeModel.getPhone());
        txtType.setText("Loại: "+placeModel.getType());
        if (placeModel.isVerified()){
            imgVerified.setVisibility(View.VISIBLE);
        }
        Location location=new Location("");
        location.setLatitude(placeModel.getLatitude());
        location.setLongitude(placeModel.getLongitude());
        double distance= curLocation.distanceTo(location)/1000;
        Log.d("distance",curLocation + "" +location);
        txtDistance.setText("Khoảng cách: "+String.format("%.1f",distance)+" km");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

        String now = dateFormat.format(calendar.getTime());
        String open = placeModel.getOpen();
        String close = placeModel.getClose();
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
        StorageReference storagePhoto= FirebaseStorage.getInstance().getReference().child("photo").child(placeModel.getPhoto());
        long ONE_MEGABYTE=1024*1024;
        storagePhoto.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                imgPlace.setImageBitmap(bitmap);
            }
        });

        //Load danh sach binh luan cua quan
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewComment.setLayoutManager(layoutManager);
        adapterComment = new AdapterComment(this,R.layout.custom_comment,placeModel.getCommentModelList());
        recyclerViewComment.setAdapter(adapterComment);
        adapterComment.notifyDataSetChanged();

        NestedScrollView nestedScrollViewDetail = (NestedScrollView) findViewById(R.id.nestScrollView_Detail);
        nestedScrollViewDetail.smoothScrollTo(0,0);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        String type=placeModel.getType();
        mMap=googleMap;
        boolean a=type.equals("COFFEE");
        Log.d("ck",a+"");
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
        LatLng latLng=new LatLng(placeModel.getLatitude(),placeModel.getLongitude());
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.position(latLng).icon(icon).flat(true).rotation(0);
        CameraPosition position=CameraPosition.builder().target(latLng)
                .zoom(14).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position),1500,null);

        mMap.addMarker(markerOptions);

    }
    private  void sharePlace(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String name=placeModel.getName();
        String address=placeModel.getAddress();
        String phone=placeModel.getPhone();
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Ứng dụng Near me");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "Địa điểm:" + name + "\nĐịa Chỉ:" + address + "\nSố điện thoại:" + phone);

        startActivity(Intent.createChooser(intent, "Share via"));
    }
    private void direction(){
        double lat =placeModel.getLatitude();
        double lng=placeModel.getLongitude();
        String direction="google.navigation:q="+lat+","+lng;
        Uri uri=Uri.parse(direction);
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }
private  void call(){
        String phone=placeModel.getPhone();
    Intent intent =new Intent(Intent.ACTION_DIAL);
    if (phone.trim().isEmpty()){
        intent.setData(Uri.parse("tel:0969352676"));
        Toast.makeText(getApplicationContext(),"Số điện thoại bị lỗi,Vui lòng liên hệ Admin",Toast.LENGTH_SHORT).show();
    }else {
        intent.setData(Uri.parse("tel:"+phone));
    }
    startActivity(intent);
}
private void report(){
        String placeID=placeModel.getPlaceID();
    Intent intent = new Intent (Intent.ACTION_SEND);
    intent.setType("message/rfc822");
    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"truccongle@gmail.com"});
    intent.putExtra(Intent.EXTRA_SUBJECT, "Địa điểm sai thông tin - ["+placeID+"]");
    intent.putExtra(Intent.EXTRA_TEXT,"Xin chào,\n\nĐịa điểm "+placeID+" có thông tin không chính xác so với thực tế.\nHi vọng các bạn kiểm tra lại.\n\nXin cảm ơn.");
    intent.setPackage("com.google.android.gm");
    if (intent.resolveActivity(getPackageManager())!=null)
        startActivity(intent);
    else
        Toast.makeText(this,"Gmail App is not installed",Toast.LENGTH_SHORT).show();
}
    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.btn_Call:
                call();
                break;
            case R.id.btn_Share:
                sharePlace();
                break;
            case R.id.btn_Comment:
                Intent intent = new Intent(this,CommentActivity.class);
                intent.putExtra("placeID",placeModel.getPlaceID());
                Log.d("placeid",placeModel.getPlaceID()+"");
                intent.putExtra("name",placeModel.getName());
                intent.putExtra("address",placeModel.getAddress());
                startActivity(intent);
                break;
            case R.id.btn_Report:
                report();
                break;
            case R.id.btn_Direction:
                direction();
                break;
        }

    }
}
