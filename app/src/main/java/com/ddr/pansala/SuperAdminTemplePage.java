package com.ddr.pansala;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
                populateShowAvatarDialog();
            }
        });

        sAdminTempleViewLayout.setOnClickListener(new View.OnClickListener() {
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
                CommonMethods.signOut();
                Intent openSignInPage = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(openSignInPage);
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