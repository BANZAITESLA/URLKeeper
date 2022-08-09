package com.disu.urlkeeper.dao;

import com.disu.urlkeeper.data.UrlNoteData;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * 09/08/2022 | 10119239 | DEA INESIA SRI UTAMI | IF6
 */
public class ViewNoteDao {
    private DatabaseReference databaseReference;

    public ViewNoteDao() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(UrlNoteData.class.getSimpleName());
    }

    public Task<Void> get(UrlNoteData urlNoteData) {
        return databaseReference.push().setValue(urlNoteData);
    }
}
