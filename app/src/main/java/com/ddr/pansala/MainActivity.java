package com.ddr.pansala;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText loginEmail, loginPassword;
    Button loginBtn, signUpBtn;
    TextView errorMessageView;
    Boolean isEmailValid, isPasswordValid;
    String emailError = null;
    String passwordError = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginEmail = (EditText) findViewById(R.id.login_email);
        loginPassword = (EditText) findViewById(R.id.login_password);
        loginBtn = (Button) findViewById(R.id.login_btn);
        signUpBtn = (Button) findViewById(R.id.signUp_btn);

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
    }

    public void validateEmailAndPassword() {
        // Check for a valid email address.
        if (loginEmail.getText().toString().isEmpty()) {
            emailError = "Provided email is empty";
            isEmailValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(loginEmail.getText().toString()).matches()) {
            emailError = "Provided email is not valid email";
            isEmailValid = false;
        } else  {
            isEmailValid = true;
//            emailError.setErrorEnabled(false);
        }

        // Check for a valid password.
        if (loginPassword.getText().toString().isEmpty()) {
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
//            passwordError.setErrorEnabled(false);
        }

        if (!isEmailValid && !isPasswordValid) {
            showErrorDialog("Provided email and password are in invalid format");
        } else if (!isEmailValid) {
            showErrorDialog(emailError);
        } else if (!isPasswordValid) {
            showErrorDialog(passwordError);
        }

        if (isEmailValid && isPasswordValid) {
            Toast.makeText(getApplicationContext(), "Successfully", Toast.LENGTH_SHORT).show();
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