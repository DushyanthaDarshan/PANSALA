package com.ddr.pansala;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * author : Dushyantha Darshan Rubasinghe
 */
public class CustomListAdapterSuperAdminViewUsers extends ArrayAdapter<String> {

    private final Activity context;
    private final List<String> namesList;
    private final List<String> emailList;
    private final List<String> userTypeList;
    private final List<String> userStatusList;
    private final List<Bitmap> userDpList;

    public CustomListAdapterSuperAdminViewUsers(Activity context, List<String> namesList, List<String> emailList,
                                                List<String> userTypeList, List<String> userStatusList, List<Bitmap> userDpList) {
        super(context, R.layout.s_admin_temple_list, namesList);

        this.context = context;
        this.namesList = namesList;
        this.emailList = emailList;
        this.userTypeList = userTypeList;
        this.userStatusList = userStatusList;
        this.userDpList = userDpList;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        @SuppressLint("ViewHolder") View rowView = inflater.inflate(R.layout.s_admin_users_list, null, true);
        TextView userName = (TextView) rowView.findViewById(R.id.s_admin_view_user_name);
        TextView email = (TextView) rowView.findViewById(R.id.s_admin_view_user_email);
        TextView userType = (TextView) rowView.findViewById(R.id.s_admin_view_user_type);
        TextView userStatus = (TextView) rowView.findViewById(R.id.s_admin_view_user_status);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.s_admin_view_user_dp);

        userName.setText(namesList.get(position));
        email.setText(emailList.get(position));
        userType.setText(userTypeList.get(position));
        userStatus.setText(userStatusList.get(position));
        imageView.setImageBitmap(userDpList.get(position));
        return rowView;
    }
}