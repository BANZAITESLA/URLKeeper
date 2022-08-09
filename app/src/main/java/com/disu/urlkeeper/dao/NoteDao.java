package com.disu.urlkeeper.dao;

import com.disu.urlkeeper.data.UrlNoteData;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * 10/08/2022 | 10119239 | DEA INESIA SRI UTAMI | IF6
 */
public class NoteDao {
    private DatabaseReference databaseReference;

    public NoteDao() {
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference().child("note").child("user1");
    }

    public Task<Void> addNote(UrlNoteData urlNoteData) {
        return databaseReference.push().setValue(urlNoteData);
    }

    public String readId() {
        return databaseReference.push().getKey();
    }
}
