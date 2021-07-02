package com.ddr.pansala;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    private EditText registerEmail, registerPassword, registerConfirmPassword;
    private Button registerBtn;
    private TextView errorMessageView, alreadySignin;
    private Boolean isEmailValid, isPasswordValid;
    private TextInputLayout emailErrorr, passError;
    private String emailError = null;
    private String passwordError = null;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("පන්සල");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#800000"));
        actionBar.setBackgroundDrawable(colorDrawable);


        registerEmail = (EditText) findViewById(R.id.register_email);
        registerPassword = (EditText) findViewById(R.id.register_password);
        registerConfirmPassword = (EditText) findViewById(R.id.register_confirm_password);
        registerBtn = (Button) findViewById(R.id.register_btn);
        alreadySignin = (TextView) findViewById(R.id.already_sinIn);
        emailErrorr = (TextInputLayout) findViewById(R.id.register_email_error);
        passError = (TextInputLayout) findViewById(R.id.register_password_error);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = registerEmail.getText().toString().trim();
                String password = registerPassword.getText().toString().trim();

                validateEmailAndPassword(email, password);
            }
        });
    }

    private void firebaseProcess(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                if (!task.isSuccessful()) {
                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignupActivity.this, "AAAAAAAAAAAAAAAAAAAAAAAA" + task.getException(),
                            Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(SignupActivity.this, MainActivity.class));
//                            finish();
                }
            }
        });
    }

    public void validateEmailAndPassword(String email, String password) {
        // Check for a valid email address.
        if (email.isEmpty()) {
            emailError = "Provided email is empty";
            emailErrorr.setError(emailError);
            isEmailValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Provided email is not valid email";
            emailErrorr.setError(emailError);
            isEmailValid = false;
        } else {
            isEmailValid = true;
            emailErrorr.setErrorEnabled(false);
        }

        // Check for a valid password.
        if (password.isEmpty()) {
            passwordError = "Provided password is empty";
            passError.setError(passwordError);
            isPasswordValid = false;
        } else if (password.length() < 6) {
            passwordError = "Provided password is too short";
            passError.setError(passwordError);
            isPasswordValid = false;
        } else if (password.length() > 6) {
            passwordError = "Provided password is too long";
            passError.setError(passwordError);
            isPasswordValid = false;
        } else if (!password.equals(registerConfirmPassword.getText().toString())) {
            passwordError = "Provided password is not matching";
            passError.setError(passwordError);
            isPasswordValid = false;
        } else {
            isPasswordValid = true;
            passError.setErrorEnabled(false);
        }

        if (isEmailValid && isPasswordValid) {
            firebaseProcess(email, password);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}