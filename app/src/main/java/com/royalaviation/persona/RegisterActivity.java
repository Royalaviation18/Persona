package com.royalaviation.persona;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etAge, etPassword, etCpassword;
    private Button btnSignup, btnGSignup;
    private TextView goToLogin;
    private ProgressDialog loadingBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();

        etName = findViewById(R.id.etUname);
        etEmail = findViewById(R.id.etUEmail);
        etPassword = findViewById(R.id.etUPassword);
        etCpassword = findViewById(R.id.etUCPassword);
        etAge = findViewById(R.id.etUAge);
        btnSignup = findViewById(R.id.btnSignUp);
        btnGSignup = findViewById(R.id.btnGSignIn);
        goToLogin = findViewById(R.id.tvLogin);
        loadingBar = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

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
                createAccount();
            }
        });

    }

    private void createAccount() {
        String uName = etName.getText().toString();
        String uEmail = etEmail.getText().toString();
        String uAge = etAge.getText().toString();
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
        if (uAge.isEmpty()) {
            etAge.setError("Please enter your age");
            etAge.requestFocus();
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

            firebaseAuth.createUserWithEmailAndPassword(uEmail, uPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        User user = new User(uName, uAge, uEmail);

                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, "Congratulations,Your Account has been created", Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                            sendEmailVerification();
                                        } else {
                                            loadingBar.dismiss();
                                            Toast.makeText(RegisterActivity.this, "Something went Wrong,Please try again later!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(RegisterActivity.this, "Failed to register", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }

    }

    private void sendEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "Verification email sent,verify and login", Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Failed to send verification email", Toast.LENGTH_SHORT).show();
        }
    }


}