package com.royalaviation.persona;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class journalDetails extends AppCompatActivity {

    private TextView journalTitle, journalContent;
    FloatingActionButton fabGotoJEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_details);

        journalTitle = findViewById(R.id.tvJournalTitle);
        journalContent = findViewById(R.id.tvJournalDesc);
        fabGotoJEdit = findViewById(R.id.goToEditJournal);
        Toolbar toolbar = findViewById(R.id.toolBarJournalDetails);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent data = getIntent();

        fabGotoJEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), editJournalActivity.class);
                intent.putExtra("title", data.getStringExtra("title"));
                intent.putExtra("content", data.getStringExtra("content"));
                intent.putExtra("jId", data.getStringExtra("jId"));
                view.getContext().startActivity(intent);
            }
        });

        journalTitle.setText(data.getStringExtra("title"));
        journalContent.setText(data.getStringExtra("content"));
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onContextItemSelected(item);
    }
}