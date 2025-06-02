package com.example.georun;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    public interface BottomSheetListener {
        void onDataEntered(String workoutType, String distance, String duration, String cadence);
    }

    private BottomSheetListener listener;

    private AppCompatButton doneButton;
    private EditText etDistance, etDuration, etCadence;

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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.workout_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        doneButton.setOnClickListener(v -> {
            String type = spinner.getSelectedItem().toString();
            String distance = etDistance.getText().toString();
            String duration = etDuration.getText().toString();
            String cadence = etCadence.getText().toString();

            if (listener != null) {
                listener.onDataEntered(type, distance, duration, cadence);
            }

            dismiss(); // Optional: close the bottom sheet after data entry
        });

        return view;
    }
}
