package com.disu.urlkeeper.activity;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.disu.urlkeeper.R;
import com.disu.urlkeeper.dao.NoteDao;
import com.disu.urlkeeper.data.KeyData;
import com.disu.urlkeeper.data.UrlNoteData;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddNoteActivity extends AppCompatActivity {

    private TextInputLayout title_layout, link_layout;
    private TextInputEditText title, link, shortLink, secretNote, visibleNote;
    private Button shortLink_button, copyShortLink_button, saveButton;
    private RelativeLayout shortLink_layout;
    private MaterialToolbar toolbar;

    KeyData key = new KeyData(); // object for secret key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

//      binding
        title = findViewById(R.id.addTitle_InputEdit);
        title_layout = findViewById(R.id.addTitle_inputLayout);
        link = findViewById(R.id.addLink_inputEdit);
        link_layout = findViewById(R.id.addLink_inputLayout);
        shortLink = findViewById(R.id.addShortLink_inputEdit);
        secretNote = findViewById(R.id.addSecretNote_inputEdit);
        visibleNote = findViewById(R.id.addVisibleNote_InputEdit);
        shortLink_button = findViewById(R.id.addGenerate_shortlink);
        shortLink_layout = findViewById(R.id.addShortLink_layout);
        copyShortLink_button = findViewById(R.id.addCopyShortLink_button);
        toolbar = findViewById(R.id.materialToolbar_add);
        saveButton = findViewById(R.id.addSave_button);

//      method
        toolbarClicked();
        copyLink();
        generateSl();
        addNewNote();
    }

//  method for add new data
    private void addNewNote() {
        NoteDao dao = new NoteDao(); // object for crud process
        saveButton.setOnClickListener(view -> { // if user click save button

//          if title & link filled
            if (!title.getText().toString().equals("") && !link.getText().toString().equals("")) {
                UrlNoteData urlNoteData = new UrlNoteData( // get input text
                        dao.readId(), // generate id using firebase push for identity additional
                        title.getText().toString(),
                        link.getText().toString(),
                        shortLink.getText().toString(),
                        secretNote.getText().toString(),
                        visibleNote.getText().toString(),
                        false
                );

//              processing add note using addNote (behavior) from dao (object) for NoteDao (class)
                dao.addNote(urlNoteData)
                        .addOnSuccessListener(success -> Toast.makeText(getApplicationContext(), "Note Saved", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(error -> Toast.makeText(getApplicationContext(), "Error : " + error.getMessage(), Toast.LENGTH_LONG).show());

//              turn text-field to empty back
                title.setText("");
                link.setText("");
                shortLink.setText("");
                secretNote.setText("");
                visibleNote.setText("");

//              turn setError for text-field false back
                title_layout.setErrorEnabled(false);
                link_layout.setErrorEnabled(false);

            } else { // if title & link is not filled yet
                if (!title.getText().toString().equals("")) { // if title filled, turn text-field setError false
                    title_layout.setErrorEnabled(false);
                }

                if (!link.getText().toString().equals("")) { // if link filled, turn text-field setError false
                    link_layout.setErrorEnabled(false);
                }

                if (title.getText().toString().equals("")) { // if title is not filled, turn text-field setError true
                    title_layout.setErrorEnabled(true);
                    title_layout.setError("Title could not be blank");
                }

                if (link.getText().toString().equals("")) { // if link is not filled, turn text-field setError true
                    link_layout.setErrorEnabled(true);
                    link_layout.setError("Link could not be blank");
                }
            }
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
                            AddNoteActivity.this.runOnUiThread(() -> {
                                try {
//                                  get short link from json object format with 'result_url' as name
                                    JSONObject json = new JSONObject(url_response);
                                    shortLink.setText(json.getString("result_url"));

//                                  hide 'generate short link' button and visible short link text-field
                                    shortLink_button.setVisibility(View.GONE);
                                    shortLink_layout.setVisibility(View.VISIBLE);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });

                            response.body().close(); // close reponse body
                        } else { // if response failed show toast
                            AddNoteActivity.this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Link invalid", Toast.LENGTH_SHORT).show());
                        }
                    }
                });
            } else { // if user's real-link is blank show toast, hide short link text-field and show 'generate short link' button
                Toast.makeText(getApplicationContext(), "Insert link first", Toast.LENGTH_SHORT).show();
                shortLink_button.setVisibility(View.VISIBLE);
                shortLink_layout.setVisibility(View.GONE);
            }
        });
    }

//  method if back button on toolbar clicked, finish the activity.
    private void toolbarClicked() {
        toolbar.setNavigationOnClickListener(view -> finish());
    }

//  method if short link 'copy' button to clipboard clicked
    private void copyLink() {
        copyShortLink_button.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("short_link", shortLink.getText()); // get short link text with 'short_link' label
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), "Short link copied", Toast.LENGTH_SHORT).show(); // show toast after click
        });
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