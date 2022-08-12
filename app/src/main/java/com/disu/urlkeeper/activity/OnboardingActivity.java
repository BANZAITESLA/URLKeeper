package com.disu.urlkeeper.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.disu.urlkeeper.R;
import com.disu.urlkeeper.adapter.OnboardingAdapter;
import com.disu.urlkeeper.data.OnboardingData;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private OnboardingAdapter onboardingAdapter;
    private LinearLayout indicator_layout;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        indicator_layout = findViewById(R.id.indicator);
        button = findViewById(R.id.next_button);

        setOnboarding();

        ViewPager2 onboardingViewPager = findViewById(R.id.onboard_ViewPage);
        onboardingViewPager.setAdapter(onboardingAdapter);

        setIndicator();
        setCurrentIndicator(0);

        onboardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });


        button.setOnClickListener(view -> {
            if (onboardingViewPager.getCurrentItem() + 1 < onboardingAdapter.getItemCount()) {
                onboardingViewPager.setCurrentItem(onboardingViewPager.getCurrentItem() + 1);
            } else {
                prevData();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        if (restorePrevData()) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    private void setOnboarding() {
        List<OnboardingData> onboardingDataList = new ArrayList<>();

        OnboardingData item1 = new OnboardingData();
        item1.setImage(R.drawable.slide1);
        item1.setTitle("Manage Link");
        item1.setDesc("Create a note to manage your link list");

        OnboardingData item2 = new OnboardingData();
        item2.setImage(R.drawable.slide2);
        item2.setTitle("Generate Short Link");
        item2.setDesc("Generate a new short link to make it simpler and easier to attach somewhere");

        OnboardingData item3 = new OnboardingData();
        item3.setImage(R.drawable.slide3);
        item3.setTitle("Add Note");
        item3.setDesc("You can add a casual note or also a secret note to your link");

        onboardingDataList.add(item1);
        onboardingDataList.add(item2);
        onboardingDataList.add(item3);

        onboardingAdapter = new OnboardingAdapter(onboardingDataList);
    }

    private void setIndicator() {
        ImageView[] indicator = new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicator.length; i++) {
            indicator[i] = new ImageView(getApplicationContext());
            indicator[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.indicator_inactive));
            indicator[i].setLayoutParams(layoutParams);
            indicator_layout.addView(indicator[i]);
        }
    }

    private void setCurrentIndicator(int index) {
        int childCount = indicator_layout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) indicator_layout.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_active)
                );
            } else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_inactive)
                );
            }
        }
        if (index == onboardingAdapter.getItemCount() - 1) {
            button.setText(R.string.start);
        } else {
            button.setText(R.string.next);
        }
    }

    private void prevData() {
        SharedPreferences prev = getApplicationContext().getSharedPreferences("prev", MODE_PRIVATE);
        SharedPreferences.Editor editor = prev.edit();
        editor.putBoolean("opened",true);
        editor.apply();
    }

    private boolean restorePrevData() {
        SharedPreferences prev = getApplicationContext().getSharedPreferences("prev", MODE_PRIVATE);
        return prev.getBoolean("opened", false);
    }
}