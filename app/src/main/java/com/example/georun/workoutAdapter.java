package com.example.georun;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class workoutAdapter extends RecyclerView.Adapter<workoutAdapter.WorkoutViewHolder> {

    private List<workoutModel> workoutList;

    public workoutAdapter(List<workoutModel> workoutList) {
        this.workoutList = workoutList;
    }

    // Add this method to update data and refresh UI
    public void updateWorkouts(List<workoutModel> newWorkoutList) {
        workoutList.clear();
        workoutList.addAll(newWorkoutList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        workoutModel workout = workoutList.get(position);
        String title = workout.getType() + " on " + workout.getDate();
        String paceOrSpeed = "Running".equalsIgnoreCase(workout.getType())
                ? workout.getResult() + " MIN/KM"
                : workout.getResult() + " KM/H";

        holder.title.setText(title);
        holder.distance.setText(workout.getDistance() + " KM");
        holder.duration.setText(workout.getDuration() + " MIN");
        holder.paceOrSpeed.setText(paceOrSpeed);
        holder.cadence.setText(workout.getCadence() + " SPM");
    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView title, distance, duration, paceOrSpeed, cadence;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.workoutTitle);
            distance = itemView.findViewById(R.id.distance);
            duration = itemView.findViewById(R.id.duration);
            paceOrSpeed = itemView.findViewById(R.id.speedOrPace);
            cadence = itemView.findViewById(R.id.cadence);
        }
    }

    public void setWorkoutList(List<workoutModel> newList) {
        workoutList.clear();           // clear existing items
        workoutList.addAll(newList);  // add all updated items
    }


}
