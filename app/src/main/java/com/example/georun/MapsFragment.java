package com.example.georun;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.ParseException;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    public interface OnMapLongClickListener {
        void onMapLongClicked(LatLng latLng);
    }
    private OnMapLongClickListener longClickListener;

    public void setOnMapLongClickListener(OnMapLongClickListener listener) {
        this.longClickListener = listener;
    }

    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;

    public MapsFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            return;
        }

        map.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 16f));
            }
        });

        loadMarkersFromDB();

        map.setOnMapLongClickListener(latLng -> {
            if (longClickListener != null) longClickListener.onMapLongClicked(latLng);
        });
    }


    private void loadMarkersFromDB() {
        WorkoutDbHelper db = new WorkoutDbHelper(getContext());
        List<workoutModel> workouts = db.getAllWorkoutLocations();

        java.text.DateFormat outFmt = new java.text.SimpleDateFormat("dd MMM", java.util.Locale.getDefault());
        java.text.SimpleDateFormat inputFmt = new java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault());

        for (workoutModel w : workouts) {
            if (w.getType() == null || w.getType().trim().isEmpty()) continue;
            if (w.getLat() == 0 && w.getLng() == 0) continue;

            LatLng p = new LatLng(w.getLat(), w.getLng());
            String title = w.getType();

            try {
                String rawDate = w.getDate();
                if (rawDate != null && !rawDate.isEmpty()) {
                    title += " on " + outFmt.format(inputFmt.parse(rawDate));
                }
            } catch (Exception e) {
                Log.e("MapsFragment", "Date parse failed for: " + w.getDate(), e);
            }

            map.addMarker(new MarkerOptions()
                    .position(p)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title(title));
        }


        if (!workouts.isEmpty()) {
            LatLng first = new LatLng(workouts.get(0).getLat(), workouts.get(0).getLng());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(first, 12));
        }
    }

    private String formatDate(String fullDate) {
        try {
            // Assuming original format is: "dd MMM yyyy, hh:mm a"
            java.text.SimpleDateFormat input = new java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault());
            java.text.DateFormat output = new java.text.SimpleDateFormat("dd MMM", java.util.Locale.getDefault());
            java.util.Date date = input.parse(fullDate);
            return output.format(date);
        } catch (Exception e) {
            return fullDate;  // fallback
        }
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001 &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                onMapReady(map); // Reinitialize with permission
            }
        }
    }
    public void addMarkerAt(LatLng latLng, workoutModel workout) {
        if (map != null) {
            String title = workout.getType();
            if (workout.getDate() != null && !workout.getDate().isEmpty()) {
                title += " on " + workout.getDate();
            }

            map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title(title));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    }


    private BitmapDescriptor getBitmapFromDrawable(int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(requireContext(), drawableId);
        if (drawable == null) return BitmapDescriptorFactory.defaultMarker();

        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void refreshMarkers() {
        if (map != null) {
            map.clear();  // Remove old markers
            loadMarkersFromDB();
        }
    }



//    private void loadWorkoutMarkers() {
//        WorkoutDbHelper dbHelper = new WorkoutDbHelper(requireContext());
//        List<workoutModel> locations = dbHelper.getAllWorkoutLocations();
//
//        for (workoutModel latLng : locations) {
//            map.addMarker(new MarkerOptions()
//                    .position()
//                    .icon(getBitmapFromDrawable(R.drawable.baseline_location_pin_24))
//                    .title("Workout Location"));
//        }
//    }



}