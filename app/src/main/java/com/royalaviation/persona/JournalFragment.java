package com.royalaviation.persona;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.DateFormat;
import java.util.Calendar;

public class JournalFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private FloatingActionButton fb_add;
    RecyclerView jrrecylerView;
    LinearLayoutManager linearLayoutManager;

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    FirestoreRecyclerAdapter<firebasemodel, JournalFragment.journalViewHolder> journalAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fb_add = getView().findViewById(R.id.fb_add);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();


        fb_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), createJournal.class));
            }
        });
        //Onstart

        jrrecylerView = getView().findViewById(R.id.rv_journal);
        jrrecylerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        jrrecylerView.setLayoutManager(linearLayoutManager);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_journal, container, false);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        String currentDateString = DateFormat.getDateInstance().format(c.getTime());

    }

    public static class journalViewHolder extends RecyclerView.ViewHolder {

        private TextView jrTitle;

        public journalViewHolder(@NonNull View itemView) {
            super(itemView);

            jrTitle = itemView.findViewById(R.id.jrtitle);

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = firebaseFirestore.collection("Journal").document(firebaseUser.getUid()).collection("myJournal").orderBy("date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<firebasemodel> alljournal = new FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(query, firebasemodel.class).build();

        journalAdapter = new FirestoreRecyclerAdapter<firebasemodel, journalViewHolder>(alljournal) {
            @Override
            protected void onBindViewHolder(@NonNull journalViewHolder holder, int position, @NonNull firebasemodel model) {
                holder.jrTitle.setText(model.getTitle());
            }

            @NonNull
            @Override
            public journalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.journal_layout, parent, false);
                return new journalViewHolder(view);
            }
        };

        jrrecylerView.setAdapter(journalAdapter);
        journalAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (journalAdapter != null) {
            journalAdapter.stopListening();
        }
    }
}

