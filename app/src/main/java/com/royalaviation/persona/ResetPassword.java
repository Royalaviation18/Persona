package com.royalaviation.persona;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    private EditText etUEmail;
    private Button btnSend;
    private TextView pvRemember;
    private ProgressDialog loadingBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        getSupportActionBar().hide();

        etUEmail = findViewById(R.id.etREmail);
        btnSend = findViewById(R.id.btnSend);
        pvRemember = findViewById(R.id.tvRemember);
        firebaseAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        pvRemember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userMail = etUEmail.getText().toString().trim();

                if (userMail.isEmpty()) {
                    etUEmail.setError("Please enter your registered mail");
                    etUEmail.requestFocus();
                    return;
                } else {
                    //Firebase Action
                    loadingBar.setTitle("Forgot Password");
                    loadingBar.setMessage("Please wait,While we are checking the credentials");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    firebaseAuth.sendPasswordResetEmail(userMail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Mail sent,check your mail to proceed", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(ResetPassword.this, CResetPassword.class));
                            } else {
                                Toast.makeText(getApplicationContext(), "Invalid Email or the account doesn't exists", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }
}