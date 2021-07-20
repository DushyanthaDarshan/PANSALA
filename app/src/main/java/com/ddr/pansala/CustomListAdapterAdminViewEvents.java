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

public class CustomListAdapterAdminViewEvents extends ArrayAdapter<String> {

    private final Activity context;
    private final List<String> eventNamesList;
    private final List<String> eventDescriptionList;
    private final List<String> dateList;
    private final List<String> timeList;
    private final List<String> placeList;
    private final List<Bitmap> imageList;

    public CustomListAdapterAdminViewEvents(Activity context, List<String> eventNamesList, List<String> eventDescriptionList,
                                            List<String> dateList, List<String> timeList, List<String> placeList,
                                            List<Bitmap> imageList) {
        super(context, R.layout.admin_events_list, eventNamesList);

        this.context = context;
        this.eventNamesList = eventNamesList;
        this.eventDescriptionList = eventDescriptionList;
        this.dateList = dateList;
        this.timeList = timeList;
        this.placeList = placeList;
        this.imageList = imageList;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        @SuppressLint("ViewHolder") View rowView = inflater.inflate(R.layout.admin_events_list, null, true);
        TextView eventName = (TextView) rowView.findViewById(R.id.admin_view_event_name);
        TextView date = (TextView) rowView.findViewById(R.id.admin_view_event_date);
        TextView time = (TextView) rowView.findViewById(R.id.admin_view_event_time);
        TextView place = (TextView) rowView.findViewById(R.id.admin_view_event_place);
        TextView description = (TextView) rowView.findViewById(R.id.admin_view_event_description);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.admin_view_event_image);

        eventName.setText(eventNamesList.get(position));
        date.setText(dateList.get(position));
        time.setText(timeList.get(position));
        place.setText(placeList.get(position));
        description.setText(eventDescriptionList.get(position));
        imageView.setImageBitmap(imageList.get(position));
        return rowView;
    }
}