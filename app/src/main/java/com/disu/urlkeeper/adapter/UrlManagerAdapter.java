package com.disu.urlkeeper.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.disu.urlkeeper.R;
import com.disu.urlkeeper.activity.AddNoteActivity;
import com.disu.urlkeeper.activity.ViewNoteActivity;
import com.disu.urlkeeper.data.UrlNoteData;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
            intent.putExtra("id", model.getId());
            holder.itemView.getContext().startActivity(intent);
        });

        holder.copyLink_button.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) holder.copyLink_button.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("link", holder.link.getHint());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(holder.copyLink_button.getContext(), "Link copied", Toast.LENGTH_SHORT).show();
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
        Button copyLink_button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title_note);
            link = itemView.findViewById(R.id.link_inputLayout);
            last = itemView.findViewById(R.id.lastEdited_note);
            copyLink_button = itemView.findViewById(R.id.copyLink_button);
        }
    }
}
