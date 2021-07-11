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
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.ContentValues.TAG;

public class SAdminViewTemples extends AppCompatActivity {

    private ProgressBar progressBar;
    List<String> templeNamesList = new ArrayList<>();
    List<String> wiharadhipathiHimiNamesList = new ArrayList<>();
    List<String> telNoList = new ArrayList<>();
    List<String> addressList = new ArrayList<>();
    List<String> emailList = new ArrayList<>();
    List<String> descriptionList = new ArrayList<>();
    List<Bitmap> templeImageList = new ArrayList<>();
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

        TextView hiText = (TextView) findViewById(R.id.s_admin_temple_add_hi_text);
        ImageView avatarImage = (ImageView) findViewById(R.id.s_admin_temple_view_avatar);
        SearchView searchView = (SearchView) findViewById(R.id.s_admin_search_temple_view);
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
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                Log.d("AAAAAAAAAAAAAAA", query);
//                if(templeNamesList.contains(query)){
//                    Log.d("BBBBBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
//                    adapter.getFilter().filter(query);
//                }else{
//                    Log.d("CCCCCCCCCCCCCCCCCCCC", "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC");
//                    Toast.makeText(SAdminViewTemples.this, "No Match found",Toast.LENGTH_LONG).show();
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                Log.d("DDDDDDDDDDDDDDDDDD", newText);
//                for(int i=0; i<templeNamesList.size();i++)
//                {
//                    String name = templeNamesList.get(i);
//                    if(name.startsWith(newText)) {
//                        Log.d("EEEEEEEEEEEEEEE", name);
//                        adapter.getFilter().filter(name);
//                    }
//                }
//                return false;
//            }
//        });
    }

    private void executeListView() {
        adapter = new CustomListAdapterSuperAdminViewTemples(this,
                templeNamesList, wiharadhipathiHimiNamesList, telNoList, addressList, emailList, descriptionList, templeImageList);
        ListView listView = (ListView) findViewById(R.id.s_admin_temple_list_view);
        listView.setAdapter(adapter);

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(), templeNamesList.get(position), Toast.LENGTH_LONG);
//            }
//        });
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
                        finish();
                        Intent restartActivity = new Intent(getApplicationContext(), SignupActivity.class);
                        startActivity(restartActivity);
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
            executeListView();
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