package com.royalaviation.persona;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class editJournalActivity extends AppCompatActivity {

    private EditText etEditTitle, etEditContent;
    private DatePickerDialog datePickerDialog;
    private FloatingActionButton fbEditBtn;
    private Button btnJDate;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_journal);
        initDatePicker();
        etEditTitle = findViewById(R.id.etJournalEditTitle);
        etEditContent = findViewById(R.id.etEditJournalDesc);
        fbEditBtn = findViewById(R.id.btnSaveEditJournal);
        Toolbar toolbar = findViewById(R.id.toolBarEditJournal);
        btnJDate = findViewById(R.id.btnJDate);
        btnJDate.setText(getTodaysDate());
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();


        fbEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTitle = etEditTitle.getText().toString();
                String newContent = etEditContent.getText().toString();
                String date = btnJDate.getText().toString();
                if (newTitle.isEmpty()) {
                    etEditTitle.setError("Please enter a title");
                    etEditTitle.requestFocus();
                    return;
                }
                if (newContent.isEmpty()) {
                    etEditContent.setError("Please share your feelings");
                    etEditContent.requestFocus();
                    return;
                } else {
                    DocumentReference documentReference = firebaseFirestore.collection("Journal").document(firebaseUser.getUid()).collection("myJournal").document(intent.getStringExtra("jId"));
                    Map<String, Object> journal = new HashMap<>();
                    journal.put("title", newTitle);
                    journal.put("content", newContent);
                    journal.put("date", date);
                    documentReference.set(journal).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Note is updated", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(editJournalActivity.this, ActivityHome.class));

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to update", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");

        etEditTitle.setText(title);
        etEditContent.setText(content);

    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        month = month + 1;
        return makeDateString(day, month, year);

    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month += 1;
                String date = makeDateString(day, month, year);
                btnJDate.setText(date);
            }
        };
//
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        switch (month) {
            case 1:
                return "JAN";

            case 2:
                return "FEB";

            case 3:
                return "MAR";

            case 4:
                return "APR";

            case 5:
                return "MAY";

            case 6:
                return "JUN";

            case 7:
                return "JUL";

            case 8:
                return "AUG";

            case 9:
                return "SEP";

            case 10:
                return "OCT";

            case 11:
                return "NOV";

            case 12:
                return "DEC";

            default:
                return "Jan";

        }
    }

    public void OpenDatePicker(View view) {
        datePickerDialog.show();

    }

}