package com.ddr.pansala;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminEventAdd extends AppCompatActivity {

    private EditText eventName, description, time,
            place, registerPassword, registerConfirmPassword;
    private Button registerEventBtn, datePickerBtn;
    private ImageView admin_event_add_avatar;
    private TextView date;
    private Boolean isTempleNameValid, isEmailValid, isPasswordValid, isAlreadyRegistered, isWiharadhipathiHimiNameValid,
            isTelNoValid, isAddressValid;
    private TextInputLayout dateError, pwError, confirmPwError, eventNameError, descriptionError, timeError, placeError;
    private LinearLayout dateLayout;
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
        setContentView(R.layout.activity_admin_event_add);

        auth = FirebaseAuth.getInstance();

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#800000"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        admin_event_add_avatar = (ImageView) findViewById(R.id.admin_event_add_avatar);
        eventNameError = (TextInputLayout) findViewById(R.id.register_event_name_error);
        eventName = (EditText) findViewById(R.id.register_event_name);
        descriptionError = (TextInputLayout) findViewById(R.id.register_event_description_error);
        description = (EditText) findViewById(R.id.register_event_description);
        timeError = (TextInputLayout) findViewById(R.id.register_event_time_error);
        time = (EditText) findViewById(R.id.register_event_time);
        placeError = (TextInputLayout) findViewById(R.id.register_event_place_error);
        place = (EditText) findViewById(R.id.register_event_place);
        dateError = (TextInputLayout) findViewById(R.id.register_event_date_error);
        date = (TextView) findViewById(R.id.register_event_date);
        registerEventBtn = (Button) findViewById(R.id.register_event_btn);
        progressBar = (ProgressBar) findViewById(R.id.templeRegisterProgressBar);
        dateLayout = (LinearLayout) findViewById(R.id.admin_date_picker_layout);
        datePickerBtn = (Button) findViewById(R.id.date_picker_btn);

        long unixTime = System.currentTimeMillis() / 1000L;

        datePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateDialog();
            }
        });

        registerEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        admin_event_add_avatar.setOnClickListener(new View.OnClickListener() {
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
                new SweetAlertDialog(AdminEventAdd.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Sign Out")
                        .setContentText("Do you want to sign out from the app? ")
                        .setConfirmText("Yes")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                                CommonMethods.signOut();
                                new SweetAlertDialog(AdminEventAdd.this)
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

    private void dateDialog() {
        final View dateLayout = getLayoutInflater().inflate(R.layout.date_picker, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dateLayout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                DatePicker datePicker = (DatePicker) dateLayout.findViewById(R.id.datePicker);
                date.setText(datePicker.getYear() + "-" + datePicker.getMonth() + "-" + datePicker.getDayOfMonth());
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}