package com.disu.urlkeeper.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.disu.urlkeeper.R;
import com.disu.urlkeeper.activity.ViewNoteActivity;
import com.disu.urlkeeper.dao.NoteDao;
import com.disu.urlkeeper.data.UrlNoteData;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * 06/08/2022 | 10119239 | DEA INESIA SRI UTAMI | IF6
 */

public class UrlManagerAdapter extends FirebaseRecyclerAdapter<UrlNoteData, UrlManagerAdapter.ViewHolder> {

    Query databaseReference;
    UrlNoteData url;

    NoteDao dao = new NoteDao(); // object for crud code

    private FirebaseUser mCurrentUser;

    public UrlManagerAdapter(@NonNull FirebaseRecyclerOptions<UrlNoteData> options) { // constructor adapter
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UrlManagerAdapter.ViewHolder holder, int position, @NonNull UrlNoteData model) {
        //      firebase authentication initial
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        holder.title.setText(model.getTitle()); // set title note
        holder.link.setHint(model.getUrl()); // set link note

        holder.star_check.setChecked(model.isStar()); // set star

        holder.star_check.setOnClickListener(view -> { // if star clicked
//          initiate database reference
            databaseReference = FirebaseDatabase.getInstance().getReference().child("note").child(mCurrentUser.getUid());
            databaseReference.orderByChild("id").equalTo(model.getId()).addListenerForSingleValueEvent(new ValueEventListener() {

//              method to get static data snapshot
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String key = dataSnapshot.getKey(); // collect prev node
                        url = dataSnapshot.getValue(UrlNoteData.class); // object for UrlNoteData

                        boolean star = url.isStar(); // collect data from firebase for 'star'

                        HashMap<String, Object> hashMap = new HashMap<>();

                        if (!star) { // if star is false
                            hashMap.put("star", true); // turn true

//                          processing update note using updateNote (behavior) from dao (object) for NoteDao (class)
                            dao.updateNote(key, hashMap)
                                    .addOnSuccessListener(success -> {
                                        url.setStar(true); // set star true for UrlNoteData
                                        holder.star_check.setChecked(true); // set icon star solid
                                        Toast.makeText(holder.star_check.getContext(), "Note Starred", Toast.LENGTH_SHORT).show();}) // show toast
                                    .addOnFailureListener(error -> Toast.makeText(holder.star_check.getContext(), "Error : " + error.getMessage(), Toast.LENGTH_LONG).show()); // show toast
                        } else { // if star is true
                            hashMap.put("star", false); // turn false

//                          processing update note using updateNote (behavior) from dao (object) for NoteDao (class)
                            dao.updateNote(key, hashMap)
                                    .addOnSuccessListener(success -> {
                                        url.setStar(false); // set star false for UrlNoteData
                                        holder.star_check.setChecked(false); // set icon star regular
                                        Toast.makeText(holder.itemView.getContext(), "Star removed", Toast.LENGTH_SHORT).show(); }) // show toast
                                    .addOnFailureListener(error -> Toast.makeText(holder.star_check.getContext(), "Error : " + error.getMessage(), Toast.LENGTH_LONG).show()); // show toast
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        });

        holder.itemView.setOnClickListener(view -> { // if note clicked
            Intent intent = new Intent(holder.itemView.getContext(), ViewNoteActivity.class);
            intent.putExtra("id", model.getId()); // send extra id
            holder.itemView.getContext().startActivity(intent); // start activity to view note
        });

        holder.browse.setOnClickListener(view -> { // if button browse clicked
            Intent link = new Intent(Intent.ACTION_VIEW);

//          validation if link contain 'http'
            if (model.getUrl().contains("http")) {
                link.setData(Uri.parse(model.getUrl()));
            } else { // if link does not contain http/https, set it
                link.setData(Uri.parse("https://" + model.getUrl()));
            }
            holder.browse.getContext().startActivity(link); // go to browser
        });
    }

    @NonNull
    @Override
    public UrlManagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_url_manager, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextInputLayout link;
        Button browse;
        CheckBox star_check;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_note);
            link = itemView.findViewById(R.id.link_inputLayout);
            browse = itemView.findViewById(R.id.browse_button);
            star_check = itemView.findViewById(R.id.star_check);
        }
    }
}
