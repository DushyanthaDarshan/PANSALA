package com.ddr.pansala;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminEventAdd extends AppCompatActivity {

    private EditText eventNameFromXml, descriptionFromXml, timeFromXml,
            placeFromXml;
    private Button registerEventBtn, datePickerBtn, imageSelectBtn;
    private ImageView admin_event_add_avatar, selectedImageView;
    private TextView dateTextView;
    private Boolean isEventNameValid, isDescriptionValid, isPlaceValid, isAlreadyRegistered, isDateValid,
            isTimeValid;
    private TextInputLayout dateError, eventNameError, descriptionError, timeError, placeError;
    private LinearLayout dateLayout;
    private String eventNameErrorMessage = null;
    private String placeErrorMessage = null;
    private String timeErrorMessage = null;
    private String descriptionErrorMessage = null;
    private String dateErrorMessage = null;
    private String convertedDate;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseDatabase rootNode;
    private DatabaseReference eventReference;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;
    private FirebaseStorage storage;
    private StorageReference storageReference;
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
        eventNameFromXml = (EditText) findViewById(R.id.register_event_name);
        descriptionError = (TextInputLayout) findViewById(R.id.register_event_description_error);
        descriptionFromXml = (EditText) findViewById(R.id.register_event_description);
        timeError = (TextInputLayout) findViewById(R.id.register_event_time_error);
        timeFromXml = (EditText) findViewById(R.id.register_event_time);
        placeError = (TextInputLayout) findViewById(R.id.register_event_place_error);
        placeFromXml = (EditText) findViewById(R.id.register_event_place);
        dateError = (TextInputLayout) findViewById(R.id.register_event_date_error);
        dateTextView = (TextView) findViewById(R.id.register_event_date);
        registerEventBtn = (Button) findViewById(R.id.register_event_btn);
        progressBar = (ProgressBar) findViewById(R.id.templeRegisterProgressBar);
        dateLayout = (LinearLayout) findViewById(R.id.admin_date_picker_layout);
        datePickerBtn = (Button) findViewById(R.id.date_picker_btn);
        imageSelectBtn = (Button) findViewById(R.id.image_picker_btn);
        selectedImageView = (ImageView) findViewById(R.id.admin_event_add_post_image);

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        imageSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });

        datePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateDialog();
            }
        });

        registerEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputs();
            }
        });

        admin_event_add_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateShowAvatarDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();
            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                selectedImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    private void SelectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    private void validateInputs() {
        // Check for a valid event name
        String eventName = eventNameFromXml.getText().toString();
        if (eventName.isEmpty()) {
            eventNameErrorMessage = "Event name should not be empty";
            eventNameError.setError(eventNameErrorMessage);
            isEventNameValid = false;
        } else {
            isEventNameValid = true;
            eventNameError.setErrorEnabled(false);
        }
        // Check for a valid description
        String description = descriptionFromXml.getText().toString();
        if (description.isEmpty()) {
            descriptionErrorMessage = "Description should not be empty";
            descriptionError.setError(descriptionErrorMessage);
            isDescriptionValid = false;
        } else {
            isDescriptionValid = true;
            descriptionError.setErrorEnabled(false);
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

        if (isEventNameValid && isPlaceValid && isDateValid && isDescriptionValid && isTimeValid) {
            firebaseProcess(eventName, description, convertedDate, time, place);
        } else {
//                    alreadySignText.setTextSize(13l);
//                    alreadySign.setTextSize(13l);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void firebaseProcess(String eventName, String description, String date, String time, String place) {
        progressBar.setVisibility(View.VISIBLE);
        isAlreadyRegistered = false;

        rootNode = FirebaseDatabase.getInstance();
        eventReference = rootNode.getReference("EVENT");

        eventReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        Event event = dataSnapshot.getValue(Event.class);
                        if (event != null) {
                            if (event.getEventName().equals(eventName) && event.getEventDate().equals(date) &&
                                    event.getEventTime().equals(time)) {
                                isAlreadyRegistered = true;
                            }
                        }
                    }

                    if (!isAlreadyRegistered) {
                        FirebaseUser user = auth.getCurrentUser();
                        String key = eventReference.push().getKey();
                        long unixTime = System.currentTimeMillis() / 1000L;
                        String imageId = UUID.randomUUID().toString();
                        uploadImage(imageId);

                        Event event = new Event(user.getUid(), key, eventName, description, date,
                                time, place, imageId, user.getUid(), unixTime);
                        eventReference.child(key).setValue(event, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
//                                CommonMethods.clearSession(getApplicationContext());
//                                CommonMethods.saveSession(getApplicationContext(), event, password);
                                progressBar.setVisibility(View.GONE);
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

    // UploadImage method
    private void uploadImage(String imageId) {
        if (filePath != null) {
            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref = storageReference.child("EVENT_IMAGE/" + imageId);

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Image uploaded successfully
                    // Dismiss dialog
                    progressDialog.dismiss();
                    showSuccessDialog();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Error, Image not uploaded
                    progressDialog.dismiss();
                    showErrorDialog("Failed " + e.getMessage());
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    // Progress Listener for loading
                    // percentage on the dialog box
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int)progress + "%");
                    }
                });
        }
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
        eventNameFromXml.getText().clear();
        eventNameError.setErrorEnabled(false);
        descriptionFromXml.getText().clear();
        descriptionError.setErrorEnabled(false);
        dateTextView.setText("");
        dateError.setErrorEnabled(false);
        timeFromXml.getText().clear();
        timeError.setErrorEnabled(false);
        placeFromXml.getText().clear();
        placeError.setErrorEnabled(false);
        selectedImageView.setVisibility(View.GONE);
    }

    private void showErrorDialog(String errorMessage) {
        new SweetAlertDialog(AdminEventAdd.this, SweetAlertDialog.ERROR_TYPE)
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
                convertedDate = datePicker.getYear() + "-" + datePicker.getMonth() + "-" + datePicker.getDayOfMonth();
                dateTextView.setText(convertedDate);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}