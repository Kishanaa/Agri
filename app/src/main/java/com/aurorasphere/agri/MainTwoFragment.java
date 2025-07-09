package com.aurorasphere.agri;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainTwoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainTwoFragment extends Fragment {

    TextView stateName_tx,cityName_tx,cultivation_tx,price_tx;
    CardView scanner;

    public MainTwoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_two, container, false);

        stateName_tx = view.findViewById(R.id.stateName_tx);
        cityName_tx = view.findViewById(R.id.cityName_tx);
        cultivation_tx = view.findViewById(R.id.cultivation_tx);
        price_tx = view.findViewById(R.id.price_tx);
        scanner = view.findViewById(R.id.scanner);

        CropSelectionManager.loadSelection(requireContext());

        String state = CropSelectionManager.getSelectedState();
        String city = CropSelectionManager.getSelectedCity();
        String crop = CropSelectionManager.getSelectedCrop();
        CropInfo info = CropSelectionManager.getSelectedCropInfo();
        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "Future Implementation", Toast.LENGTH_SHORT).show();
            }
        });

        if (state != null && city != null && crop != null && info != null) {
            stateName_tx.setText(state);
            cityName_tx.setText(city);
            cultivation_tx.setText(info.Cultivation);
            price_tx.setText("₹" + info.crop_price + " per quintal");
        } else {
            Toast.makeText(requireContext(), "No crop selected", Toast.LENGTH_SHORT).show();
        }



        return view;
    }
}