package com.royalaviation.persona;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;

import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.text.nlclassifier.NLClassifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class createJournal extends AppCompatActivity {
    private DatePickerDialog datePickerDialog;
    private Button btn_date, btnPredict;
    private FloatingActionButton fb_save, fb_jr_Mic, btnImage;
    private EditText et_title, et_content;
    private TextView tvResult;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    static final int REQUEST_CODE_SPEECH_INPUT = 1;
    Timestamp timestamp;
    //changes
    private NLClassifier textClassifier;
    private ExecutorService executorService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_journal);
        getSupportActionBar().hide();
        initDatePicker();
        executorService = Executors.newSingleThreadExecutor();
        btn_date = findViewById(R.id.btn_date);
        btn_date.setText(getTodaysDate());
        btnImage = findViewById(R.id.btnImagetoText);
        et_title = findViewById(R.id.et_jr_title);
        et_content = findViewById(R.id.et_jr_content);
        fb_save = findViewById(R.id.fb_save);
        fb_jr_Mic = findViewById(R.id.fb_jr_Mic);
        btnPredict = findViewById(R.id.btnAnalyze);
        tvResult = findViewById(R.id.tvResult);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        Intent intent = getIntent();
        String data = intent.getStringExtra("content");
        et_content.setText(data);
        //change
        btnPredict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                classify(et_content.getText().toString());
            }
        });
        //change
        downloadModel("sentiment_analysis");

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), uploadImage.class));
            }
        });


        fb_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //saveNote();

                String title = et_title.getText().toString();
                String content = et_content.getText().toString();
                String date = btn_date.getText().toString();

                if (title.isEmpty()) {
                    et_title.setError("Please enter the title");
                    et_title.requestFocus();
                    return;
                }
                if (content.isEmpty()) {
                    et_content.setError("Please enter something");
                    et_content.requestFocus();
                    return;
                } else {
                    DocumentReference documentReference = firebaseFirestore.collection("Journal").document(firebaseUser.getUid()).collection("myJournal").document();
                    Map<String, Object> note = new HashMap<>();
                    note.put("date", date);
                    note.put("title", title);
                    note.put("content", content);

                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Note saved successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(createJournal.this, ActivityHome.class));
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to create note", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });


        fb_jr_Mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent
                        = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                        Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

                try {
                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                } catch (Exception e) {
                    Toast
                            .makeText(createJournal.this, " " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private void classify(String text) {
        executorService.execute(
                () -> {
                    Log.d("s", text);
                    // TODO 7: Run sentiment analysis on the input text
                    List<Category> results = textClassifier.classify(text);

                    // TODO 8: Convert the result to a human-readable text
                    String textToShow = "Input: " + text + "\nOutput:\n";
                    for (int i = 0; i < results.size(); i++) {
                        Category result = results.get(i);
                        textToShow +=
                                String.format("    %s: %s\n", result.getLabel(), result.getScore());
                    }
                    textToShow += "---------\n";

                    // Show classification result on screen
                    showResult(textToShow);
                });
    }

    private void showResult(final String textToShow) {
        // Run on UI thread as we'll updating our app UI
        runOnUiThread(
                () -> {
                    // Append the result to the UI.
                    tvResult.append(textToShow);

                    // Clear the input text.
//                    inputEditText.getText().clear();

                    // Scroll to the bottom to show latest entry's classification result.
//                    scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                });
    }

    /**
     * Download model from Firebase ML.
     */
    private synchronized void downloadModel(String modelName) {
        CustomModelDownloadConditions conditions = new CustomModelDownloadConditions.Builder()
                .requireWifi()
                .build();
        FirebaseModelDownloader.getInstance()
                .getModel("sentiment_analysis", DownloadType.LOCAL_MODEL, conditions)
                .addOnSuccessListener(model -> {
                    try {
                        // TODO 6: Initialize a TextClassifier with the downloaded model
                        textClassifier = NLClassifier.createFromFile(model.getFile());
                        btnPredict.setEnabled(true);
                    } catch (IOException e) {
                        Toast.makeText(
                                        createJournal.this,
                                        "Model initialization failed.",
                                        Toast.LENGTH_LONG)
                                .show();
                        btnPredict.setEnabled(false);
                    }
                })
                .addOnFailureListener(e -> {
                            Toast.makeText(
                                            createJournal.this,
                                            "Model download failed, please check your connection.",
                                            Toast.LENGTH_LONG)
                                    .show();

                        }
                );
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
                btn_date.setText(date);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                et_content.setText(
                        Objects.requireNonNull(result).get(0));
            }
        }
    }
}