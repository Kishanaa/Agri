package com.aurorasphere.agri;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

public class OnboardingActivity extends AppCompatActivity {

    private CardView start_btn;
    private ViewPager2 viewPager;
    private IntroAdapter adapter;
    private SpringDotsIndicator dotsIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        // Only show if first time
        SharedPreferences prefs = getSharedPreferences("onboarding_prefs", MODE_PRIVATE);
        if (!prefs.getBoolean("first_time", true)) {
            startActivity(new Intent(this, MainActivity2.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        start_btn = findViewById(R.id.start_btn);
        dotsIndicator = findViewById(R.id.dotsIndicator);

        adapter = new IntroAdapter(this);
        viewPager.setAdapter(adapter);
        dotsIndicator.setViewPager2(viewPager);

        start_btn.setVisibility(View.GONE); // hide initially

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == adapter.getItemCount() - 1) {
                    start_btn.setVisibility(View.VISIBLE);
                } else {
                    start_btn.setVisibility(View.GONE);
                }
            }
        });

        start_btn.setOnClickListener(v -> {
            SharedPreferences.Editor editor = getSharedPreferences("onboarding_prefs", MODE_PRIVATE).edit();
            editor.putBoolean("first_time", false).apply();

            startActivity(new Intent(OnboardingActivity.this, MainActivity2.class));
            finish();
        });
    }
}