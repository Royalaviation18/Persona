package com.royalaviation.persona;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etCpassword;
    private Button btnSignup;
    private TextView goToLogin;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etUname);
        etEmail = findViewById(R.id.etUEmail);
        etPassword = findViewById(R.id.etUPassword);
        etCpassword = findViewById(R.id.etUCPassword);

        btnSignup = findViewById(R.id.btnSignUp);
        goToLogin = findViewById(R.id.tvLogin);
        loadingBar=new ProgressDialog(this);

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uName = etName.getText().toString();
                String uEmail = etEmail.getText().toString();
                String uPassword = etPassword.getText().toString();
                String uCPassword = etCpassword.getText().toString();

                if (uName.isEmpty()) {
                    etName.setError("Please enter your name");
                    etName.requestFocus();
                    return;
                }
                if (uEmail.isEmpty()) {
                    etEmail.setError("Please enter your mail");
                    etEmail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(uEmail).matches()) {
                    etEmail.setError("Please provide a valid email id");
                    etEmail.requestFocus();
                    return;
                }
                if (uPassword.isEmpty()) {
                    etPassword.setError("Please enter a password");
                    etPassword.requestFocus();
                    return;
                }
                if (uPassword.length() < 6) {
                    etPassword.setError("Password should be of minimum 6 characters");
                    etPassword.requestFocus();
                    return;
                }
                if (uCPassword.isEmpty()) {
                    etCpassword.setError("Please confirm your password");
                    etCpassword.requestFocus();
                    return;
                }
                if (!uCPassword.equals(uPassword)) {
                    etCpassword.setError("Passwords do not match");
                    etCpassword.requestFocus();
                    return;
                } else {
                    loadingBar.setTitle("Creating your Account");
                    loadingBar.setMessage("Please wait,While we are checking the credentials");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                }
            }
        });

    }
}