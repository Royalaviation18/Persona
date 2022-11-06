package com.royalaviation.persona;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail, loginPassword;
    private Button btnLogin;
    private TextView resetPassword, goToSignUp;
    private ProgressDialog loadingBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        loginEmail = findViewById(R.id.etEmail);
        loginPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        resetPassword = findViewById(R.id.tvForgot);
        goToSignUp = findViewById(R.id.tvRegister);
        firebaseAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ResetPassword.class);
                startActivity(intent);
            }

        });

        goToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = loginEmail.getText().toString();
                String userPassword = loginPassword.getText().toString();

                if (userEmail.isEmpty()) {
                    loginEmail.setError("Please enter your email");
                    loginEmail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    loginEmail.setError("Please provide a valid email id");
                    loginEmail.requestFocus();
                    return;
                }
                if (userPassword.isEmpty()) {
                    loginPassword.setError("Please enter your password");
                    loginPassword.requestFocus();
                    return;
                } else {
                    loadingBar.setTitle("Logging you in");
                    loadingBar.setMessage("Please wait,While we are checking the credentials");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                checkEmailVerification();
                            } else {
                                Toast.makeText(getApplicationContext(), "Account doesn't exists", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
            }
        });
    }

    private void checkEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser.isEmailVerified() == true) {
            Toast.makeText(getApplicationContext(), "Logged In", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        } else {
            Toast.makeText(getApplicationContext(), "Verify your email first!", Toast.LENGTH_SHORT).show();
        }
    }


}