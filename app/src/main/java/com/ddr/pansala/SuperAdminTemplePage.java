package com.ddr.pansala;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SuperAdminTemplePage extends AppCompatActivity {

    private LinearLayout sAdminTemplesAddLayout, sAdminTempleViewLayout;
    private TextView sAdminTempleHiText;
    private ImageView sAdminTempleAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin_temple);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#800000"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        sAdminTemplesAddLayout = (LinearLayout) findViewById(R.id.s_admin_temples_add_layout);
        sAdminTempleViewLayout = (LinearLayout) findViewById(R.id.s_admin_temple_view_layout);
        sAdminTempleHiText = (TextView) findViewById(R.id.s_admin_temple_hi_text);
        sAdminTempleAvatar = (ImageView) findViewById(R.id.s_admin_temple_avatar);

        String name = CommonMethods.getName();
        sAdminTempleHiText.setText("ආයුබෝවන් " + name);

        sAdminTempleAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateShowAvatarDialog();
            }
        });

        sAdminTemplesAddLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openTempleAddActivity = new Intent(getApplicationContext(), SAdminTempleAdd.class);
                startActivity(openTempleAddActivity);
            }
        });

        sAdminTempleViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openTempleViewActivity = new Intent(getApplicationContext(), SAdminViewTemples.class);
                startActivity(openTempleViewActivity);
            }
        });
    }

    private void populateShowAvatarDialog() {
        final View avatarLayout = getLayoutInflater().inflate(R.layout.avatar_dialog, null);
        TextView logOutText = (TextView) avatarLayout.findViewById(R.id.logout_text);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ගිණුම");
        builder.setView(avatarLayout);

        logOutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(SuperAdminTemplePage.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ගිණුමෙන් ඉවත්වීම")
                        .setContentText("ඔබට යෙදුමෙන් ඉවත් වීමට අවශ්‍යද? ")
                        .setConfirmText("ඔව්")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                                CommonMethods.signOut();
                                new SweetAlertDialog(SuperAdminTemplePage.this)
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
                        .setCancelButton("නැත", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        builder.setNegativeButton("නැත", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}