package com.disu.urlkeeper.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.disu.urlkeeper.customization.CustomLinearLayoutManager;
import com.disu.urlkeeper.R;
import com.disu.urlkeeper.activity.AddNoteActivity;
import com.disu.urlkeeper.adapter.UrlManagerAdapter;
import com.disu.urlkeeper.dao.NoteDao;
import com.disu.urlkeeper.data.UrlNoteData;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UrlManagerFragment extends Fragment {

    Query databaseReference;
    UrlNoteData url;
    NoteDao dao = new NoteDao();
    RecyclerView recyclerViewNoteList;
    UrlManagerAdapter adapter;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    public UrlManagerFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_url_manager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        FloatingActionButton add = (FloatingActionButton) getView().findViewById(R.id.addNote_FAB);
        add.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), AddNoteActivity.class);
            startActivity(intent);
        });

        TextInputLayout search = (TextInputLayout) getView().findViewById(R.id.search_textField);
        TextInputEditText text = (TextInputEditText) getView().findViewById(R.id.search_text);

        recyclerViewNoteList = view.findViewById(R.id.urlNote_recylerView);
        recyclerViewNoteList.setLayoutManager(new CustomLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        FirebaseRecyclerOptions<UrlNoteData> options = new FirebaseRecyclerOptions.Builder<UrlNoteData>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("note").child(mCurrentUser.getUid()), UrlNoteData.class)
                .build();

        search.setEndIconOnClickListener(view12 -> {
            if (!text.getText().equals("")) {
                Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
                text.setText("l");
                adapter = new UrlManagerAdapter(options);
                recyclerViewNoteList.setAdapter(adapter);
//                    recyclerViewNoteList = view.findViewById(R.id.urlNote_recylerView);
//                    recyclerViewNoteList.setLayoutManager(new CustomLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//
//                    FirebaseRecyclerOptions<UrlNoteData> options = new FirebaseRecyclerOptions.Builder<UrlNoteData>()
//                            .setQuery(FirebaseDatabase.getInstance().getReference().child("note").child("user1"), UrlNoteData.class)
//                            .build();
//
//                    adapter = new UrlManagerAdapter(options);
//                    recyclerViewNoteList.setAdapter(adapter);
            } else {
                Toast.makeText(getContext(), "Savsed!", Toast.LENGTH_SHORT).show();
                FirebaseRecyclerOptions<UrlNoteData> options1 = new FirebaseRecyclerOptions.Builder<UrlNoteData>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("note").child(mCurrentUser.getUid()).orderByChild("title").equalTo(text.getText().toString()), UrlNoteData.class)
                        .build();

                adapter = new UrlManagerAdapter(options1);
                recyclerViewNoteList.setAdapter(adapter);
//                    recyclerViewNoteList = view.findViewById(R.id.urlNote_recylerView);
//                    recyclerViewNoteList.setLayoutManager(new CustomLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//
//                    FirebaseRecyclerOptions<UrlNoteData> options = new FirebaseRecyclerOptions.Builder<UrlNoteData>()
//                            .setQuery(FirebaseDatabase.getInstance().getReference().child("note").child("user1").orderByChild("title").equalTo(text.getText().toString()), UrlNoteData.class)
//                            .build();
//
//                    adapter = new UrlManagerAdapter(options);
//                    recyclerViewNoteList.setAdapter(adapter);
            }
        });
        adapter = new UrlManagerAdapter(options);
        recyclerViewNoteList.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}