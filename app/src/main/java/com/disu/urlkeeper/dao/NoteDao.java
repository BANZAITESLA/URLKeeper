package com.disu.urlkeeper.dao;

import com.disu.urlkeeper.data.UrlNoteData;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * 10/08/2022 | 10119239 | DEA INESIA SRI UTAMI | IF6
 */

public class NoteDao {
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    public NoteDao() {
//      firebase authentication initial
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

//      initiate database reference
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference().child("note").child(mCurrentUser.getUid());
    }

//  method add note
    public Task<Void> addNote(UrlNoteData urlNoteData) {
        return databaseReference.push().setValue(urlNoteData);
    }

//  method generate id
    public String readId() {
        return databaseReference.push().getKey();
    }

//  method update note
    public Task<Void> updateNote(String key, HashMap<String, Object> hashMap) {
        return databaseReference.child(key).updateChildren(hashMap);
    }

//  method delete note
    public Task<Void> removeNote(String key) {
        return databaseReference.child(key).removeValue();
    }
}
