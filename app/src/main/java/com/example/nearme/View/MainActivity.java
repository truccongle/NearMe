package com.example.nearme.View;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.nearme.Adapter.AdapterViewPaperMain;
import com.example.nearme.Model.UserModel;
import com.example.nearme.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class  MainActivity extends  AppCompatActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {

    ViewPager viewPagerMain;
    ImageView imgAddSetting;
    RadioButton rdNearme,rdMap;
    RadioGroup groupChoose;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPagerMain =  findViewById(R.id.viewpager_place);
        rdNearme = findViewById(R.id.rd_Nearme);
        rdMap =  findViewById(R.id.rd_Map);
        imgAddSetting=findViewById(R.id.img_Add_Setting);
        groupChoose =  findViewById(R.id.group_choose);


        AdapterViewPaperMain adapterViewPaperMain = new AdapterViewPaperMain(getSupportFragmentManager());
        viewPagerMain.setAdapter(adapterViewPaperMain);

        viewPagerMain.addOnPageChangeListener(this);
        groupChoose.setOnCheckedChangeListener(this);
        imgAddSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),AddPlaceActivity.class);
                startActivity(intent );
            }
        });

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position){
            case 0:
                rdNearme.setChecked(true);
                break;

            case 1:
                rdMap.setChecked(true);
                break;

        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId){
            case R.id.rd_Nearme:
                viewPagerMain.setCurrentItem(0);
                break;

            case R.id.rd_Map:
                viewPagerMain.setCurrentItem(1);
                break;
        }
    }
}
