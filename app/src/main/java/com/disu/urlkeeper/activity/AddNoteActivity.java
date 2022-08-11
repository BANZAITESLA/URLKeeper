package com.disu.urlkeeper.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.disu.urlkeeper.BuildConfig;
import com.disu.urlkeeper.R;
import com.disu.urlkeeper.dao.NoteDao;
import com.disu.urlkeeper.data.KeyData;
import com.disu.urlkeeper.data.UrlNoteData;
import com.google.android.gms.common.api.internal.ApiKey;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

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

    KeyData key = new KeyData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

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

        toolbarClicked();
        copyLink();
        generateSl();
        addNewNote();
    }

    private void addNewNote() {
        NoteDao dao = new NoteDao();
        saveButton.setOnClickListener(view -> {
            if (!title.getText().toString().equals("") && !link.getText().toString().equals("")) {
                UrlNoteData urlNoteData = new UrlNoteData(dao.readId(), title.getText().toString(), link.getText().toString(), shortLink.getText().toString(), secretNote.getText().toString(), visibleNote.getText().toString(), false);
                dao.addNote(urlNoteData)
                        .addOnSuccessListener(success -> Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(error -> Toast.makeText(getApplicationContext(), "Error : " + error.getMessage(), Toast.LENGTH_LONG).show());

                title.setText("");
                link.setText("");
                shortLink.setText("");
                secretNote.setText("");
                visibleNote.setText("");
                title_layout.setErrorEnabled(false);
                link_layout.setErrorEnabled(false);
            } else {
                if (!title.getText().toString().equals("")) {
                    title_layout.setErrorEnabled(false);
                }

                if (!link.getText().toString().equals("")) {
                    link_layout.setErrorEnabled(false);
                }

                if (title.getText().toString().equals("")) {
                    title_layout.setErrorEnabled(true);
                    title_layout.setError("Title could not be blank");
                }

                if (link.getText().toString().equals("")) {
                    link_layout.setErrorEnabled(true);
                    link_layout.setError("Link could not be blank");
                }
            }
        });
    }

    private void generateSl() {
        shortLink_button.setOnClickListener(view -> {
            String user_link = link.getText().toString();
            OkHttpClient client = new OkHttpClient();

            if (!user_link.equals("")) {
                if (user_link.contains("http")) {
                    user_link = link.getText().toString();
                } else {
                    user_link = "https://" + link.getText().toString();
                }

                RequestBody body = new FormBody.Builder()
                        .add("url", user_link)
                        .build();

                Request request = new Request.Builder()
                        .url("https://url-shortener-service.p.rapidapi.com/shorten")
                        .post(body)
                        .addHeader("content-type", "application/x-www-form-urlencoded")
                        .addHeader("X-RapidAPI-Key", key.getKey())
                        .addHeader("X-RapidAPI-Host", "url-shortener-service.p.rapidapi.com")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {}

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            final String url_response = response.body().string();
                            AddNoteActivity.this.runOnUiThread(() -> {
                                try {
                                    JSONObject json = new JSONObject(url_response);
                                    shortLink.setText(json.getString("result_url"));
                                    shortLink_button.setVisibility(View.GONE);
                                    shortLink_layout.setVisibility(View.VISIBLE);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });

                            response.body().close();
                        } else {
                            AddNoteActivity.this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Link invalid", Toast.LENGTH_SHORT).show());
                        }
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Insert link first", Toast.LENGTH_SHORT).show();
                shortLink_button.setVisibility(View.VISIBLE);
                shortLink_layout.setVisibility(View.GONE);
            }
        });
    }

    private void toolbarClicked() {
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    private void copyLink() {
        copyShortLink_button.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("short_link", shortLink.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), "Short link copied", Toast.LENGTH_SHORT).show();
        });
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