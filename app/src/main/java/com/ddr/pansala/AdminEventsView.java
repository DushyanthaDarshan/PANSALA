package com.ddr.pansala;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminEventsView extends AppCompatActivity {

    private ProgressBar progressBar;
    List<String> eventNamesList = new ArrayList<>();
    List<String> eventDescriptionList = new ArrayList<>();
    List<String> dateList = new ArrayList<>();
    List<String> timeList = new ArrayList<>();
    List<String> placeList = new ArrayList<>();
    List<Bitmap> imageList = new ArrayList<>();
    String eventsJson;
    CustomListAdapterAdminViewEvents adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_events_view);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#800000"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        TextView hiText = (TextView) findViewById(R.id.admin_events_view_hi_text);
        ImageView avatarImage = (ImageView) findViewById(R.id.admin_events_view_avatar);
//        SearchView searchView = (SearchView) findViewById(R.id.s_admin_search_temple_view);
        progressBar = (ProgressBar) findViewById(R.id.admin_events_view_progressBar);

        avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateShowAvatarDialog();
            }
        });

        FetchAdminEventsViewData fetchAdminEventsViewData = new FetchAdminEventsViewData();
        fetchAdminEventsViewData.execute();
    }

    private void executeListView() {
        adapter = new CustomListAdapterAdminViewEvents(this, eventNamesList, eventDescriptionList,
                dateList, timeList, placeList, imageList);
        ListView listView = (ListView) findViewById(R.id.admin_events_list_view);
        listView.setAdapter(adapter);
    }

    public void showErrorDialog(String errorMessage) {
        new SweetAlertDialog(AdminEventsView.this, SweetAlertDialog.ERROR_TYPE)
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
                new SweetAlertDialog(AdminEventsView.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Sign Out")
                        .setContentText("Do you want to sign out from the app? ")
                        .setConfirmText("Yes")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                                CommonMethods.signOut();
                                new SweetAlertDialog(AdminEventsView.this)
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

    public class FetchAdminEventsViewData extends AsyncTask<String, Void, String> {

        ProgressDialog progress = new ProgressDialog(AdminEventsView.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected void onPostExecute(String s) {
            String userId = CommonMethods.getUserIdFromSession();
            Bitmap commonEventImage = BitmapFactory.decodeResource(getResources(), R.drawable.common_temple);
            try {
                if (eventsJson != null) {
                    JSONObject fullObject = new JSONObject(eventsJson);
                    JSONArray keys = fullObject.names();

                    for (int i = 0; keys.length() > i; i++) {
                        String key = keys.getString(i);
                        JSONObject event = fullObject.getJSONObject(key);
                        if (userId.equals(event.getString("userId"))) {
                            eventNamesList.add(event.getString("eventName"));
                            eventDescriptionList.add(event.getString("eventDescription"));
                            timeList.add(event.getString("eventTime"));
                            dateList.add(event.getString("eventDate"));
                            placeList.add(event.getString("eventPlace"));

                            String imageId = event.getString("imageId");
                            if (imageId != null) {
                                FetchEventImageIcons fetchEventImageIcons = new FetchEventImageIcons();
                                Bitmap bitmap = fetchEventImageIcons.execute(imageId).get();

                                if (bitmap != null) {
                                    imageList.add(Bitmap.createScaledBitmap(bitmap, 500, 500, true));
                                } else {
                                    imageList.add(Bitmap.createScaledBitmap(commonEventImage, 500, 500, true));
                                }
                            }

                        } else {
                            showErrorDialog("No events");
                        }
                    }
                } else {
                    showErrorDialog("There is a internal server error");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            executeListView();
            progress.dismiss();
        }

        @Override
        protected String doInBackground(String... stringsArray) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                final String BASE_URL = "https://pansala-android-project-default-rtdb.firebaseio.com/EVENT.json";
                URL url = new URL(BASE_URL);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line1;

                while ((line1 = reader.readLine()) != null) {
                    buffer.append(line1 + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                eventsJson = buffer.toString();

            } catch (IOException e) {
                Log.e("Hi", "Error", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Hi", "Error closing stream", e);
                    }
                }
            }
            return eventsJson;
        }
    }

    public class FetchEventImageIcons extends AsyncTask<String, Void, Bitmap> {

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
                String baseUrl = "https://firebasestorage.googleapis.com/v0/b/pansala-android-project.appspot.com/o/EVENT_IMAGE%2F" + imageId + "?alt=media&token=d5ccf423-b697-437e-b429-b623eebf81c2";
                URL url = new URL(baseUrl);
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch(Exception e) {
                System.out.println(e);
            }
            return image;
        }
    }
}