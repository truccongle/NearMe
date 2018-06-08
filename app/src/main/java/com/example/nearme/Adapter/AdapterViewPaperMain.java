package com.example.nearme.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.nearme.Model.PlaceModel;
import com.example.nearme.View.fragment.MapFragment;
import com.example.nearme.View.fragment.NearmeFragment;

public class AdapterViewPaperMain  extends FragmentStatePagerAdapter {
    NearmeFragment nearmeFragment;
    MapFragment mapFragment;

    public AdapterViewPaperMain(FragmentManager fm) {
        super(fm);
        nearmeFragment = new NearmeFragment();
        mapFragment = new MapFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 :
                return nearmeFragment;

            case 1:
                return mapFragment;

            default: return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
