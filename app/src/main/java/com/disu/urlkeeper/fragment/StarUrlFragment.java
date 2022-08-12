package com.disu.urlkeeper.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.disu.urlkeeper.R;
import com.disu.urlkeeper.adapter.UrlManagerAdapter;
import com.disu.urlkeeper.customization.CustomLinearLayoutManager;
import com.disu.urlkeeper.data.UrlNoteData;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class StarUrlFragment extends Fragment {

    RecyclerView recyclerViewNoteList;
    UrlManagerAdapter adapter;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    public StarUrlFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_star_url, container, false);

//      initiate database reference
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

//      binding & set custom linear layout
        recyclerViewNoteList = view.findViewById(R.id.urlStar_recylerView);
        recyclerViewNoteList.setLayoutManager(new CustomLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

//      get all data for firebase recycler options
        FirebaseRecyclerOptions<UrlNoteData> options = new FirebaseRecyclerOptions.Builder<UrlNoteData>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("note").child(mCurrentUser.getUid())
                        .orderByChild("star").equalTo(true), UrlNoteData.class)
                .build();

//      set adapter
        adapter = new UrlManagerAdapter(options);
        recyclerViewNoteList.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening(); // star event listening for firebase
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening(); // stop event listening for firebase
    }
}