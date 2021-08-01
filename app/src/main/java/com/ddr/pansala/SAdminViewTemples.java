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
public class SAdminViewTemples extends AppCompatActivity {

    private ProgressBar progressBar;
    List<String> templeNamesList = new ArrayList<>();
    List<String> wiharadhipathiHimiNamesList = new ArrayList<>();
    List<String> telNoList = new ArrayList<>();
    List<String> addressList = new ArrayList<>();
    List<String> emailList = new ArrayList<>();
    List<String> descriptionList = new ArrayList<>();
    List<Bitmap> templeImageList = new ArrayList<>();
    List<String> tempTempleNamesList = new ArrayList<>();
    List<String> tempWiharadhipathiHimiNamesList = new ArrayList<>();
    List<String> tempTelNoList = new ArrayList<>();
    List<String> tempAddressList = new ArrayList<>();
    List<String> tempEmailList = new ArrayList<>();
    List<String> tempDescriptionList = new ArrayList<>();
    List<Bitmap> tempTempleImageList = new ArrayList<>();
    String templesJson;
    CustomListAdapterSuperAdminViewTemples adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sadmin_view_temples);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#800000"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        TextView hiText = (TextView) findViewById(R.id.s_admin_temple_view_hi_text);
        ImageView avatarImage = (ImageView) findViewById(R.id.s_admin_temple_view_avatar);
        EditText searchView = (EditText) findViewById(R.id.s_admin_view_temple_search_view);
        Button searchBtn = (Button) findViewById(R.id.s_admin_view_temple_search_btn);
        progressBar = (ProgressBar) findViewById(R.id.s_admin_temple_view_progressBar);

        String name = CommonMethods.getName();
        hiText.setText("ආයුබෝවන් " + name);

        avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateShowAvatarDialog();
            }
        });

        FetchData fetchData = new FetchData();
        fetchData.execute();

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempTempleNamesList.clear();
                tempWiharadhipathiHimiNamesList.clear();
                tempTelNoList.clear();
                tempAddressList.clear();
                tempEmailList.clear();
                tempDescriptionList.clear();
                tempTempleImageList.clear();

                String searchText = searchView.getText().toString().trim();
                for (int i = 0; templeNamesList.size() > i; i++) {
                    String templeName = templeNamesList.get(i);
                    List<String> splitNamesList = Arrays.asList(templeName.split(" "));
                    if (splitNamesList.contains(searchText)) {
                        tempTempleNamesList.add(templeName);
                        tempWiharadhipathiHimiNamesList.add(wiharadhipathiHimiNamesList.get(i));
                        tempTelNoList.add(telNoList.get(i));
                        tempAddressList.add(addressList.get(i));
                        tempEmailList.add(emailList.get(i));
                        tempDescriptionList.add(descriptionList.get(i));
                        tempTempleImageList.add(templeImageList.get(i));
                    }
                }
                if (tempTempleNamesList.size() != 0) {
                    executeListView(tempTempleNamesList, tempWiharadhipathiHimiNamesList, tempTelNoList,
                            tempAddressList, tempEmailList, tempDescriptionList, tempTempleImageList);
                } else {
                    executeListView(templeNamesList, wiharadhipathiHimiNamesList, telNoList, addressList,
                            emailList, descriptionList, templeImageList);
                    showErrorDialog("There are no any matched temples found. Try using another word....");
                }
            }
        });
    }

    private void executeListView(List<String> templeNamesList, List<String> wiharadhipathiHimiNamesList,
                                 List<String> telNoList, List<String> addressList, List<String> emailList,
                                 List<String> descriptionList, List<Bitmap> templeImageList) {
        adapter = new CustomListAdapterSuperAdminViewTemples(this,
                templeNamesList, wiharadhipathiHimiNamesList, telNoList, addressList, emailList, descriptionList, templeImageList);
        ListView listView = (ListView) findViewById(R.id.s_admin_temple_list_view);
        listView.setAdapter(adapter);
    }

    public void showErrorDialog(String errorMessage) {
        new SweetAlertDialog(SAdminViewTemples.this, SweetAlertDialog.ERROR_TYPE)
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
                new SweetAlertDialog(SAdminViewTemples.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Sign Out")
                        .setContentText("Do you want to sign out from the app? ")
                        .setConfirmText("Yes")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                                CommonMethods.signOut();
                                new SweetAlertDialog(SAdminViewTemples.this)
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

    public class FetchData extends AsyncTask<String, Void, String> {

        ProgressDialog progress = new ProgressDialog(SAdminViewTemples.this);

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
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.common_temple);
            try {
                if (templesJson != null) {
                    JSONObject fullObject = new JSONObject(templesJson);
                    JSONArray keys = fullObject.names();

                    for (int i = 0; keys.length() > i; i++) {
                        String key = keys.getString(i);
                        JSONObject temple = fullObject.getJSONObject(key);
                        templeNamesList.add(temple.getString("templeName"));
                        wiharadhipathiHimiNamesList.add(temple.getString("wiharadhipathiHimi"));
                        addressList.add(temple.getString("templeAddress"));
                        telNoList.add(temple.getString("telNo"));
                        emailList.add(temple.getString("email"));
                        descriptionList.add(temple.getString("userStatus"));
                        templeImageList.add(Bitmap.createScaledBitmap(bitmap, 500, 500, true));
                    }
                } else {
                    showErrorDialog("There is a internal server error");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            executeListView(templeNamesList, wiharadhipathiHimiNamesList, telNoList, addressList, emailList, descriptionList, templeImageList);
            progress.dismiss();
        }

        @Override
        protected String doInBackground(String... stringsArray) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                final String BASE_URL = "https://pansala-android-project-default-rtdb.firebaseio.com/TEMPLE.json";
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
                templesJson = buffer.toString();

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
            return templesJson;
        }
    }
}