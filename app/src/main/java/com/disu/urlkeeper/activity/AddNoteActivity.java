package com.disu.urlkeeper.activity;

import androidx.appcompat.app.AppCompatActivity;
import com.disu.urlkeeper.R;
import com.disu.urlkeeper.dao.NoteDao;
import com.disu.urlkeeper.data.UrlNoteData;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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

public class AddNoteActivity extends AppCompatActivity {

    private TextInputLayout title_layout, link_layout;
    private TextInputEditText title, link, shortLink, secretNote, visibleNote;
    private Button shortLink_button, copyShortLink_button, saveButton;
    private RelativeLayout shortLink_layout;
    private MaterialToolbar toolbar;

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

    private void toolbarClicked() {
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    private void copyLink() {
        copyShortLink_button.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("short_link", shortLink.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), "Short link copied", Toast.LENGTH_LONG).show();
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