package com.royalaviation.persona;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ResetPassword extends AppCompatActivity {

    private EditText etUEmail;
    private Button btnSend;
    private TextView pvRemember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        getSupportActionBar().hide();

        etUEmail = findViewById(R.id.etREmail);
        btnSend = findViewById(R.id.btnSend);
        pvRemember = findViewById(R.id.tvRemember);

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
                }
            }
        });
    }
}