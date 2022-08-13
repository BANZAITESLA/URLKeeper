package com.disu.urlkeeper.activity;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.disu.urlkeeper.R;
import com.disu.urlkeeper.fragment.StarUrlFragment;
import com.disu.urlkeeper.fragment.UrlManagerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottom_nav = findViewById(R.id.bottomNavigationView);
        bottom_nav.setOnItemSelectedListener(this);
        bottom_nav.setSelectedItemId(R.id.menu_url); // URL manager as first menu to show
    }

//  method load fragment setter
    private boolean loadFragment(Fragment fragment){
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return false;
    }

    @Override // navigation menu setter
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()){
            case R.id.starring:
                fragment = new StarUrlFragment();
                break;
            case R.id.menu_url:
                fragment = new UrlManagerFragment();
                break;
        }
        return loadFragment(fragment);
    }

    @Override // hide keyboard and change focus
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            View v = getCurrentFocus();
            if ( v instanceof TextInputEditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    @Override // back button pressed, finish app
    public void onBackPressed() {
        FrameLayout fl = findViewById(R.id.frame_layout);
        if (fl.getChildCount() >= 0) {
            super.onBackPressed();
            finishAffinity();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}