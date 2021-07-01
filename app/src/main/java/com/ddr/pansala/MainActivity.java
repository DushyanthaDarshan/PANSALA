package com.ddr.pansala;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
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
import com.google.firebase.database.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    EditText loginEmail, loginPassword;
    Button loginBtn, signUpBtn;
    TextView errorMessageView, forgotPassword;
    Boolean isEmailValid, isPasswordValid;
    String emailError = null;
    String passwordError = null;
    private FirebaseAuth auth;

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
        forgotPassword = (TextView) findViewById(R.id.forgot_password);

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
                Toast.makeText(getApplicationContext(), "This feature will be coming soon", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
//                startActivity(intent);
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

    public void validateEmailAndPassword() {
        // Check for a valid email address.
        String email = loginEmail.getText().toString();
        if (email.isEmpty()) {
            emailError = "Provided email is empty";
            isEmailValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Provided email is not valid email";
            isEmailValid = false;
        } else  {
            isEmailValid = true;
        }

        // Check for a valid password.
        String password = loginPassword.getText().toString();
        if (password.isEmpty()) {
            passwordError = "Provided password is empty";
            isPasswordValid = false;
        } else if (loginPassword.getText().length() < 6) {
            passwordError = "Provided password is too short";
            isPasswordValid = false;
        } else if (loginPassword.getText().length() > 6) {
            passwordError = "Provided password is too long";
            isPasswordValid = false;
        } else  {
            isPasswordValid = true;
        }

        if (!isEmailValid && !isPasswordValid) {
            showErrorDialog("Provided email and password are in invalid format");
        } else if (!isEmailValid) {
            showErrorDialog(emailError);
        } else if (!isPasswordValid) {
            showErrorDialog(passwordError);
        }

        if (isEmailValid && isPasswordValid) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Successfully", Toast.LENGTH_SHORT).show();
//                        finish();
                    }
                }
            });
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
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}