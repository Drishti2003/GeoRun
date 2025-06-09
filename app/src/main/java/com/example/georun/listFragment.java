package com.example.georun;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class listFragment extends Fragment {

    private RecyclerView recyclerView;
    private workoutAdapter adapter;
    private List<workoutModel> workoutList;
    private WorkoutDbHelper dbHelper;

    public listFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewWorkouts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new WorkoutDbHelper(getContext());
        workoutList = dbHelper.getAllWorkouts();  // Fetch from DB
        adapter = new workoutAdapter(workoutList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWorkoutData(); // reload from DB
    }

    private void loadWorkoutData() {
        WorkoutDbHelper dbHelper = new WorkoutDbHelper(requireContext());
        List<workoutModel> updatedList = dbHelper.getAllWorkouts();  // Replace with your actual method
        adapter.updateWorkouts(updatedList);

    }

    public void refreshWorkoutList() {
        if (adapter != null && dbHelper != null) {
            List<workoutModel> updatedList = dbHelper.getAllWorkouts();
            adapter.updateWorkouts(updatedList);
        }
    }


}
