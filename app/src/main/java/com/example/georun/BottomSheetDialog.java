package com.example.georun;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    ImageView closeButton;
    private  LatLng selectedLatLng;

    private void showUnitIfNotEmpty(EditText editText, final TextView unitView) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.toString().trim().isEmpty()) {
                    unitView.setVisibility(View.GONE);
                } else {
                    unitView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }


    @Override
    public int getTheme() {
        return R.style.CustomBottomSheetDialogTheme;
    }

    @Override
    public void onStart() {
        super.onStart();

        FrameLayout bottomSheet = getDialog().findViewById(
                com.google.android.material.R.id.design_bottom_sheet
        );

        if (bottomSheet != null) {
            BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setShouldRemoveExpandedCorners(false); // 🔑 Keeps corners when expanded
        }
    }

    public void setSelectedLatLng(LatLng latLng) {
        selectedLatLng = latLng;
    }


    public interface BottomSheetListener {
        void onDataEntered(String workoutType, float distance, int duration, int cadence);
    }

    private BottomSheetListener listener;

    private AppCompatButton doneButton;
    private EditText etDistance, etDuration, etCadence;
    private TextView distanceUnit,cadenceUnit,durationUnit;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof BottomSheetListener) {
            listener = (BottomSheetListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement BottomSheetListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        Spinner spinner = view.findViewById(R.id.workout_spinner);
        doneButton = view.findViewById(R.id.btnDone);
        etDistance = view.findViewById(R.id.distance);
        etDuration = view.findViewById(R.id.duration);
        etCadence = view.findViewById(R.id.cadence);
        closeButton = view.findViewById(R.id.btnClose);

        distanceUnit = view.findViewById(R.id.distance_unit);
        showUnitIfNotEmpty(etDistance, distanceUnit);

        cadenceUnit = view.findViewById(R.id.cadence_unit);
        showUnitIfNotEmpty(etCadence,cadenceUnit);

        durationUnit = view.findViewById(R.id.duration_unit);
        showUnitIfNotEmpty(etDuration,durationUnit);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.workout_array,
                R.layout.spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        doneButton.setOnClickListener(v -> {
            String type = spinner.getSelectedItem().toString();
            String distanceStr = etDistance.getText().toString().trim();
            String durationStr = etDuration.getText().toString().trim();
            String cadenceStr = etCadence.getText().toString().trim();

            // Validate input
            if (distanceStr.isEmpty() || durationStr.isEmpty() || cadenceStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                float distance = Float.parseFloat(distanceStr);
                int duration = Integer.parseInt(durationStr);
                int cadence = Integer.parseInt(cadenceStr);

                // ✂️ REMOVE this block:
                // workoutModel workout = new workoutModel(...);
                // WorkoutDbHelper dbHelper = new WorkoutDbHelper(getContext());
                // dbHelper.insertWorkout(workout);

                Toast.makeText(getContext(), "Workout saved!", Toast.LENGTH_SHORT).show();

                if (listener != null) {
                    listener.onDataEntered(type, distance, duration, cadence);
                }

                dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid input format", Toast.LENGTH_SHORT).show();
            }

        });


        return view;
    }
}
