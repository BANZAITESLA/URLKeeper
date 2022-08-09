package com.disu.urlkeeper.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.disu.urlkeeper.CustomLinearLayoutManager;
import com.disu.urlkeeper.R;
import com.disu.urlkeeper.activity.AddNoteActivity;
import com.disu.urlkeeper.adapter.UrlManagerAdapter;
import com.disu.urlkeeper.dao.UrlNoteDao;
import com.disu.urlkeeper.data.UrlNoteData;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UrlManagerFragment extends Fragment {
    RecyclerView recyclerViewNoteList;
    UrlManagerAdapter adapter;

    public UrlManagerFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_url_manager, container, false);

        recyclerViewNoteList = view.findViewById(R.id.urlNote_recylerView);
        recyclerViewNoteList.setLayoutManager(new CustomLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        FirebaseRecyclerOptions<UrlNoteData> options = new FirebaseRecyclerOptions.Builder<UrlNoteData>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("note").child("user1"), UrlNoteData.class)
                .build();

        adapter = new UrlManagerAdapter(options);
        recyclerViewNoteList.setAdapter(adapter);

//        addClicked();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton add = (FloatingActionButton) getView().findViewById(R.id.addNote_FAB);
        add.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), AddNoteActivity.class);
            startActivity(intent);
        });
    }

//    private void addClicked() {
//        add = view.findViewById(R.id.addNote_FAB);
////        add.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                Toast.makeText(getContext(), "Link copied", Toast.LENGTH_LONG).show();
////            }
////        });
//    }

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