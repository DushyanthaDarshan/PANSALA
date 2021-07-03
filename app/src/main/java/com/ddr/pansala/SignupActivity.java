package com.ddr.pansala;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import static android.content.ContentValues.TAG;

public class SignupActivity extends AppCompatActivity {

    private EditText registerEmail, registerPassword, registerConfirmPassword, registerName;
    private Button registerBtn;
    private TextView alreadySignText, alreadySign, errorMessageView;
    private Boolean isNameValid, isEmailValid, isPasswordValid, isAlreadyRegistered;
    private TextInputLayout emailErrorr, passError, nameError;
    private String emailError = null;
    private String passwordError = null;
    private String nameE = null;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;

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
        registerName = (EditText) findViewById(R.id.register_name);
        registerBtn = (Button) findViewById(R.id.register_btn);
        alreadySign = (TextView) findViewById(R.id.already_sinIn);
        alreadySignText = (TextView) findViewById(R.id.already_sinIn_name);
        nameError = (TextInputLayout) findViewById(R.id.register_name_error);
        emailErrorr = (TextInputLayout) findViewById(R.id.register_email_error);
        passError = (TextInputLayout) findViewById(R.id.register_password_error);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = registerEmail.getText().toString().trim();
                String password = registerPassword.getText().toString().trim();
                String name = registerName.getText().toString().trim();

                validateEmailAndPasswordAndName(email, password, name);
            }
        });

        alreadySign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openLoginActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(openLoginActivity);
            }
        });
    }

    private void firebaseProcess(String email, String password, String name) {
        progressBar.setVisibility(View.VISIBLE);
        isAlreadyRegistered = false;

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("USER");

        reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        UserRole userRole = dataSnapshot.getValue(UserRole.class);
                        if (userRole != null) {
                            if (userRole.getEmail().equals(email)) {
                                isAlreadyRegistered = true;
                            }
                        }
                    }

                    if (!isAlreadyRegistered) {
                        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {

                                    FirebaseUser user = auth.getCurrentUser();
                                    String key = reference.push().getKey();
                                    long unixTime = System.currentTimeMillis() / 1000L;

                                    UserRole userRole = new UserRole(user.getUid(), name, email, "USER", user.getUid(), unixTime);
                                    reference.child(key).setValue(userRole, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
                                            finish();
                                            Intent restartActivity = new Intent(getApplicationContext(), SignupActivity.class);
                                            startActivity(restartActivity);
//                                        Toast.makeText(SignupActivity.this, "user home page", Toast.LENGTH_LONG).show();
                                            //                            startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                        }
                                    });
                                } else {
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        progressBar.setVisibility(View.GONE);
                        showErrorDialog("The email address is already in use by another account");
                    }
                } else {
                    showErrorDialog("Internal server error");
                }
            }
        });
    }

    public void validateEmailAndPasswordAndName(String email, String password, String name) {
        // Check for a valid name
        if (name.isEmpty()) {
            nameE = "Name should not be empty";
            nameError.setError(nameE);
            isNameValid = false;
        } else {
            isNameValid = true;
            nameError.setErrorEnabled(false);
        }

        // Check for a valid email address.
        if (email.isEmpty()) {
            emailError = "Email should not be empty";
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
            passwordError = "Password should not be empty";
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

        if (isEmailValid && isPasswordValid && isNameValid) {
            firebaseProcess(email, password, name);
        } else {
            alreadySignText.setTextSize(13l);
            alreadySign.setTextSize(13l);
            progressBar.setVisibility(View.GONE);
        }
    }

    public void showErrorDialog(String errorMessage) {
        final View errorMessageLayout = getLayoutInflater().inflate(R.layout.display_error_message, null);
        errorMessageView = (TextView) errorMessageLayout.findViewById(R.id.error_message);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Html.fromHtml("<font color='#F11D1D'>Error</font>"));
        errorMessageView.setText(errorMessage);
        builder.setView(errorMessageLayout);

        builder.setPositiveButton(Html.fromHtml("<font color='#F11D1D'>OK</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
                Intent restartActivity = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(restartActivity);
            }
        });

        progressBar.setVisibility(View.GONE);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        progressBar.setVisibility(View.GONE);
//    }
}