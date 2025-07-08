package com.aurorasphere.agri;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainThreeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainThreeFragment extends Fragment {

    private RecyclerView sensorDataRecyclerView;
    private SensorDataAdapter adapter;
    private SensorDataDBHelper dbHelper;

    public MainThreeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_three, container, false);

        sensorDataRecyclerView = view.findViewById(R.id.sensorDataRecyclerView);
        sensorDataRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new SensorDataDBHelper(getContext());
        List<SensorDataDBHelper.SensorData> sensorList = dbHelper.getAllSensorData();

        adapter = new SensorDataAdapter(sensorList);
        sensorDataRecyclerView.setAdapter(adapter);

        return view;
    }
}