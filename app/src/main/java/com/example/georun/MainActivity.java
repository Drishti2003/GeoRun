package com.example.georun;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowInsetsController;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements
        BottomSheetDialog.BottomSheetListener,
        MapsFragment.OnMapLongClickListener {

    ViewPager viewPager;
    TabLayout tabLayout;
    LatLng selectedLatLng;
    private MapsFragment mapsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#2C2C2E"));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().getInsetsController().setSystemBarsAppearance(0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(0);
        }

        viewPager = findViewById(R.id.ViewPager);
        tabLayout = findViewById(R.id.Tab);

        ViewPagerGeoRunAdapter adapter = new ViewPagerGeoRunAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.post(() -> {
            mapsFragment = (MapsFragment) getSupportFragmentManager()
                    .findFragmentByTag("android:switcher:" + R.id.ViewPager + ":0");
            if (mapsFragment != null) {
                mapsFragment.setOnMapLongClickListener(MainActivity.this);
            } else {
                Log.e("MainActivity", "MapsFragment not found!");
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                Fragment currentFragment = adapter.getItem(position);
                if (currentFragment instanceof MapsFragment) {
                    ((MapsFragment) currentFragment).setOnMapLongClickListener(MainActivity.this);
                }
            }

            @Override public void onPageScrollStateChanged(int state) {}
        });
    }

    @Override
    public void onMapLongClicked(LatLng latLng) {
        selectedLatLng = latLng;
    }

    @Override
    public void onDataEntered(String workoutType, String distance, String duration, String cadence) {
        if (selectedLatLng == null) {
            Toast.makeText(this, "Location not selected!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (distance.isEmpty() || duration.isEmpty() || cadence.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mapsFragment.addMarkerAt(selectedLatLng);

        Log.d("DataReceived", workoutType + ", " + distance + ", " + duration + ", " + cadence);
    }

}
