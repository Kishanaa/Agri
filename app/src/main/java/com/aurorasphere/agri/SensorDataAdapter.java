package com.aurorasphere.agri;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SensorDataAdapter extends RecyclerView.Adapter<SensorDataAdapter.SensorViewHolder> {

    private List<SensorDataDBHelper.SensorData> sensorList;

    public SensorDataAdapter(List<SensorDataDBHelper.SensorData> sensorList) {
        this.sensorList = sensorList;
    }

    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sensor_data, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorViewHolder holder, int position) {
        SensorDataDBHelper.SensorData data = sensorList.get(position);
        holder.tvDateTime.setText(data.datetime);
        holder.tvTemperature.setText("Temperature: " + data.temperature + "°C");
        holder.tvHumidity.setText("Humidity: " + data.humidity + "%");
        holder.tvSoilMoisture.setText("Soil Moisture: " + data.soilMoisture + "%");
    }

    @Override
    public int getItemCount() {
        return sensorList.size();
    }

    public static class SensorViewHolder extends RecyclerView.ViewHolder {
        TextView tvDateTime, tvTemperature, tvHumidity, tvSoilMoisture;

        public SensorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvTemperature = itemView.findViewById(R.id.tvTemperature);
            tvHumidity = itemView.findViewById(R.id.tvHumidity);
            tvSoilMoisture = itemView.findViewById(R.id.tvSoilMoisture);
        }
    }
}

