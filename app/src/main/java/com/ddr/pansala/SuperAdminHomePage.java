package com.ddr.pansala;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * author : Dushyantha Darshan Rubasinghe
 */

public class SuperAdminHomePage extends AppCompatActivity {

    private LinearLayout sAdminHomeTemplesLayout, sAdminHomeContributorsLayout, sAdminHomeNoticesLayout;
    private TextView sAdminHomeHiText;
    private ImageView sAdminHomeAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin_home_page);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#800000"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        sAdminHomeTemplesLayout = (LinearLayout) findViewById(R.id.s_admin_home_temples_layout);
        sAdminHomeContributorsLayout = (LinearLayout) findViewById(R.id.s_admin_home_contributors_layout);
        sAdminHomeNoticesLayout = (LinearLayout) findViewById(R.id.s_admin_home_notices_layout);
        sAdminHomeHiText = (TextView) findViewById(R.id.s_admin_home_hi_text);
        sAdminHomeAvatar = (ImageView) findViewById(R.id.s_admin_home_avatar);

        String name = CommonMethods.getName();
        sAdminHomeHiText.setText("ආයුබෝවන් " + name);

        sAdminHomeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateShowAvatarDialog();
            }
        });

        sAdminHomeTemplesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openSuperAdminTemplePage = new Intent(getApplicationContext(), SuperAdminTemplePage.class);
                startActivity(openSuperAdminTemplePage);
            }
        });

        sAdminHomeContributorsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openSuperAdminUsersPage = new Intent(getApplicationContext(), SAdminViewUsers.class);
                startActivity(openSuperAdminUsersPage);
            }
        });

        sAdminHomeNoticesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "This feature will be coming soon...", Toast.LENGTH_LONG).show();
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
                new SweetAlertDialog(SuperAdminHomePage.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Sign Out")
                        .setContentText("Do you want to sign out from the app? ")
                        .setConfirmText("Yes")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                                CommonMethods.signOut();
                                new SweetAlertDialog(SuperAdminHomePage.this)
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

    /**
     * The method to three vertical menu
     *
     * @param menu
     * @return
     */
    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.layout.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Settings");
            builder.setMessage("This feature will be coming soon...");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        if (id == R.id.about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("About");
            builder.setMessage("This feature will be coming soon...");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }
}