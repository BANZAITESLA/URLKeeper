package com.disu.urlkeeper.activity;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.RelativeLayout;

import com.disu.urlkeeper.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//      initiate database reference
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();

        if (mCurrentUser == null) { // if user is unregistered
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInAnonymously:success");
                        } else {
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                        }
                    });
        }

//      set delay next activity pada splash screen
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, OnboardingActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}