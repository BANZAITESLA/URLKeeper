package com.disu.urlkeeper.dao;

import com.disu.urlkeeper.data.UrlNoteData;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

/**
 * 10/08/2022 | 10119239 | DEA INESIA SRI UTAMI | IF6
 */
public class NoteDao {
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    public NoteDao() {
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference().child("note").child(mCurrentUser.getUid());
    }

    public Task<Void> addNote(UrlNoteData urlNoteData) {
        return databaseReference.push().setValue(urlNoteData);
    }

    public String readId() {
        return databaseReference.push().getKey();
    }

    public Task<Void> updateNote(String key, HashMap<String, Object> hashMap) {
        return databaseReference.child(key).updateChildren(hashMap);
    }

    public Task<Void> removeNote(String key) {
        return databaseReference.child(key).removeValue();
    }
}
