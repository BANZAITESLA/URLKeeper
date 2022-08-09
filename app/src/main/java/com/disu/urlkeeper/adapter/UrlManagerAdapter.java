package com.disu.urlkeeper.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.disu.urlkeeper.R;
import com.disu.urlkeeper.activity.ViewNoteActivity;
import com.disu.urlkeeper.data.UrlNoteData;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * 06/08/2022 | 10119239 | DEA INESIA SRI UTAMI | IF6
 */

public class UrlManagerAdapter extends FirebaseRecyclerAdapter<UrlNoteData, UrlManagerAdapter.ViewHolder> {

    private Context context;

    public UrlManagerAdapter(@NonNull FirebaseRecyclerOptions<UrlNoteData> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UrlManagerAdapter.ViewHolder holder, int position, @NonNull UrlNoteData model) {
        holder.title.setText(model.getTitle());
        holder.link.setHint(model.getUrl());
        holder.last.setText(String.format("Last edited : %s", model.getLast_edited()));

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(holder.itemView.getContext(), ViewNoteActivity.class);
//            intent.putExtra("child", )
            intent.putExtra("id", model.getId());
//            intent.putExtra("url", model.getUrl());
//            intent.putExtra("short_url", model.getShort_url());
//            intent.putExtra("secret_note", model.getSecret_note());
//            intent.putExtra("visible_note", model.getVisible_note());
//            intent.putExtra("last_edited", model.getLast_edited());

//            ValueEventListener postListener = new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                        String key = dataSnapshot.getRef().getParent().getKey();
//                        intent.putExtra("key", key);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            };

            holder.itemView.getContext().startActivity(intent);
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
        TextView last;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title_note);
            link = itemView.findViewById(R.id.link_textField);
            last = itemView.findViewById(R.id.lastEdited_note);
//                    view -> itemView.getContext().startActivity(new Intent(itemView.getContext(), ViewNoteActivity.class)));
        }
    }
}
