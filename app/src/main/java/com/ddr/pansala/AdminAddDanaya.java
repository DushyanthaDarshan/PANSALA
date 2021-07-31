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
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminAddDanaya extends AppCompatActivity {

    private EditText userNameFromXml, timeFromXml, placeFromXml;
    private Button registerDanayaBtn, datePickerBtn;
    private ImageView admin_add_danaya_avatar;
    private TextView dateTextView;
    private Boolean isUserNameValid, isPlaceValid, isAlreadyRegistered, isDateValid, isTimeValid;
    private TextInputLayout dateError, userNameError, timeError, placeError;
    private LinearLayout dateLayout;
    private String userNameErrorMessage = null;
    private String placeErrorMessage = null;
    private String timeErrorMessage = null;
    private String dateErrorMessage = null;
    private String convertedDate;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseDatabase rootNode;
    private DatabaseReference eventReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_danaya);

        auth = FirebaseAuth.getInstance();

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#800000"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        admin_add_danaya_avatar = (ImageView) findViewById(R.id.admin_danaya_add_avatar);
        userNameError = (TextInputLayout) findViewById(R.id.register_danaya_user_name_error);
        userNameFromXml = (EditText) findViewById(R.id.register_danaya_user_name);
        timeError = (TextInputLayout) findViewById(R.id.register_danaya_time_error);
        timeFromXml = (EditText) findViewById(R.id.register_danaya_time);
        placeError = (TextInputLayout) findViewById(R.id.register_danaya_place_error);
        placeFromXml = (EditText) findViewById(R.id.register_danaya_place);
        dateError = (TextInputLayout) findViewById(R.id.register_danaya_date_error);
        dateTextView = (TextView) findViewById(R.id.register_danaya_date);
        registerDanayaBtn = (Button) findViewById(R.id.register_danaya_btn);
        progressBar = (ProgressBar) findViewById(R.id.admin_add_danaya_progressBar);
        dateLayout = (LinearLayout) findViewById(R.id.admin_add_danaya_date_picker_layout);
        datePickerBtn = (Button) findViewById(R.id.admin__add_danaya_date_picker_btn);

        datePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateDialog();
            }
        });

        registerDanayaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputs();
            }
        });

        admin_add_danaya_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateShowAvatarDialog();
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
                new SweetAlertDialog(AdminAddDanaya.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Sign Out")
                        .setContentText("Do you want to sign out from the app? ")
                        .setConfirmText("Yes")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                                CommonMethods.signOut();
                                new SweetAlertDialog(AdminAddDanaya.this)
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

    private void validateInputs() {
        // Check for a valid event name
        String userName = userNameFromXml.getText().toString();
        if (userName.isEmpty()) {
            userNameErrorMessage = "User name should not be empty";
            userNameError.setError(userNameErrorMessage);
            isUserNameValid = false;
        } else {
            isUserNameValid = true;
            userNameError.setErrorEnabled(false);
        }
        // Check for a valid date
        if (convertedDate.isEmpty()) {
            dateErrorMessage = "Date should not be empty";
            dateError.setError(dateErrorMessage);
            isDateValid = false;
        } else {
            isDateValid = true;
            dateError.setErrorEnabled(false);
        }
        // Check for a valid time
        String time = timeFromXml.getText().toString();
        if (time.isEmpty()) {
            timeErrorMessage = "Time should not be empty";
            timeError.setError(timeErrorMessage);
            isTimeValid = false;
        } else {
            isTimeValid = true;
            timeError.setErrorEnabled(false);
        }
        // Check for a valid place
        String place = placeFromXml.getText().toString();
        if (place.isEmpty()) {
            placeErrorMessage = "Place should not be empty";
            placeError.setError(placeErrorMessage);
            isPlaceValid = false;
        } else {
            isPlaceValid = true;
            placeError.setErrorEnabled(false);
        }

        if (isUserNameValid && isPlaceValid && isDateValid && isTimeValid) {
            firebaseProcess(userName, convertedDate, time, place);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void firebaseProcess(String username, String date, String time, String place) {
        progressBar.setVisibility(View.VISIBLE);
        isAlreadyRegistered = false;

        rootNode = FirebaseDatabase.getInstance();
        eventReference = rootNode.getReference("EVENT");

        eventReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        Event danaya = dataSnapshot.getValue(Event.class);
                        if (danaya != null) {
                            if (danaya.getUsername().equals(username) && danaya.getEventDate().equals(date) &&
                                    danaya.getEventTime().equals(time)) {
                                isAlreadyRegistered = true;
                            }
                        }
                    }

                    if (!isAlreadyRegistered) {
                        FirebaseUser user = auth.getCurrentUser();
                        String key = eventReference.push().getKey();
                        long unixTime = System.currentTimeMillis() / 1000L;

                        Event danaya = new Event("NO", key, "danaya", "danaya", date, time, place, "NO", user.getUid(), unixTime);
                        danaya.setUsername(username);
                        eventReference.child(key).setValue(danaya, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
                                progressBar.setVisibility(View.GONE);
                                showSuccessDialog();
                            }
                        });
                    } else {
                        progressBar.setVisibility(View.GONE);
                        showErrorDialog("The event is already added");
                    }
                } else {
                    showErrorDialog("Internal server error");
                }
            }
        });
    }

    private void showErrorDialog(String errorMessage) {
        new SweetAlertDialog(AdminAddDanaya.this, SweetAlertDialog.ERROR_TYPE)
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

    private void showSuccessDialog() {
        clearAllEditFields();
        new SweetAlertDialog(
                this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Great!")
                .setContentText("Event successfully added.")
                .setConfirmText("Continue")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog
                                .dismiss();
                    }
                })
                .show();
    }

    private void clearAllEditFields() {
        userNameFromXml.getText().clear();
        userNameError.setErrorEnabled(false);
        timeFromXml.getText().clear();
        timeError.setErrorEnabled(false);
        dateTextView.setText("");
        dateError.setErrorEnabled(false);
        timeFromXml.getText().clear();
        timeError.setErrorEnabled(false);
        placeFromXml.getText().clear();
        placeError.setErrorEnabled(false);
    }

    private void dateDialog() {
        final View dateLayout = getLayoutInflater().inflate(R.layout.date_picker, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dateLayout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                DatePicker datePicker = (DatePicker) dateLayout.findViewById(R.id.datePicker);
                convertedDate = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth();
                dateTextView.setText(convertedDate);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}