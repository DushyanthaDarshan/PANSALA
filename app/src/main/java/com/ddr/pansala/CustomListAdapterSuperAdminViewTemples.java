package com.ddr.pansala;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomListAdapterSuperAdminViewTemples extends ArrayAdapter<String> {

    private final Activity context;
    private final List<String> templeNamesList;
    private final List<String> wiharadhipathiHimiNamesList;
    private final List<String> telNoList;
    private final List<String> addressList;
    private final List<String> emailList;
    private final List<String> descriptionList;
    private final List<Bitmap> templeImageList;

    public CustomListAdapterSuperAdminViewTemples(Activity context, List<String> templeNamesList, List<String> wiharadhipathiHimiNamesList,
                                                  List<String> telNoList, List<String> addressList, List<String> emailList,
                                                  List<String> descriptionList, List<Bitmap> templeImageList) {
        super(context, R.layout.s_admin_temple_list, templeNamesList);

        this.context = context;
        this.templeNamesList = templeNamesList;
        this.wiharadhipathiHimiNamesList = wiharadhipathiHimiNamesList;
        this.telNoList = telNoList;
        this.addressList = addressList;
        this.emailList = emailList;
        this.descriptionList = descriptionList;
        this.templeImageList = templeImageList;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        @SuppressLint("ViewHolder") View rowView = inflater.inflate(R.layout.s_admin_temple_list, null, true);
        TextView templeName = (TextView) rowView.findViewById(R.id.s_admin_view_temple_name);
        TextView wiharadhipathiHimi = (TextView) rowView.findViewById(R.id.s_admin_view_wiharadhipathi_himi);
        TextView email = (TextView) rowView.findViewById(R.id.s_admin_view_email);
        TextView address = (TextView) rowView.findViewById(R.id.s_admin_view_address);
        TextView telNo = (TextView) rowView.findViewById(R.id.s_admin_view_telNo);
        TextView description = (TextView) rowView.findViewById(R.id.s_admin_view_description);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.s_admin_view_image);

        templeName.setText(templeNamesList.get(position));
        wiharadhipathiHimi.setText(wiharadhipathiHimiNamesList.get(position));
        email.setText(emailList.get(position));
        address.setText(addressList.get(position));
        telNo.setText(telNoList.get(position));
        description.setText(descriptionList.get(position));
        imageView.setImageBitmap(templeImageList.get(position));
        return rowView;
    }
}