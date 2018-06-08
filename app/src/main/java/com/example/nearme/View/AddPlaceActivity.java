package com.example.nearme.View;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.nearme.Model.PlaceModel;
import com.example.nearme.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddPlaceActivity extends AppCompatActivity implements  View.OnClickListener,AdapterView.OnItemSelectedListener {
    EditText edtName, edtAddress, edtPhone, edtType, edtMin, edtMax;
    ImageView imgAddPhoto;
    TextView txtLaLng;
    Button btnAddPlace, btnClose, btnOpen;
    RadioGroup rdgStatus;
    String close, open, placeID,photo;
    RadioButton rdVerified, rdNotVerified;
   final int REQUEST_CODE_IMAGE = 1;
    SharedPreferences sharedPreferences;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);
        edtName = findViewById(R.id.edt_Name);
        edtAddress = findViewById(R.id.edt_Address);
        edtPhone = findViewById(R.id.edt_Phone);
        edtType = findViewById(R.id.edt_Type);
        edtMin = findViewById(R.id.edt_Min_price);
        edtMax = findViewById(R.id.edt_Max_price);
        imgAddPhoto = findViewById(R.id.img_Add_Photo);
        txtLaLng = findViewById(R.id.txt_Lalng);
        btnAddPlace = findViewById(R.id.btn_Add_Place);
        btnClose = findViewById(R.id.btn_Close);
        btnOpen = findViewById(R.id.btn_Open);
        rdgStatus = findViewById(R.id.rdg_Status);
        rdVerified = findViewById(R.id.rd_Verified);
        rdNotVerified = findViewById(R.id.rd_Not_Verified);
        firebaseStorage=FirebaseStorage.getInstance();
        sharedPreferences = getSharedPreferences("location", Context.MODE_PRIVATE);
        String lat = sharedPreferences.getString("latitude", "0");
        String lng = sharedPreferences.getString("longitude", "0");

        txtLaLng.setText(lat + " - " + lng);



        btnClose.setOnClickListener(this);
        btnOpen.setOnClickListener(this);

        imgAddPhoto.setOnClickListener(this);
        btnAddPlace.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_IMAGE:
                if (RESULT_OK == resultCode) {
                    Uri uri = data.getData();
                    imgAddPhoto.setImageURI(uri);
                    photo= String.valueOf(uri);

                }
                break;

        }

    }

    @Override
    public void onClick(final View v) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        switch (v.getId()) {
            case R.id.btn_Close:

                TimePickerDialog timePickerDialog = new TimePickerDialog(AddPlaceActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        close = hourOfDay + ":" + minute;
                        ((Button) v).setText(close);
                    }
                }, hour, minute, true);

                timePickerDialog.show();
                break;

            case R.id.btn_Open:

                TimePickerDialog moCuaTimePickerDialog = new TimePickerDialog(AddPlaceActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        open = hourOfDay + ":" + minute;
                        ((Button) v).setText(open);

                    }
                }, hour, minute, true);

                moCuaTimePickerDialog.show();
                break;

            case R.id.img_Add_Photo:
                ChooseImageFromGallary(REQUEST_CODE_IMAGE);
                break;

            case R.id.btn_Add_Place:
                AddPlace();
                Toast.makeText(getApplicationContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void AddPlace() {
        String namePlace = edtName.getText().toString();
        String maxPrice = edtMax.getText().toString();
        String minPrice = edtMin.getText().toString();
        String type = edtType.getText().toString();
        String price = minPrice + " - " + maxPrice;
        String phone = edtPhone.getText().toString();
        double latitude = Double.parseDouble(sharedPreferences.getString("latitude", "0"));
        double longitude = Double.parseDouble(sharedPreferences.getString("longitude", "0"));

        String address = edtAddress.getText().toString();

        int radioSelected = rdgStatus.getCheckedRadioButtonId();
        boolean verified = false;
        if (radioSelected == R.id.rd_Verified) {
            verified = true;
        } else {
            verified = false;
        }
        DatabaseReference nodeRoot = FirebaseDatabase.getInstance().getReference();
        DatabaseReference nodePlace = nodeRoot.child("places");
        placeID = nodePlace.push().getKey();

        PlaceModel placeModel = new PlaceModel();
        placeModel.setName(namePlace);
        placeModel.setOpen(open);
        placeModel.setClose(close);
        placeModel.setPirce (price);
        placeModel.setAddress(address);
        placeModel.setType(type);
        placeModel.setVerified(verified);
        placeModel.setLatitude(latitude);
        placeModel.setLongitude(longitude);
        placeModel.setPhone(phone);
        placeModel.setLike(0);

        nodePlace.child(placeID).setValue(placeModel).addOnSuccessListener(new OnSuccessListener<Void>() {

            @Override
            public void onSuccess(Void aVoid) {

            }
        });

        long time = Calendar.getInstance().getTimeInMillis();
        String photoName = String.valueOf(time)+".jpg";
            nodeRoot.child("places").child(placeID).child("photo").setValue(photoName);
            imgAddPhoto.setDrawingCacheEnabled(true);
            imgAddPhoto.buildDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = imgAddPhoto.getDrawingCache();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            FirebaseStorage.getInstance().getReference().child("photo/"+photoName).putBytes(data);
            Toast.makeText(this,"Ảnh đã upload thành công",Toast.LENGTH_SHORT).show();


    }


    private void ChooseImageFromGallary(int requestCode){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Chọn hình..."),requestCode);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
