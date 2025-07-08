package com.aurorasphere.agri;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFourFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFourFragment extends Fragment {

    Spinner spinner_state, spinner_city, spinner_crop;
    CardView btn_set_selection;
    TextView sowing_tx, harvest_tx, growing_tx;

    public MainFourFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_four, container, false);

        spinner_state = view.findViewById(R.id.spinner_state);
        spinner_city = view.findViewById(R.id.spinner_city);
        spinner_crop = view.findViewById(R.id.spinner_crop);
        btn_set_selection = view.findViewById(R.id.btn_set_selection);
        sowing_tx = view.findViewById(R.id.sowing_tx);
        harvest_tx = view.findViewById(R.id.harvest_tx);
        growing_tx = view.findViewById(R.id.growing_tx);

        setupSpinners();
        setupButton();

        return view;
    }

    private void setupSpinners() {
        String[] states = CropSelectionManager.getAllStates();
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, states);
        spinner_state.setAdapter(stateAdapter);

        // Listener for state spinner
        spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedState = states[position];
                updateCitySpinner(selectedState);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Listener for city spinner
        spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedState = spinner_state.getSelectedItem().toString();
                String selectedCity = spinner_city.getSelectedItem().toString();
                updateCropSpinner(selectedState, selectedCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Load saved selection
        CropSelectionManager.loadSelection(requireContext());

        String savedState = CropSelectionManager.getSelectedState();
        String savedCity = CropSelectionManager.getSelectedCity();
        String savedCrop = CropSelectionManager.getSelectedCrop();

        if (savedState != null) {
            int stateIndex = getIndex(spinner_state, savedState);
            spinner_state.setSelection(stateIndex);
        }

        if (savedCity != null) {
            updateCitySpinner(savedState);
            spinner_city.post(() -> {
                int cityIndex = getIndex(spinner_city, savedCity);
                spinner_city.setSelection(cityIndex);
            });
        }

        if (savedCrop != null) {
            updateCropSpinner(savedState, savedCity);
            spinner_crop.post(() -> {
                int cropIndex = getIndex(spinner_crop, savedCrop);
                spinner_crop.setSelection(cropIndex);
            });
        }
    }

    private void updateCitySpinner(String state) {
        String[] cities = CropSelectionManager.getCitiesByState(state);
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, cities);
        spinner_city.setAdapter(cityAdapter);

        if (cities.length > 0) {
            updateCropSpinner(state, cities[0]); // Default to first city
        }
    }

    private void updateCropSpinner(String state, String city) {
        String[] crops = CropSelectionManager.getCropsByCity(state, city);
        ArrayAdapter<String> cropAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, crops);
        spinner_crop.setAdapter(cropAdapter);
    }

    private void setupButton() {
        btn_set_selection.setOnClickListener(v -> {
            String selectedState = spinner_state.getSelectedItem().toString();
            String selectedCity = spinner_city.getSelectedItem().toString();
            String selectedCrop = spinner_crop.getSelectedItem().toString();

            // Save the selection permanently
            CropSelectionManager.setSelection(requireContext(), selectedState, selectedCity, selectedCrop);

            // Get the crop info
            CropInfo info = CropSelectionManager.getSelectedCropInfo();

            if (info != null) {
                sowing_tx.setText("Sowing: " + info.sowingWindow);
                harvest_tx.setText("Harvest: " + info.harvestWindow);
                growing_tx.setText("Growing Period: " + info.growingPeriodDays + " days");

                Toast.makeText(requireContext(), "Selection saved & info shown", Toast.LENGTH_SHORT).show();
            } else {
                sowing_tx.setText("No info available");
                harvest_tx.setText("");
                growing_tx.setText("");
                Toast.makeText(requireContext(), "No data for selected combination", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;
    }
}
