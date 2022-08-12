package com.disu.urlkeeper.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

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

//      binding
        indicator_layout = findViewById(R.id.indicator);
        button = findViewById(R.id.next_button);

        setOnboarding(); // method to set onboarding item

        ViewPager2 onboardingViewPager = findViewById(R.id.onboard_ViewPage); // binding viewpager
        onboardingViewPager.setAdapter(onboardingAdapter); // set adapter to viewpager

        setIndicator(); // method to set indicator
        setCurrentIndicator(0); // set initiate indicator

        onboardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });

        button.setOnClickListener(view -> { // if button next clicked
            if (onboardingViewPager.getCurrentItem() + 1 < onboardingAdapter.getItemCount()) {
//              show next slide
                onboardingViewPager.setCurrentItem(onboardingViewPager.getCurrentItem() + 1);
            } else {
                prevData(); // mark onboarding activity shouldnt show anymore for the user
                startActivity(new Intent(getApplicationContext(), MainActivity.class)); // go to main activity
                finish(); // finish this activity
            }
        });

        if (restorePrevData()) { // mark that user should directly go to main activity after splash screen
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

//  method to set onboarding item
    private void setOnboarding() {
        List<OnboardingData> onboardingDataList = new ArrayList<>(); // object for onboarding data class

//      item slide 1
        OnboardingData item1 = new OnboardingData();
        item1.setImage(R.drawable.slide1);
        item1.setTitle("Manage Link");
        item1.setDesc("Create a note to manage your link list");

//      item slide 2
        OnboardingData item2 = new OnboardingData();
        item2.setImage(R.drawable.slide2);
        item2.setTitle("Generate Short Link");
        item2.setDesc("Generate a new short link to make it simpler and easier to attach somewhere");

//      item slide 3
        OnboardingData item3 = new OnboardingData();
        item3.setImage(R.drawable.slide3);
        item3.setTitle("Add Note");
        item3.setDesc("You can add a casual note or also a secret note to your link");

//      add item to the object for onboarding-data class
        onboardingDataList.add(item1);
        onboardingDataList.add(item2);
        onboardingDataList.add(item3);

//      set it to the adapter
        onboardingAdapter = new OnboardingAdapter(onboardingDataList);
    }

//  method to set indicator
    private void setIndicator() {
        ImageView[] indicator = new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( // create linear layout
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 0, 8, 0); // set margin
        for (int i = 0; i < indicator.length; i++) { // initiate index indicator as imageview
            indicator[i] = new ImageView(getApplicationContext());
            indicator[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.indicator_inactive)); // set drawable inactive as the beginning
            indicator[i].setLayoutParams(layoutParams); // set indicator to layout
            indicator_layout.addView(indicator[i]);
        }
    }

//  method to set current index for indicator
    private void setCurrentIndicator(int index) {
        int childCount = indicator_layout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) indicator_layout.getChildAt(i);
            if (i == index) { // set indicator active if slide shows
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_active)
                );
            } else { // set indicator inactive if slide doesn't show
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_inactive)
                );
            }
        }

//      validation for setting text button
        if (index == onboardingAdapter.getItemCount() - 1) { // if slide onboarding finish
            button.setText(R.string.start);
        } else { // if slide onboarding unfinished
            button.setText(R.string.next);
        }
    }

//  method to mark user for onboarding activity
    private void prevData() {
        SharedPreferences prev = getApplicationContext().getSharedPreferences("prev", MODE_PRIVATE);
        SharedPreferences.Editor editor = prev.edit();
        editor.putBoolean("opened",true);
        editor.apply();
    }

//  method to mark user for onboarding activity
    private boolean restorePrevData() {
        SharedPreferences prev = getApplicationContext().getSharedPreferences("prev", MODE_PRIVATE);
        return prev.getBoolean("opened", false);
    }
}