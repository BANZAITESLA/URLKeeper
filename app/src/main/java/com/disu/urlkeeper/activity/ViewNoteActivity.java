package com.disu.urlkeeper.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.disu.urlkeeper.R;
import com.disu.urlkeeper.dao.NoteDao;
import com.disu.urlkeeper.data.KeyData;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

    NoteDao dao = new NoteDao(); // object for crud code
    KeyData key = new KeyData(); // object for secret key

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

//      binding
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

//      firebase authentication initial
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

//      extra from url manager fragment to identify data
        id = getIntent().getStringExtra("id");

//      call method
        getData();
        generateSl();
        copyLink();
        toolbarClicked();
    }

//  method to get data by ordered by child 'id' from extra
    private void getData() {
//      initiate database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("note").child(mCurrentUser.getUid());
        databaseReference.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {

//          method to get static data snapshot
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    url = dataSnapshot.getValue(UrlNoteData.class); // object for UrlNoteData
                    String key = dataSnapshot.getKey(); // collect prev node

//                  set text-field with data snapshot
                    title.setText(url.getTitle());
                    link.setText(url.getUrl());
                    secretNote.setText(url.getSecret_note());
                    visibleNote.setText(url.getVisible_note());
                    star = url.isStar();

//                  validation if short url not blank / generated
                    if (dataSnapshot.child("short_url").exists() && !url.getShort_url().equals("")) {
                        shortLink_button.setVisibility(View.GONE); // hide button 'generate short link'
                        shortLink_layout.setVisibility(View.VISIBLE); // show short link layout
                        shortLink.setText(url.getShort_url()); // set short link text-field with data snapshot
                    } else { // if short url ungenerated
                        shortLink_button.setVisibility(View.VISIBLE); // show button 'generate short link'
                        shortLink_layout.setVisibility(View.GONE); // hide short link layout
                    }

                    updateData(key); // method for update data by prev node

//                  validation if user starred the note
                    if (url.isStar()) {
                        toolbar.inflateMenu(R.menu.view_note_starred); // set menu for starred note
                    } else { // if note is not starred
                        toolbar.inflateMenu(R.menu.view_note_unstarred); // set menu for unstarred note
                    }

//                  validation if short url is not generated
                    if (url.getShort_url().equals("")) { // hide menu 'delete short link'
                        toolbar.getMenu().findItem(R.id.delete_shortLink_view).setEnabled(false).setVisible(false); // hide menu 'delete short link'
                    } else {
                        toolbar.getMenu().findItem(R.id.delete_shortLink_view).setEnabled(true).setVisible(true); // show menu 'delete short link'
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

//  method to update data by key prev node
    private void updateData(String key) {
        save_button.setOnClickListener(view -> { // if save button clicked
//          validation if title & link filled
            if (!title.getText().toString().equals("") && !link.getText().toString().equals("")) {

//              set new data from the text field
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("title", title.getText().toString());
                hashMap.put("url", link.getText().toString());
                hashMap.put("short_url", shortLink.getText().toString());
                hashMap.put("secret_note", secretNote.getText().toString());
                hashMap.put("visible_note", visibleNote.getText().toString());

//              processing update note using updateNote (behavior) from dao (object) for NoteDao (class)
                dao.updateNote(key, hashMap)
                        .addOnSuccessListener(success -> Toast.makeText(getApplicationContext(), "Note Updated", Toast.LENGTH_SHORT).show()) // show toast
                        .addOnFailureListener(error -> Toast.makeText(getApplicationContext(), "Error : " + error.getMessage(), Toast.LENGTH_LONG).show()); // show toast

//              turn setError for title and link layout false back
                title_layout.setErrorEnabled(false);
                link_layout.setErrorEnabled(false);

//              show link copy button
                copyLink_button.setVisibility(View.VISIBLE);

            } else { // if title & link is not filled yet
                if (!title.getText().toString().equals("")) { // if title filled, turn text-field setError false
                    title_layout.setErrorEnabled(false);
                }

                if (!link.getText().toString().equals("")) { // if link filled, turn text-field setError false
                    link_layout.setErrorEnabled(false);
                    copyLink_button.setVisibility(View.VISIBLE); // show link copy button
                }

                if (title.getText().toString().equals("")) { // if title is not filled, turn text-field setError true
                    title_layout.setErrorEnabled(true);
                    title_layout.setError("Title could not be blank");
                }

                if (link.getText().toString().equals("")) { // if link is not filled, turn text-field setError true
                    copyLink_button.setVisibility(View.GONE); // hide link cpoy button
                    link_layout.setErrorEnabled(true);
                    link_layout.setError("Link could not be blank");
                }
            }
        });
    }

//  method delete data
    private void deleteData() {
//      initiate database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("note").child(mCurrentUser.getUid());
        databaseReference.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {

//          method to get static data snapshot
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String key = dataSnapshot.getKey(); // collect prev node

//                  processing delete note using removeNote (behavior) from dao (object) for NoteDao (class)
                    dao.removeNote(key)
                            .addOnSuccessListener(success -> {
                                Toast.makeText(getApplicationContext(), "Note deleted", Toast.LENGTH_SHORT).show();
                                finish(); }) // back to prev activity
                            .addOnFailureListener(error -> Toast.makeText(getApplicationContext(), "Error : " + error.getMessage(), Toast.LENGTH_LONG).show());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

//  method set star
    private void starData(MenuItem item) {
//      initiate database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("note").child(mCurrentUser.getUid());
        databaseReference.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {

//          method to get static data snapshot
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String key = dataSnapshot.getKey(); // collect prev node
                    url = dataSnapshot.getValue(UrlNoteData.class); // object for UrlNoteData

                    boolean star = url.isStar(); // collect data from firebase for 'star'

                    HashMap<String, Object> hashMap = new HashMap<>();

                    if (!star) { // if star is false
                        hashMap.put("star", true); // turn true

//                      processing update note using updateNote (behavior) from dao (object) for NoteDao (class)
                        dao.updateNote(key, hashMap)
                                .addOnSuccessListener(success -> {
                                    url.setStar(true); // set star true for UrlNoteData
                                    item.setIcon(R.drawable.ic_star_solid); // set icon star solid
                                    Toast.makeText(getApplicationContext(), "Note Starred", Toast.LENGTH_SHORT).show();}) // show toast
                                .addOnFailureListener(error -> Toast.makeText(getApplicationContext(), "Error : " + error.getMessage(), Toast.LENGTH_LONG).show()); // show toast
                    } else { // if star is true
                        hashMap.put("star", false); // turn false

//                      processing update note using updateNote (behavior) from dao (object) for NoteDao (class)
                        dao.updateNote(key, hashMap)
                                .addOnSuccessListener(success -> {
                                    url.setStar(false); // set star false for UrlNoteData
                                    item.setIcon(R.drawable.ic_star_regular); // set icon star regular
                                    Toast.makeText(getApplicationContext(), "Star removed", Toast.LENGTH_SHORT).show(); }) // show toast
                                .addOnFailureListener(error -> Toast.makeText(getApplicationContext(), "Error : " + error.getMessage(), Toast.LENGTH_LONG).show()); // show toast
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

//  method to direct into browser
    private void browse() {
        Intent url = new Intent(Intent.ACTION_VIEW);

//      validation if link contain 'http'
        if (link.getText().toString().contains("http")) {
            url.setData(Uri.parse(link.getText().toString()));
        } else { // if link does not contain http/https, set it
            url.setData(Uri.parse("https://" + link.getText().toString()));
        }
        this.startActivity(url); // go to browser
    }

//  method if user delete short link
    private void deleteShortlink() {
//      initiate database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("note").child(mCurrentUser.getUid());
        databaseReference.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {

//          method to get static data snapshot
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String key = dataSnapshot.getKey(); // collect prev node

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("short_url", ""); // set data empty

//                  processing update note using updateNote (behavior) from dao (object) for NoteDao (class)
                    dao.updateNote(key, hashMap)
                            .addOnSuccessListener(success -> {
                                shortLink_button.setVisibility(View.VISIBLE); // show 'generate short link'
                                shortLink_layout.setVisibility(View.GONE); // hide 'short link layout'
                                Toast.makeText(getApplicationContext(), "Short link removed", Toast.LENGTH_SHORT).show(); }) // show toast
                            .addOnFailureListener(error -> Toast.makeText(getApplicationContext(), "Error : " + error.getMessage(), Toast.LENGTH_LONG).show()); // show toast
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

//  method for generate short link
    private void generateSl() {
        shortLink_button.setOnClickListener(view -> { // if 'generate short link' clicked
            String user_link = link.getText().toString(); // get real-link
            OkHttpClient client = new OkHttpClient(); // object for OkHttpClient class (library for link request)

            if (!user_link.equals("")) { // if real-link filled

//              user's real-lik validation cause API request body only accept link with http / https format
                if (user_link.contains("http")) {
                    user_link = link.getText().toString();
                } else { // if user's real-link have no 'http' then add 'https://' to the link
                    user_link = "https://" + link.getText().toString();
                }

//              fill in the request body
                RequestBody body = new FormBody.Builder()
                        .add("url", user_link) // with user's real-link as 'url'
                        .build();

//              set header parameter
                Request request = new Request.Builder()
                        .url("https://url-shortener-service.p.rapidapi.com/shorten")
                        .post(body)
                        .addHeader("content-type", "application/x-www-form-urlencoded")
                        .addHeader("X-RapidAPI-Key", key.getKey()) // get key from KeyData clasa
                        .addHeader("X-RapidAPI-Host", "url-shortener-service.p.rapidapi.com")
                        .build();

//              async get call from request
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {}

                    @Override // if get response
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) { // response is success
//                          set url_response to collect response body
                            final String url_response = response.body().string();
                            ViewNoteActivity.this.runOnUiThread(() -> {
                                try {
//                                  get short link from json object format with 'result_url' as name
                                    JSONObject json = new JSONObject(url_response);
                                    shortLink.setText(json.getString("result_url"));

//                                  hide 'generate short link' button and visible short link text-field
                                    shortLink_button.setVisibility(View.GONE);
                                    shortLink_layout.setVisibility(View.VISIBLE);

//                                  show menu 'delete short link'
                                    toolbar.getMenu().findItem(R.id.delete_shortLink_view).setEnabled(true).setVisible(true);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });

                            response.body().close(); // close reponse body
                        } else { // if response failed show toast
                            ViewNoteActivity.this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Link invalid", Toast.LENGTH_SHORT).show());
                        }
                    }
                });
            } else { // if user's real-link is blank show toast, hide short link text-field and show 'generate short link' button
                Toast.makeText(getApplicationContext(), "Insert link first", Toast.LENGTH_SHORT).show();
                shortLink_button.setVisibility(View.VISIBLE);
                shortLink_layout.setVisibility(View.GONE);

//              hide menu 'delete short link'
                toolbar.getMenu().findItem(R.id.delete_shortLink_view).setEnabled(false).setVisible(false);
            }
        });
    }

//  method if 'copy' button to clipboard clicked
    private void copyLink() {
        copyLink_button.setOnClickListener(view -> { // if copy link clicked
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("link", link.getText()); // get link text with 'link' label
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), "Link copied", Toast.LENGTH_SHORT).show(); // show toast after click
        });

        copyShortLink_button.setOnClickListener(view -> { // if copy short link clicked
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("short_link", shortLink.getText()); // get short link text with 'short_link' label
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), "Short link copied", Toast.LENGTH_SHORT).show(); // show toast after click
        });
    }

//  method if menu on top app bar clicked
    private void toolbarClicked() {
        toolbar.setNavigationOnClickListener(view -> finish()); // if back button clicked, finish activity
        toolbar.setOnMenuItemClickListener(item -> { // navigation menu setter
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

//  method dialog when delete button clicked
    private void dialogDelete() {
//      create dialog layout programmatically
        new MaterialAlertDialogBuilder(ViewNoteActivity.this, R.style.ThemeOverlay_App_MaterialAlertDialog)
                .setTitle(R.string.delete_title_dialog)
                .setMessage(R.string.delete_message_dialog)
                .setPositiveButton(R.string.delete, (dialogInterface, i) -> deleteData())
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {})
                .show();
    }

    @Override // hide keyboard and change focus
    public boolean dispatchTouchEvent(MotionEvent event) {
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