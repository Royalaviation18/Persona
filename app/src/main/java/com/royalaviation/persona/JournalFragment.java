package com.royalaviation.persona;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.DateFormat;
import java.util.Calendar;

public class JournalFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private FloatingActionButton fb_add;
    RecyclerView jrrecylerView;
    LinearLayoutManager linearLayoutManager;
    String jId;
    String mTitle, mContent, mDate;
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

        private TextView tvJDate, tvJTitle;

        public journalViewHolder(@NonNull View itemView) {
            super(itemView);

            tvJDate = itemView.findViewById(R.id.tvJDate);
            tvJTitle = itemView.findViewById(R.id.tvJTitle);

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
                holder.tvJDate.setText(model.getDate());
                holder.tvJTitle.setText(model.getTitle());
                mTitle = model.getTitle().toString();
                mContent = model.getContent().toString();
                mDate = model.getDate().toString();
                jId = journalAdapter.getSnapshots().getSnapshot(position).getId();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), journalDetails.class);
                        intent.putExtra("title", model.getTitle());
                        intent.putExtra("content", model.getContent());
                        intent.putExtra("date", model.getDate());
                        intent.putExtra("jId", jId);
                        view.getContext().startActivity(intent);
                        registerForContextMenu(holder.itemView);
                    }
                });
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

    //Context Menu
    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.jEdit:
                Intent intent = new Intent(getActivity(), editJournalActivity.class);
                intent.putExtra("title", mTitle);
                intent.putExtra("content", mContent);
                intent.putExtra("date", mDate);
                intent.putExtra("jId", jId);
                startActivity(intent);
                return true;

            case R.id.jDelete:
                DocumentReference documentReference = firebaseFirestore.collection("Journal").document(firebaseUser.getUid()).collection("myJournal").document(jId);
                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "The Journal is deleted", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                return true;

            default:
                return super.onContextItemSelected(item);
        }
//        return super.onContextItemSelected(item);
    }
}

