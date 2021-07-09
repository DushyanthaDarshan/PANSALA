package com.ddr.pansala;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private EditText loginEmail, loginPassword;
    private Button loginBtn, signUpBtn;
    private TextView errorMessageView, forgotPassword;
    private Boolean isEmailValid, isPasswordValid;
    private String emailError = null;
    private String passwordError = null;
    private TextInputLayout emailErrorTextInput, passErrorTextInput;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("පන්සල");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#800000"));
        actionBar.setBackgroundDrawable(colorDrawable);


        loginEmail = (EditText) findViewById(R.id.login_email);
        loginPassword = (EditText) findViewById(R.id.login_password);
        loginBtn = (Button) findViewById(R.id.login_btn);
        signUpBtn = (Button) findViewById(R.id.signUp_btn);
        emailErrorTextInput = (TextInputLayout) findViewById(R.id.login_email_error);
        passErrorTextInput = (TextInputLayout) findViewById(R.id.login_password_error);
        forgotPassword = (TextView) findViewById(R.id.forgot_password);
        progressBar = (ProgressBar) findViewById(R.id.login_progress_bar);

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("USER");

        //Login button related
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateEmailAndPassword();
            }
        });

        //Sign up button related
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });

        //Forgot password button related
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "This feature will be coming soon", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
//                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            CommonMethods.clearSession(getApplicationContext());
            reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                            UserRole userRole = dataSnapshot.getValue(UserRole.class);
                            populateUserType(userRole, currentUser.getUid());
                        }
                    }
                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    public void validateEmailAndPassword() {
        // Check for a valid email address.
        String email = loginEmail.getText().toString();
        if (email.isEmpty()) {
            emailError = "Email should not be empty";
            emailErrorTextInput.setError(emailError);
            isEmailValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Provided email is not valid email";
            emailErrorTextInput.setError(emailError);
            isEmailValid = false;
        } else  {
            isEmailValid = true;
            emailErrorTextInput.setErrorEnabled(false);
        }

        // Check for a valid password.
        String password = loginPassword.getText().toString();
        if (password.isEmpty()) {
            passwordError = "Password should not be empty";
            passErrorTextInput.setError(passwordError);
            isPasswordValid = false;
        } else if (password.length() < 6) {
            passwordError = "Provided password is too short";
            passErrorTextInput.setError(passwordError);
            isPasswordValid = false;
        } else if (password.length() > 6) {
            passwordError = "Provided password is too long";
            passErrorTextInput.setError(passwordError);
            isPasswordValid = false;
        } else  {
            isPasswordValid = true;
            passErrorTextInput.setErrorEnabled(false);
        }

//        if (!isEmailValid && !isPasswordValid) {
//            showErrorDialog("Provided email and password are in invalid format");
//        } else if (!isEmailValid) {
//            showErrorDialog(emailError);
//        } else if (!isPasswordValid) {
//            showErrorDialog(passwordError);
//        }

        if (isEmailValid && isPasswordValid) {
            progressBar.setVisibility(View.VISIBLE);
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        CommonMethods.clearSession(getApplicationContext());
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        UserRole userRole = dataSnapshot.getValue(UserRole.class);
                                        populateUserType(userRole, userId);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError error) {
                                    Log.w(TAG, "Failed to read value.", error.toException());
                                }
                            });
                        }
//                        finish();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        showErrorDialog("Please check your email and password");
                    }
                }
            });
        }
    }

    private void populateUserType(UserRole userRole, String userId) {
        if (userRole != null) {
            if (userRole.getUserId().equals(userId)) {
                CommonMethods.saveSession(getApplicationContext(), userRole, loginPassword.getText().toString());
                progressBar.setVisibility(View.GONE);
                String userType = userRole.getUserType();
                if (userType.equals("SUPER_ADMIN")) {
                    Intent openSuperAdminHomePage = new Intent(getApplicationContext(), SuperAdminHomePage.class);
                    showSuccessDialog(openSuperAdminHomePage);
                } else if (userType.equals("ADMIN")) {
                    Toast.makeText(getApplicationContext(), "ADMIN page will be coming soon", Toast.LENGTH_LONG).show();
                } else if (userType.equals("USER")) {
                    Toast.makeText(getApplicationContext(), "USER page will be coming soon", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "You are a not registered user", Toast.LENGTH_LONG).show();
                }
                Log.d(TAG, "Value is: " + userRole);
            }
        }
    }

    public void showSuccessDialog(Intent openActivity) {
        new SweetAlertDialog(
                this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Great!")
                .setContentText("Login Success.")
                .setConfirmText("Continue")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog
                                .dismiss();
                        startActivity(openActivity);
                    }
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Exit")
                .setContentText("Do you want to exit from the app? ")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        finish();
                        System.exit(0);
                    }
                })
                .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    public void showErrorDialog(String errorMessage) {
        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(errorMessage)
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                    }
                })
                .show();
        progressBar.setVisibility(View.GONE);
    }
}