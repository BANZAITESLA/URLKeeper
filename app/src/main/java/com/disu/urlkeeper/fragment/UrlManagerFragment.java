package com.disu.urlkeeper.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.disu.urlkeeper.R;
import com.disu.urlkeeper.activity.AddNoteActivity;
import com.disu.urlkeeper.adapter.UrlManagerAdapter;
import com.disu.urlkeeper.customization.CustomLinearLayoutManager;
import com.disu.urlkeeper.data.UrlNoteData;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class UrlManagerFragment extends Fragment {

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_url_manager, container, false);

//      initiate database reference
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

//      binding & set custom linear layout
        recyclerViewNoteList = view.findViewById(R.id.urlNote_recylerView);
        recyclerViewNoteList.setLayoutManager(new CustomLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

//      get all data for firebase recycler options
        FirebaseRecyclerOptions<UrlNoteData> options = new FirebaseRecyclerOptions.Builder<UrlNoteData>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("note").child(mCurrentUser.getUid()), UrlNoteData.class)
                .build();

//      set adapter
        adapter = new UrlManagerAdapter(options);
        recyclerViewNoteList.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        FloatingActionButton add = getView().findViewById(R.id.addNote_FAB);
        add.setOnClickListener(view1 -> { // if FAB clicked
            Intent intent = new Intent(getActivity(), AddNoteActivity.class); // go to add note activity
            startActivity(intent);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening(); // start event listening
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening(); // stop event listening
    }
}