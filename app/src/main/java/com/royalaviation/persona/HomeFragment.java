package com.royalaviation.persona;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment {
    FloatingActionButton btnActFloat;
    RecyclerView mRecylerView;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    FirestoreRecyclerAdapter<firebasemodel, NotesActivity.NoteViewHolder> noteAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnActFloat = getView().findViewById(R.id.btnAct2);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        btnActFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),CreateNote.class);
                startActivity(intent);
            }
        });


        mRecylerView = getActivity().findViewById(R.id.recyclerView);
        mRecylerView.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecylerView.setLayoutManager(staggeredGridLayoutManager);


    }

    private int getRandomColor() {
        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.gray);
        colorCode.add(R.color.pink);
        colorCode.add(R.color.lightGreen);
        colorCode.add(R.color.skyBlue);
        colorCode.add(R.color.yellow);
        colorCode.add(R.color.color2);
        colorCode.add(R.color.color3);
        colorCode.add(R.color.color4);
        colorCode.add(R.color.color5);
        colorCode.add(R.color.green);

        Random random = new Random();
        int number = random.nextInt(colorCode.size());
        return colorCode.get(number);
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        public TextView noteTitle;
        public TextView noteContent;
        LinearLayout mNote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            noteTitle = itemView.findViewById(R.id.tvNotesTitle);
            noteContent = itemView.findViewById(R.id.tvNotesContent);
            mNote = itemView.findViewById(R.id.llNote);

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").orderBy("title", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<firebasemodel> allusernotes = new FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(query, firebasemodel.class).build();

        noteAdapter = new FirestoreRecyclerAdapter<firebasemodel, NotesActivity.NoteViewHolder>(allusernotes) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull NotesActivity.NoteViewHolder holder, int position, @NonNull firebasemodel model) {

                ImageView popupButton = holder.itemView.findViewById(R.id.ivMenuPop);


                int colourCode = getRandomColor();
                holder.mNote.setBackgroundColor(holder.itemView.getResources().getColor(colourCode, null));

                holder.noteTitle.setText(model.getTitle());
                holder.noteContent.setText(model.getContent());

                String docId = noteAdapter.getSnapshots().getSnapshot(position).getId();

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), noteDetails.class);
                        intent.putExtra("title", model.getTitle());
                        intent.putExtra("content", model.getContent());
                        intent.putExtra("noteId", docId);
                        view.getContext().startActivity(intent);
                    }
                });

                popupButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                        popupMenu.setGravity(Gravity.END);
                        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                Intent intent = new Intent(view.getContext(), editNoteActivity.class);
                                intent.putExtra("title", model.getTitle());
                                intent.putExtra("content", model.getContent());
                                intent.putExtra("noteId", docId);
                                view.getContext().startActivity(intent);
                                return false;
                            }
                        });

                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(docId);
                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(view.getContext(), "The note is deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(view.getContext(), "Failed to deleted", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }

            @NonNull
            @Override
            public NotesActivity.NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout, parent, false);
                return new NotesActivity.NoteViewHolder(view);
            }
        };
        mRecylerView.setAdapter(noteAdapter);
        noteAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (noteAdapter != null) {
            noteAdapter.stopListening();
        }
    }
}
