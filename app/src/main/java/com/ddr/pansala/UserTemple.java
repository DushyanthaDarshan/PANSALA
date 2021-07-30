package com.ddr.pansala;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UserTemple extends AppCompatActivity {

    private ProgressBar progressBar;
    private FirebaseDatabase rootNode;
    private DatabaseReference userReference;
    private DatabaseReference templeReference;
    private DatabaseReference imageReference;
    private Boolean isSelected;
    private FirebaseAuth auth;
    private String userPreferenceTempleId;
    private Temple temple;
    private TextView descriptionTextView;
    private List<Bitmap> listOfImages;
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_temple);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#800000"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        TextView hiText = (TextView) findViewById(R.id.user_temple_hi_text);
        ImageView avatarImage = (ImageView) findViewById(R.id.user_temple_avatar);
        image1 = (ImageView) findViewById(R.id.user_temple_image_1);
        image2 = (ImageView) findViewById(R.id.user_temple_image_2);
        image3 = (ImageView) findViewById(R.id.user_temple_image_3);
        progressBar = (ProgressBar) findViewById(R.id.user_temple_progressBar);
        descriptionTextView = (TextView) findViewById(R.id.user_temple_wistharaya);
        auth = FirebaseAuth.getInstance();

        String name = CommonMethods.getName();
        hiText.setText("ආයුබෝවන් " + name);

        firebaseProcess();

        avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateShowAvatarDialog();
            }
        });
    }

    private void firebaseProcess() {
        progressBar.setVisibility(View.VISIBLE);
        isSelected = false;
        listOfImages = new ArrayList<>();
        FirebaseUser user = auth.getCurrentUser();
        rootNode = FirebaseDatabase.getInstance();
        userReference = rootNode.getReference("USER");
        templeReference = rootNode.getReference("TEMPLE");
        imageReference = rootNode.getReference("IMGS");

        userReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        UserRole userFromFirebase = dataSnapshot.getValue(UserRole.class);
                        if (userFromFirebase != null) {
                            if (userFromFirebase.getUserId().equals(user.getUid())) {
                                if (userFromFirebase.getPreferenceTempleId() != null) {
                                    userPreferenceTempleId = userFromFirebase.getPreferenceTempleId();
                                    isSelected = true;
                                }
                            }
                        }
                    }

                    if (isSelected) {
                        templeReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                                        Temple templeFromFirebase = dataSnapshot.getValue(Temple.class);
                                        if (templeFromFirebase != null) {
                                            if (userPreferenceTempleId.equals(templeFromFirebase.getUserId())) {
                                                String description = "\u2022" + " නම: " + templeFromFirebase.getTempleName() + "\n" +
                                                        "\u2022" + " විහාරාධිපති හිමි: " + templeFromFirebase.getWiharadhipathiHimi() + "\n" +
                                                        "\u2022" + " ලිපිනය: " + templeFromFirebase.getTempleAddress() + "\n" +
                                                        "\u2022" + " දුරකථන අංකය : " + templeFromFirebase.getTelNo() + "\n" +
                                                        "\u2022" + " විද්යුත් තැපෑල: " + templeFromFirebase.getEmail() + "\n" +
                                                        "\u2022" + " වැඩිදුර තොරතුරු: " + ((templeFromFirebase.getTempleDescription() == null)
                                                        ? "-" : templeFromFirebase.getTempleDescription());
                                                descriptionTextView.setText(description);

                                                populateImagesFromFirebase(templeFromFirebase);
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    } else {
                        progressBar.setVisibility(View.GONE);
                        showErrorDialog("Temple is not selected as preference temple");
                    }
                } else {
                    showErrorDialog("Internal server error");
                }
            }
        });
    }

    private void populateImagesFromFirebase(Temple templeFromFirebase) {
        List<String> imageIdList = new ArrayList<>();
        imageReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        Img imgFromFirebase = dataSnapshot.getValue(Img.class);
                        if (imgFromFirebase != null) {
                            if (templeFromFirebase.getUserId().equals(imgFromFirebase.getReferenceId())) {
                                imageIdList.add(imgFromFirebase.getImgId());
                            }
                        }
                    }
                    populateImages(imageIdList);
                }
            }
        });
    }

    private void populateImages(List<String> imageIdList) {
        for (String imageId : imageIdList) {
            FetchTempleImageIcons fetchEventImageIcons = new FetchTempleImageIcons();
            try {
                Bitmap bitmap = fetchEventImageIcons.execute(imageId).get();
                if (bitmap != null) {
                    listOfImages.add(Bitmap.createScaledBitmap(bitmap, 500, 500, true));
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Bitmap commonTempleImage = BitmapFactory.decodeResource(getResources(), R.drawable.common_temple);

        image2.setVisibility(View.VISIBLE);
        image3.setVisibility(View.VISIBLE);
        if (listOfImages.size() == 3) {
            image1.setImageBitmap(listOfImages.get(0));
            image2.setImageBitmap(listOfImages.get(1));
            image3.setImageBitmap(listOfImages.get(2));
        } else if (listOfImages.size() == 2){
            image1.setImageBitmap(listOfImages.get(0));
            image2.setImageBitmap(listOfImages.get(1));
            image3.setVisibility(View.INVISIBLE);
        } else if (listOfImages.size() == 1){
            image1.setImageBitmap(listOfImages.get(0));
            image2.setVisibility(View.INVISIBLE);
            image3.setVisibility(View.INVISIBLE);
        } else if (listOfImages.size() == 0){
            image1.setImageBitmap(Bitmap.createScaledBitmap(commonTempleImage, 500, 500, true));
            image2.setVisibility(View.INVISIBLE);
            image3.setVisibility(View.INVISIBLE);
        }
        progressBar.setVisibility(View.GONE);
    }

    public class FetchTempleImageIcons extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap image = null;
            try {
                String imageId = null;
                if (strings.length != 0) {
                    imageId = strings[0];
                }

                //TODO - get all images from firebase
                //check access token scenario
                //for now i skip that
                String baseUrl = "https://firebasestorage.googleapis.com/v0/b/pansala-android-project.appspot.com/o/TEMPLE_IMAGE%2F" + imageId + "?alt=media";
                URL url = new URL(baseUrl);
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch(Exception e) {
                System.out.println(e);
            }
            return image;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
        }
    }

    private void showErrorDialog(String errorMessage) {
        new SweetAlertDialog(UserTemple.this, SweetAlertDialog.ERROR_TYPE)
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
                new SweetAlertDialog(UserTemple.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Sign Out")
                        .setContentText("Do you want to sign out from the app? ")
                        .setConfirmText("Yes")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                                CommonMethods.signOut();
                                new SweetAlertDialog(UserTemple.this)
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