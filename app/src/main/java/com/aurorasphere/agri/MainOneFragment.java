package com.aurorasphere.agri;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainOneFragment extends Fragment {

    private static final String WEATHER_API_KEY = ".....use your api";

    TextView humidity_tx, temp_tx, soil_moisture_tx, windText, descriptionText,
            cropName_tx, start_end_date_tx, city_tx, humidity_reTx, moisture_reTx, temp_reTx;

    String cityName_search, humidity_re, moisture_re, tempMx_re, tempMn_re;
    ImageView weatherIcon;
    CardView cardView6;

    private DatabaseReference databaseReference;
    private ValueEventListener sensorListener;

    public MainOneFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_one, container, false);

        // Initialize TextViews and ImageView
        humidity_tx = view.findViewById(R.id.humidity_tx);
        temp_tx = view.findViewById(R.id.temp_tx);
        soil_moisture_tx = view.findViewById(R.id.soil_moisture_tx);
        windText = view.findViewById(R.id.windText);
        descriptionText = view.findViewById(R.id.descriptionText);
        weatherIcon = view.findViewById(R.id.weatherIcon);
        cropName_tx = view.findViewById(R.id.cropName_tx);
        start_end_date_tx = view.findViewById(R.id.start_end_date_tx);
        city_tx = view.findViewById(R.id.city_tx);
        humidity_reTx = view.findViewById(R.id.humidity_reTx);
        moisture_reTx = view.findViewById(R.id.moisture_reTx);
        temp_reTx = view.findViewById(R.id.temp_reTx);
        cardView6 = view.findViewById(R.id.cardView6);

        // Load saved crop selection
        CropSelectionManager.loadSelection(requireContext());
        CropInfo info = CropSelectionManager.getSelectedCropInfo();
        if (info != null) {
            String cropName = CropSelectionManager.getSelectedCrop();
            String cityName = CropSelectionManager.getSelectedCity();
            humidity_re = String.valueOf(info.select_humidity);
            moisture_re = String.valueOf(info.select_moisture);
            tempMx_re = String.valueOf(info.select_tempMax);
            tempMn_re = String.valueOf(info.select_tempMin);
            String sowing = info.sowingWindow;
            String harvest = info.harvestWindow;
            windText.setText("No selection");

            cardView6.setVisibility(View.VISIBLE);
            cropName_tx.setText(cropName);
            city_tx.setText(cityName);
            cityName_search = cityName;
            humidity_reTx.setText(humidity_re);
            moisture_reTx.setText(moisture_re);
            temp_reTx.setText(tempMx_re + "°C / " + tempMn_re + "°C");
            start_end_date_tx.setText(sowing + " to " + harvest);
        } else {
            cardView6.setVisibility(View.GONE);
            cropName_tx.setText("No selection");
            city_tx.setText("");
            descriptionText.setText("");
            cityName_search = "Gwalior";
            start_end_date_tx.setText("");
        }

        // Setup Firebase reference
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("agri_flow")
                .child("AbC18s");

        // Define sensor data listener
        sensorListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long humidity = snapshot.child("humidity").getValue(Long.class);
                Long temperature = snapshot.child("temperature").getValue(Long.class);
                Long soilMoisture = snapshot.child("soil_moisture").getValue(Long.class);

                humidity_tx.setText(humidity + "%");
                temp_tx.setText(temperature + "°C");
                soil_moisture_tx.setText(soilMoisture + "%");

                String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                if (getContext() != null) {
                    SensorDataDBHelper dbHelper = new SensorDataDBHelper(getContext());
                    dbHelper.insertSensorData(
                            currentTime,
                            temperature != null ? temperature.intValue() : 0,
                            humidity != null ? humidity.intValue() : 0,
                            soilMoisture != null ? soilMoisture.intValue() : 0
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Optional: handle database error
            }
        };

        FetchWeatherData(cityName_search);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (databaseReference != null && sensorListener != null) {
            databaseReference.addValueEventListener(sensorListener);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (databaseReference != null && sensorListener != null) {
            databaseReference.removeEventListener(sensorListener);
        }
    }

    private void FetchWeatherData(String cityName) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + WEATHER_API_KEY + "&units=metric";
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            try {
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                requireActivity().runOnUiThread(() -> updateUi(result));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void updateUi(String result) {
        if (result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject main = jsonObject.getJSONObject("main");
                double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                String iconCode = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");
                String resourceName = "ic_" + iconCode;
                int resourceId = getResources().getIdentifier(resourceName, "drawable", getActivity().getPackageName());
                weatherIcon.setImageResource(resourceId);
                windText.setText(windSpeed + " km/h");
                descriptionText.setText(description);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
