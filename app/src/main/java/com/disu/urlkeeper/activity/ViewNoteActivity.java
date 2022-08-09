package com.disu.urlkeeper.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.MenuItem;
import com.disu.urlkeeper.R;
import com.disu.urlkeeper.data.UrlNoteData;
import com.disu.urlkeeper.fragment.UrlManagerFragment;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ViewNoteActivity extends AppCompatActivity {

    Query databaseReference;
    private TextInputEditText title, link, shortLink, secretNote, visibleNote;
    private Button shortLink_button, copyLink_button, copyShortLink_button;
    private RelativeLayout shortLink_layout;
    private MaterialToolbar toolbar;
    UrlNoteData url;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        title = findViewById(R.id.title_InputEdit);
        link = findViewById(R.id.link_inputEdit);
        shortLink = findViewById(R.id.shortLink_inputEdit);
        secretNote = findViewById(R.id.secretNote_inputEdit);
        visibleNote = findViewById(R.id.visibleNote_InputEdit);
        shortLink_button = findViewById(R.id.generate_shortlink);
        shortLink_layout = findViewById(R.id.shortLink_layout);
        copyLink_button = findViewById(R.id.copyLink_button);
        copyShortLink_button = findViewById(R.id.copyShortLink_button);
        toolbar = findViewById(R.id.materialToolbar);

        id = getIntent().getStringExtra("id");

        getData();
        copyLink();
        toolbarClicked();
    }

    private void getData() { // get data by id
        databaseReference = FirebaseDatabase.getInstance().getReference().child("note").child("user1");
        databaseReference.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    url = dataSnapshot.getValue(UrlNoteData.class);

                    title.setText(url.getTitle());
                    link.setText(url.getUrl());
                    secretNote.setText(url.getSecret_note());
                    visibleNote.setText(url.getVisible_note());

                    if (dataSnapshot.child("short_url").exists()) {
                        shortLink_layout.setVisibility(View.VISIBLE);
                        shortLink.setText(url.getShort_url());
                        shortLink_button.setVisibility(View.GONE);
                    } else {
                        shortLink_button.setVisibility(View.VISIBLE);
                        shortLink_layout.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void copyLink() {
        copyLink_button.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("link", link.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), "Link copied", Toast.LENGTH_LONG).show();
        });

        copyShortLink_button.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("short_link", shortLink.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), "Short link copied", Toast.LENGTH_LONG).show();
        });
    }

    private void toolbarClicked() {
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.star_view:
//                fragment = new UrlManagerFragment();
                break;
//            case R.id.menu_tag:
////                fragment = new UrlManagerFragment();
//                break;
            case R.id.menu_url:
//                fragment = new UrlManagerFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
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
}