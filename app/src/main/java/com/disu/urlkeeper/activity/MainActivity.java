package com.disu.urlkeeper.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.disu.urlkeeper.R;
import com.disu.urlkeeper.fragment.UrlManagerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFragment(new UrlManagerFragment());
//        loadFragment(new ProfileFragment());
//        loadFragment(new GalleryFragment());
        BottomNavigationView bottom_nav = findViewById(R.id.bottomNavigationView);
        bottom_nav.setOnItemSelectedListener(this);
        bottom_nav.setSelectedItemId(R.id.menu_url);
    }

    private boolean loadFragment(Fragment fragment){            // load fragment setter
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
//                    .setCustomAnimations(
//                            R.anim.fade_in,  // enter
//                            R.anim.fade_out,  // exit
//                            R.anim.fade_in,   // popEnter
//                            R.anim.fade_out  // popExit
//                    )
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        }

        return false;
    }

    @Override           // navigation setter
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()){
//            case R.id.menu_home:
////                fragment = new UrlManagerFragment();
//                break;
//            case R.id.menu_tag:
////                fragment = new UrlManagerFragment();
//                break;
            case R.id.menu_url:
                fragment = new UrlManagerFragment();
                break;
        }
        return loadFragment(fragment);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) { // hide keyboard and change focus
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

    @Override
    public void onBackPressed() {
        FrameLayout fl = (FrameLayout) findViewById(R.id.frame_layout);
        if (fl.getChildCount() >= 0) {
            super.onBackPressed();
            finishAffinity();
        }
    }
}