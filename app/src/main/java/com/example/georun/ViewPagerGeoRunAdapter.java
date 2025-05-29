package com.example.georun;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerGeoRunAdapter extends FragmentPagerAdapter {
    public ViewPagerGeoRunAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        if(i == 0){
            return new MapsFragment();
        }
        else {
            return  new listFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0){
            return "Map";
        }
        else{
            return "My Workouts";
        }
    }
}
