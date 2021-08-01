package com.ddr.pansala;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * author : Dushyantha Darshan Rubasinghe
 */
public class SAdminViewUsers extends AppCompatActivity {

    private ProgressBar progressBar;
    List<String> usersNamesList = new ArrayList<>();
    List<String> emailList = new ArrayList<>();
    List<String> userTypeList = new ArrayList<>();
    List<String> userStatusList = new ArrayList<>();
    List<Bitmap> usersDpList = new ArrayList<>();
    List<String> tempUsersNamesList = new ArrayList<>();
    List<String> tempEmailList = new ArrayList<>();
    List<String> tempUserTypeList = new ArrayList<>();
    List<String> tempUserStatusList = new ArrayList<>();
    List<Bitmap> tempUsersDpList = new ArrayList<>();
    String usersJson;
    CustomListAdapterSuperAdminViewUsers adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sadmin_view_users);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#800000"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        TextView hiText = (TextView) findViewById(R.id.s_admin_view_users_hi_text);
        ImageView avatarImage = (ImageView) findViewById(R.id.s_admin_view_users_avatar);
        EditText searchView = (EditText) findViewById(R.id.s_admin_search_user_view);
        Button searchBtn = (Button) findViewById(R.id.s_admin_search_user_btn);
        progressBar = (ProgressBar) findViewById(R.id.s_admin_view_users_progressBar);

        String name = CommonMethods.getName();
        hiText.setText("ආයුබෝවන් " + name);

        avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateShowAvatarDialog();
            }
        });

        FetchDataForViewUsers fetchData = new FetchDataForViewUsers();
        fetchData.execute();

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempUsersNamesList.clear();
                tempEmailList.clear();
                tempUserTypeList.clear();
                tempUserStatusList.clear();
                tempUsersDpList.clear();

                String searchText = searchView.getText().toString().trim();
                for (int i = 0; usersNamesList.size() > i; i++) {
                    String templeName = usersNamesList.get(i);
                    List<String> splitNamesList = Arrays.asList(templeName.split(" "));
                    if (splitNamesList.contains(searchText)) {
                        tempUsersNamesList.add(templeName);
                        tempEmailList.add(emailList.get(i));
                        tempUserTypeList.add(userTypeList.get(i));
                        tempUserStatusList.add(userStatusList.get(i));
                        tempUsersDpList.add(usersDpList.get(i));
                    }
                }
                if (tempUsersNamesList.size() != 0) {
                    executeListView(tempUsersNamesList, tempEmailList, tempUserTypeList, tempUserStatusList, tempUsersDpList);
                } else {
                    executeListView(usersNamesList, emailList, userTypeList, userStatusList, usersDpList);
                    showErrorDialog("There are no any matched user found. Try using another word....");
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
                new SweetAlertDialog(SAdminViewUsers.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Sign Out")
                        .setContentText("Do you want to sign out from the app? ")
                        .setConfirmText("Yes")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                                CommonMethods.signOut();
                                new SweetAlertDialog(SAdminViewUsers.this)
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

    public class FetchDataForViewUsers extends AsyncTask<String, Void, String> {

        ProgressDialog progress = new ProgressDialog(SAdminViewUsers.this);

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
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user_dp);
            try {
                if (usersJson != null) {
                    JSONObject fullObject = new JSONObject(usersJson);
                    JSONArray keys = fullObject.names();

                    for (int i = 0; keys.length() > i; i++) {
                        String key = keys.getString(i);
                        JSONObject temple = fullObject.getJSONObject(key);
                        usersNamesList.add(temple.getString("name"));
                        emailList.add(temple.getString("email"));
                        userTypeList.add(temple.getString("userType"));
                        userStatusList.add(temple.getString("userStatus"));
                        usersDpList.add(Bitmap.createScaledBitmap(bitmap, 60, 70, true));
                    }
                } else {
                    showErrorDialog("There is a internal server error");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            executeListView(usersNamesList, emailList, userTypeList, userStatusList, usersDpList);
            progress.dismiss();
        }

        @Override
        protected String doInBackground(String... stringsArray) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                final String BASE_URL = "https://pansala-android-project-default-rtdb.firebaseio.com/USER.json";
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
                usersJson = buffer.toString();

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
            return usersJson;
        }
    }

    private void executeListView(List<String> usersNamesList, List<String> emailList, List<String> userTypeList,
                                 List<String> userStatusList, List<Bitmap> usersDpList) {
        adapter = new CustomListAdapterSuperAdminViewUsers(this,
                usersNamesList, emailList, userTypeList, userStatusList, usersDpList);
        ListView listView = (ListView) findViewById(R.id.s_admin_users_list_view);
        listView.setAdapter(adapter);
    }

    public void showErrorDialog(String errorMessage) {
        new SweetAlertDialog(SAdminViewUsers.this, SweetAlertDialog.ERROR_TYPE)
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