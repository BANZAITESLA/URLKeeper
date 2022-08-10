package com.disu.urlkeeper.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import com.disu.urlkeeper.R;
import com.disu.urlkeeper.dao.NoteDao;
import com.disu.urlkeeper.data.UrlNoteData;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ViewNoteActivity extends AppCompatActivity {

    Query databaseReference;
    private TextInputLayout title_layout, link_layout;
    private TextInputEditText title, link, shortLink, secretNote, visibleNote;
    private Button shortLink_button, copyLink_button, copyShortLink_button, save_button;
    private RelativeLayout shortLink_layout;
    private MaterialToolbar toolbar;
    private boolean star;
    UrlNoteData url;
    String id;
    NoteDao dao = new NoteDao();
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        title = findViewById(R.id.title_InputEdit);
        title_layout = findViewById(R.id.title_inputLayout);
        link = findViewById(R.id.link_inputEdit);
        link_layout = findViewById(R.id.link_inputLayout);
        shortLink = findViewById(R.id.shortLink_inputEdit);
        secretNote = findViewById(R.id.secretNote_inputEdit);
        visibleNote = findViewById(R.id.visibleNote_InputEdit);
        shortLink_button = findViewById(R.id.generate_shortlink);
        shortLink_layout = findViewById(R.id.shortLink_layout);
        copyLink_button = findViewById(R.id.copyLink_button);
        copyShortLink_button = findViewById(R.id.copyShortLink_button);
        toolbar = findViewById(R.id.materialToolbar);
        save_button = findViewById(R.id.save_button);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        id = getIntent().getStringExtra("id");

        getData();
        copyLink();
        toolbarClicked();
    }

    private void getData() { // get data by id
        databaseReference = FirebaseDatabase.getInstance().getReference().child("note").child(mCurrentUser.getUid());
        databaseReference.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    url = dataSnapshot.getValue(UrlNoteData.class);
                    String key = dataSnapshot.getKey();

                    title.setText(url.getTitle());
                    link.setText(url.getUrl());
                    secretNote.setText(url.getSecret_note());
                    visibleNote.setText(url.getVisible_note());
                    star = url.isStar();

                    if (dataSnapshot.child("short_url").exists() && !url.getShort_url().equals("")) {
                        shortLink_button.setVisibility(View.GONE);
                        shortLink_layout.setVisibility(View.VISIBLE);
                        shortLink.setText(url.getShort_url());
                    } else {
                        shortLink_button.setVisibility(View.VISIBLE);
                        shortLink_layout.setVisibility(View.GONE);
                    }

                    updateData(key);
                    if (url.isStar()) {
                        toolbar.inflateMenu(R.menu.view_note_starred);
                    } else {
                        toolbar.inflateMenu(R.menu.view_note_unstarred);
                    }

                    if (url.getShort_url().equals("")) {
                        toolbar.getMenu().findItem(R.id.delete_shortLink_view).setEnabled(false).setVisible(false);
                    } else {
                        toolbar.getMenu().findItem(R.id.delete_shortLink_view).setEnabled(true).setVisible(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateData(String key) {
        save_button.setOnClickListener(view -> {
            if (!title.getText().toString().equals("") && !link.getText().toString().equals("")) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("title", title.getText().toString());
                hashMap.put("url", link.getText().toString());
                hashMap.put("short_url", shortLink.getText().toString());
                hashMap.put("secret_note", secretNote.getText().toString());
                hashMap.put("visible_note", visibleNote.getText().toString());
                dao.updateNote(key, hashMap)
                        .addOnSuccessListener(success -> Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(error -> Toast.makeText(getApplicationContext(), "Error : " + error.getMessage(), Toast.LENGTH_LONG).show());

                title_layout.setErrorEnabled(false);
                link_layout.setErrorEnabled(false);
                copyLink_button.setVisibility(View.VISIBLE);
            } else {
                if (!title.getText().toString().equals("")) {
                    title_layout.setErrorEnabled(false);
                }

                if (!link.getText().toString().equals("")) {
                    link_layout.setErrorEnabled(false);
                    copyLink_button.setVisibility(View.VISIBLE);
                }

                if (title.getText().toString().equals("")) {
                    title_layout.setErrorEnabled(true);
                    title_layout.setError("Title could not be blank");
                }

                if (link.getText().toString().equals("")) {
                    copyLink_button.setVisibility(View.GONE);
                    link_layout.setErrorEnabled(true);
                    link_layout.setError("Link could not be blank");
                }
            }
        });
    }

    private void deleteData() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("note").child(mCurrentUser.getUid());
        databaseReference.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String key = dataSnapshot.getKey();
                    dao.removeNote(key)
                            .addOnSuccessListener(success -> {
                                Toast.makeText(getApplicationContext(), "Deleted!", Toast.LENGTH_SHORT).show();
                                finish(); })
                            .addOnFailureListener(error -> Toast.makeText(getApplicationContext(), "Error : " + error.getMessage(), Toast.LENGTH_LONG).show());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void starData(MenuItem item) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("note").child(mCurrentUser.getUid());
        databaseReference.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String key = dataSnapshot.getKey();
                    url = dataSnapshot.getValue(UrlNoteData.class);
                    boolean star = url.isStar();

                    HashMap<String, Object> hashMap = new HashMap<>();

                    if (!star) {
                        hashMap.put("star", true);
                        dao.updateNote(key, hashMap)
                                .addOnSuccessListener(success -> {
                                    url.setStar(true);
                                    item.setIcon(R.drawable.ic_star_solid);
                                    Toast.makeText(getApplicationContext(), "Starred!", Toast.LENGTH_SHORT).show();})
                                .addOnFailureListener(error -> Toast.makeText(getApplicationContext(), "Error : " + error.getMessage(), Toast.LENGTH_LONG).show());
                    } else {
                        hashMap.put("star", false);
                        dao.updateNote(key, hashMap)
                                .addOnSuccessListener(success -> {
                                    url.setStar(false);
                                    item.setIcon(R.drawable.ic_star_regular);
                                    Toast.makeText(getApplicationContext(), "Unstarred!", Toast.LENGTH_SHORT).show(); })
                                .addOnFailureListener(error -> Toast.makeText(getApplicationContext(), "Error : " + error.getMessage(), Toast.LENGTH_LONG).show());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void browse() {
        Intent url = new Intent(Intent.ACTION_VIEW);
        if (link.getText().toString().contains("http")) {
            url.setData(Uri.parse(link.getText().toString()));
        } else {
            url.setData(Uri.parse("https://" + link.getText().toString()));
        }
        this.startActivity(url);
    }

    private void deleteShortlink() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("note").child(mCurrentUser.getUid());
        databaseReference.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String key = dataSnapshot.getKey();

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("short_url", "");

                    dao.updateNote(key, hashMap)
                            .addOnSuccessListener(success -> {
                                shortLink_button.setVisibility(View.VISIBLE);
                                shortLink_layout.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Removed!", Toast.LENGTH_SHORT).show(); })
                            .addOnFailureListener(error -> Toast.makeText(getApplicationContext(), "Error : " + error.getMessage(), Toast.LENGTH_LONG).show());
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
            Toast.makeText(getApplicationContext(), "Link copied", Toast.LENGTH_SHORT).show();
        });

        copyShortLink_button.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("short_link", shortLink.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), "Short link copied", Toast.LENGTH_SHORT).show();
        });
    }

    private void toolbarClicked() {
        toolbar.setNavigationOnClickListener(view -> finish());
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.star_view:
                    starData(item);
                    break;
                case R.id.browse_view:
                    browse();
                    break;
                case R.id.delete_view:
                    dialogDelete();
                    break;
                case R.id.delete_shortLink_view:
                    deleteShortlink();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + item.getItemId());
            }
            return false;
        });
    }

    private void dialogDelete() {
        new MaterialAlertDialogBuilder(ViewNoteActivity.this, R.style.ThemeOverlay_App_MaterialAlertDialog)
                .setTitle(R.string.delete_title_dialog)
                .setMessage(R.string.delete_message_dialog)
                .setPositiveButton(R.string.delete, (dialogInterface, i) -> deleteData())
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {})
                .show();
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