package com.disu.urlkeeper.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.disu.urlkeeper.R;
import com.disu.urlkeeper.activity.ViewNoteActivity;
import com.disu.urlkeeper.data.UrlNoteData;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputLayout;

/**
 * 06/08/2022 | 10119239 | DEA INESIA SRI UTAMI | IF6
 */

public class UrlManagerAdapter extends FirebaseRecyclerAdapter<UrlNoteData, UrlManagerAdapter.ViewHolder> {

    public UrlManagerAdapter(@NonNull FirebaseRecyclerOptions<UrlNoteData> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UrlManagerAdapter.ViewHolder holder, int position, @NonNull UrlNoteData model) {
        holder.title.setText(model.getTitle());
        holder.link.setHint(model.getUrl());
        holder.last.setText(String.format("Last edited : %s", model.getLast_edited()));
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

            itemView.setOnClickListener(view -> itemView.getContext().startActivity(new Intent(itemView.getContext(), ViewNoteActivity.class)));
        }
    }
}
