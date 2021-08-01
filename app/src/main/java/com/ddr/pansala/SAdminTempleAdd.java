package com.ddr.pansala;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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

import org.jetbrains.annotations.NotNull;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * author : Dushyantha Darshan Rubasinghe
 */
public class SAdminTempleAdd extends AppCompatActivity {

    private EditText registerTempleName, registerWiharadhipathiHimi, templeTelNo,
            registerTempleAddress, registerTempleEmail, registerPassword, registerConfirmPassword;
    private Button registerTempleBtn;
    private ImageView avatarImage;
    private TextView hiText;
    private Boolean isTempleNameValid, isEmailValid, isPasswordValid, isAlreadyRegistered, isWiharadhipathiHimiNameValid,
            isTelNoValid, isAddressValid;
    private TextInputLayout emailError, pwError, confirmPwError, templeNameError, wiharadhipathiError, templeTelNoError, addressError;
    private String emailErrorMessage = null;
    private String passwordErrorMessage = null;
    private String templeNameErrorMessage = null;
    private String wiharadhipathiErrorMessage = null;
    private String telephoneNoErrorMessage = null;
    private String addressErrorMessage = null;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseDatabase rootNode;
    private DatabaseReference userReference;
    private DatabaseReference templeReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sadmin_temple_add);

        auth = FirebaseAuth.getInstance();

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#800000"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        hiText = (TextView) findViewById(R.id.s_admin_temple_add_hi_text);
        avatarImage = (ImageView) findViewById(R.id.s_admin_temple_add_avatar);
        templeNameError = (TextInputLayout) findViewById(R.id.register_temple_name_error);
        registerTempleName = (EditText) findViewById(R.id.register_temple_name);
        wiharadhipathiError = (TextInputLayout) findViewById(R.id.register_wiharadhipathi_himi_error);
        registerWiharadhipathiHimi = (EditText) findViewById(R.id.register_wiharadhipathi_himi_name);
        templeTelNoError = (TextInputLayout) findViewById(R.id.register_temple_tel_error);
        templeTelNo = (EditText) findViewById(R.id.register_temple_tel);
        addressError = (TextInputLayout) findViewById(R.id.register_temple_address_error);
        registerTempleAddress = (EditText) findViewById(R.id.register_temple_address);
        addressError = (TextInputLayout) findViewById(R.id.register_temple_address_error);
        registerTempleAddress = (EditText) findViewById(R.id.register_temple_address);
        emailError = (TextInputLayout) findViewById(R.id.register_temple_email_error);
        registerTempleEmail = (EditText) findViewById(R.id.register_temple_email);
        pwError = (TextInputLayout) findViewById(R.id.register_temple_pw_error);
        registerPassword = (EditText) findViewById(R.id.register_temple_pw);
        confirmPwError = (TextInputLayout) findViewById(R.id.register_temple_confirm_pw_error);
        registerConfirmPassword = (EditText) findViewById(R.id.register_temple_confirm_pw);
        registerTempleBtn = (Button) findViewById(R.id.register_tmple_btn);
        progressBar = (ProgressBar) findViewById(R.id.templeRegisterProgressBar);

        String name = CommonMethods.getName();
        hiText.setText("ආයුබෝවන් " + name);

        long unixTime = System.currentTimeMillis() / 1000L;

        registerTempleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Temple temple = new Temple(null, registerTempleName.getText().toString(), registerTempleEmail.getText().toString(),
                        "ADMIN", name, unixTime, registerTempleName.getText().toString(), registerWiharadhipathiHimi.getText().toString(),
                        templeTelNo.getText().toString(), registerTempleAddress.getText().toString());
                validateTempleAttributes(temple);
            }
        });

        avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateShowAvatarDialog();
            }
        });
    }

    public void validateTempleAttributes(Temple temple) {
        // Check for a valid temple name
        if (temple.getTempleName().isEmpty()) {
            templeNameErrorMessage = "Temple name should not be empty";
            templeNameError.setError(templeNameErrorMessage);
            isTempleNameValid = false;
        } else {
            isTempleNameValid = true;
            templeNameError.setErrorEnabled(false);
        }

        // Check for a valid wiharadhipathi name
        if (temple.getTelNo().isEmpty()) {
            wiharadhipathiErrorMessage = "Wiharadhipathi himi name should not be empty";
            wiharadhipathiError.setError(wiharadhipathiErrorMessage);
            isWiharadhipathiHimiNameValid = false;
        } else {
            isWiharadhipathiHimiNameValid = true;
            wiharadhipathiError.setErrorEnabled(false);
        }

        // Check for a valid telephone no
        if (temple.getTelNo().isEmpty()) {
            telephoneNoErrorMessage = "Telephone number should not be empty";
            templeTelNoError.setError(telephoneNoErrorMessage);
            isTelNoValid = false;
        } else {
            isTelNoValid = true;
            templeTelNoError.setErrorEnabled(false);
        }

        // Check for a valid address
        if (temple.getTempleAddress().isEmpty()) {
            addressErrorMessage = "Temple address should not be empty";
            addressError.setError(addressErrorMessage);
            isAddressValid = false;
        } else {
            isAddressValid = true;
            addressError.setErrorEnabled(false);
        }

        // Check for a valid email address.
        String email = temple.getEmail();
        if (email.isEmpty()) {
            emailErrorMessage = "Email should not be empty";
            emailError.setError(emailErrorMessage);
            isEmailValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailErrorMessage = "Provided email is not valid email";
            emailError.setError(emailErrorMessage);
            isEmailValid = false;
        } else {
            isEmailValid = true;
            emailError.setErrorEnabled(false);
        }

        // Check for a valid password.
        String pw = registerPassword.getText().toString();
        if (pw.isEmpty()) {
            passwordErrorMessage = "Password should not be empty";
            pwError.setError(passwordErrorMessage);
            isPasswordValid = false;
        } else if (pw.length() < 6) {
            passwordErrorMessage = "Provided password is too short";
            pwError.setError(passwordErrorMessage);
            isPasswordValid = false;
        } else if (pw.length() > 6) {
            passwordErrorMessage = "Provided password is too long";
            pwError.setError(passwordErrorMessage);
            isPasswordValid = false;
        } else if (!pw.equals(registerConfirmPassword.getText().toString())) {
            passwordErrorMessage = "Provided password is not matching";
            pwError.setError(passwordErrorMessage);
            isPasswordValid = false;
        } else {
            isPasswordValid = true;
            pwError.setErrorEnabled(false);
        }

        if (isEmailValid && isPasswordValid && isTempleNameValid && isTelNoValid && isWiharadhipathiHimiNameValid && isAddressValid) {
            firebaseProcess(temple, pw);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void firebaseProcess(Temple temple, String password) {
        progressBar.setVisibility(View.VISIBLE);
        isAlreadyRegistered = false;
        String email = temple.getEmail();

        rootNode = FirebaseDatabase.getInstance();
        userReference = rootNode.getReference("USER");
        templeReference = rootNode.getReference("TEMPLE");

        userReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SAdminTempleAdd.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {

                                    FirebaseUser user = auth.getCurrentUser();
                                    String key1 = userReference.push().getKey();
                                    String key2 = userReference.push().getKey();

                                    UserRole userRole = new UserRole(user.getUid(), temple.getTempleName(),
                                            email, temple.getUserType(), temple.getCreatedBy(), temple.getCreatedTimestamp());
                                    userReference.child(key1).setValue(userRole, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
                                        }
                                    });
                                    temple.setUserId(user.getUid());
                                    templeReference.child(key2).setValue(temple, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
                                            reLogin();
                                            clearAllEditFields();
                                            showSuccessDialog();
                                        }
                                    });
                                } else {
                                    Toast.makeText(SAdminTempleAdd.this, "Authentication failed." + task.getException(),
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

    public void clearAllEditFields() {
        registerTempleName.getText().clear();
        templeNameError.setErrorEnabled(false);
        registerWiharadhipathiHimi.getText().clear();
        wiharadhipathiError.setErrorEnabled(false);
        templeTelNo.getText().clear();
        templeTelNoError.setErrorEnabled(false);
        registerTempleAddress.getText().clear();
        addressError.setErrorEnabled(false);
        registerTempleEmail.getText().clear();
        emailError.setErrorEnabled(false);
        registerPassword.getText().clear();
        pwError.setErrorEnabled(false);
        registerConfirmPassword.getText().clear();
        confirmPwError.setErrorEnabled(false);
    }

    public void showErrorDialog(String errorMessage) {
        new SweetAlertDialog(SAdminTempleAdd.this, SweetAlertDialog.ERROR_TYPE)
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

    public void showSuccessDialog() {
        new SweetAlertDialog(
                this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Great!...")
                .setContentText("Registration Success.")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog
                                .dismiss();
                    }
                })
                .show();
    }

    public void reLogin() {
        String email = CommonMethods.getEmailFromSession();
        String password = CommonMethods.getPasswordFromSession();
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @com.google.firebase.database.annotations.NotNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                }
            }
        });
    }

    private void populateShowAvatarDialog() {
        final View avatarLayout = getLayoutInflater().inflate(R.layout.avatar_dialog, null);
        TextView logOutText = (TextView) avatarLayout.findViewById(R.id.logout_text);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Account");
        builder.setView(avatarLayout);

        logOutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(SAdminTempleAdd.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Sign Out")
                        .setContentText("Do you want to sign out from the app? ")
                        .setConfirmText("Yes")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                                CommonMethods.signOut();
                                new SweetAlertDialog(SAdminTempleAdd.this)
                                        .setTitleText("තෙරුවන් සරණයි !")
                                        .show();

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent openSignInPage = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(openSignInPage);
                                    }
                                }, 1000);
                            }
                        })
                        .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}