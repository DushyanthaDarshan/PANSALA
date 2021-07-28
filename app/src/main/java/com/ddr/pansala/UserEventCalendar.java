package com.ddr.pansala;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

@RequiresApi(api = Build.VERSION_CODES.O)
public class UserEventCalendar extends AppCompatActivity {

    private ProgressBar progressBar;
    private CalendarView calendarView;
    private List<Calendar> calendars = new ArrayList<>();
    String templesJson;
    private FirebaseDatabase rootNode;
    private DatabaseReference userReference;
    private DatabaseReference templeReference;
    private DatabaseReference eventReference;
    private Boolean isPreferenceTemple;
    private FirebaseAuth auth;
    private String userPreferenceTempleId;
    private List<Event> eventsList = new ArrayList<>();
    private List<EventDay> events = new ArrayList<>();
    public static final String NOTIFICATION_CHANNEL_ID = "my_channel_01";
    private final static String default_notification_channel_id = "default";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_event_calendar);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#800000"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        TextView hiText = (TextView) findViewById(R.id.user_calendar_hi_text);
        ImageView avatarImage = (ImageView) findViewById(R.id.user_calendar_avatar);
        calendarView = (CalendarView) findViewById(R.id.user_calendar_view);
        progressBar = (ProgressBar) findViewById(R.id.user_calendar_progressBar);
        auth = FirebaseAuth.getInstance();

        String name = CommonMethods.getName();
        hiText.setText("ආයුබෝවන් " + name);

        avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateShowAvatarDialog();
            }
        });

        firebaseProcess(calendarView);
    }

//    public class MyEventDay extends EventDay implements Parcelable {
//
//        private String mNote;
//
//        public MyEventDay(Calendar day, String note) {
//            super(day);
//            mNote = note;
//        }
//        public String getNote() {
//            return mNote;
//        }
//        private MyEventDay(Parcel in) {
//            super((Calendar) in.readSerializable(), in.readInt());
//            mNote = in.readString();
//        }
//
//        public final Creator<MyEventDay> CREATOR = new Creator<MyEventDay>() {
//            @Override
//            public MyEventDay createFromParcel(Parcel in) {
//                return new MyEventDay(in);
//            }
//            @Override
//            public MyEventDay[] newArray(int size) {
//                return new MyEventDay[size];
//            }
//        };
//        @Override
//        public void writeToParcel(Parcel parcel, int i) {
//            parcel.writeSerializable(getCalendar());
//            parcel.writeString(mNote);
//        }
//        @Override
//        public int describeContents() {
//            return 0;
//        }
//    }

    private void populateShowAvatarDialog() {
        final View avatarLayout = getLayoutInflater().inflate(R.layout.avatar_dialog, null);
        TextView logOutText = (TextView) avatarLayout.findViewById(R.id.logout_text);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Account");
        builder.setView(avatarLayout);

        logOutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(UserEventCalendar.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Sign Out")
                        .setContentText("Do you want to sign out from the app? ")
                        .setConfirmText("Yes")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                                CommonMethods.signOut();
                                new SweetAlertDialog(UserEventCalendar.this)
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void firebaseProcess(CalendarView calendarView) {
        progressBar.setVisibility(View.VISIBLE);
        isPreferenceTemple = false;
        FirebaseUser user = auth.getCurrentUser();
        rootNode = FirebaseDatabase.getInstance();
        userReference = rootNode.getReference("USER");
        templeReference = rootNode.getReference("TEMPLE");
        eventReference = rootNode.getReference("EVENT");
        Calendar currentDate = Calendar.getInstance();

        currentDate.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));

        userReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        UserRole userFromFirebase = dataSnapshot.getValue(UserRole.class);
                        if (userFromFirebase != null) {
                            if (userFromFirebase.getUserId().equals(user.getUid())) {
                                if (userFromFirebase.getPreferenceTempleId() != null) {
                                    userPreferenceTempleId = userFromFirebase.getPreferenceTempleId();
                                    isPreferenceTemple = true;
                                }
                            }
                        }
                    }

                    if (isPreferenceTemple) {
                        eventReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                                        Event eventFromFirebase = dataSnapshot.getValue(Event.class);
                                        if (eventFromFirebase != null) {
                                            if (eventFromFirebase.getUserId().equals(userPreferenceTempleId)) {
                                                Calendar calendar = Calendar.getInstance();
                                                List<String> splitDate = Arrays.asList(eventFromFirebase.getEventDate().split("-"));
                                                calendar.set(Integer.parseInt(splitDate.get(0)), Integer.parseInt(splitDate.get(1))-1, Integer.parseInt(splitDate.get(2)));
                                                calendars.add(calendar);
                                                EventDay eventDay = new EventDay(calendar, R.drawable.alms_giving, R.color.maroon);
                                                events.add(eventDay);
                                                eventsList.add(eventFromFirebase);

                                                //check event is already added to the file or not. if not update file and added to the set notifications
                                                SharedPreferences sharedPreferences =
                                                        getSharedPreferences("Event_id_list_file",MODE_PRIVATE);
                                                String eventIdListFromFile = sharedPreferences.getString("eventId", null);
                                                int number = sharedPreferences.getInt("notificationId", 0);
                                                String myDate = splitDate.get(0) + "/" + splitDate.get(1) + "/" +
                                                        splitDate.get(2) + " 00:00:00";
                                                if (eventIdListFromFile != null) {
                                                    List<String> splitEventIdList = Arrays.asList(eventIdListFromFile.split(","));
                                                    if (!splitEventIdList.contains(eventFromFirebase.getEventId())) {
                                                        number++;
                                                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                                        eventIdListFromFile = eventIdListFromFile + "," + eventFromFirebase.getEventId();
                                                        myEdit.putString("eventId", eventIdListFromFile);
                                                        myEdit.putInt("notificationId", number);
                                                        myEdit.commit();
                                                        populateNotificationsFromFirebase(myDate, eventFromFirebase, number);
                                                    }
                                                } else {
                                                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                                    myEdit.putString("eventId", eventFromFirebase.getEventId());
                                                    myEdit.putInt("notificationId", 1);
                                                    myEdit.apply();
                                                    populateNotificationsFromFirebase(myDate, eventFromFirebase, 1);
                                                }
                                            }
                                        }
                                    }
                                    try {
                                        calendarView.setDate(currentDate);
                                    } catch (OutOfDateRangeException e) {
                                        e.printStackTrace();
                                    }
                                    if (events.size() != 0) {
                                        calendarView.setEvents(events);
                                        calendarView.setHighlightedDays(calendars);
                                        calendarView.setOnDayClickListener(new OnDayClickListener() {
                                            @Override
                                            public void onDayClick(EventDay eventDay) {
                                                Calendar clickedDayCalendar = eventDay.getCalendar();
                                                Toast.makeText(getApplicationContext(), "IAAAAAAAAAAAAAAAAAAAAAAA" + clickedDayCalendar.toString(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    showErrorDialog("Internal server error");
                                }
                            }
                        });
                    } else {
                        progressBar.setVisibility(View.GONE);
                        showErrorDialog("Any selected preference temple not found");
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    showErrorDialog("Internal server error");
                }
            }
        });
        progressBar.setVisibility(View.GONE);
    }

    private void populateNotificationsFromFirebase(String myDate, Event event, int num) {
        long millis = 0;
        long currentMillis = 0;
        long delay = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            Date date = sdf.parse(myDate);
            Date currentDate = new Date();
            currentMillis = currentDate.getTime();
            millis = date.getTime();
            //get the gap and subtract 41 hours.
            //41 hours means notification will be appear before 41 hours
            delay = (millis - currentMillis) - 147600000;
            if (delay <= 0 ) {
                delay = 20000;
            }
        } catch (DateTimeException | ParseException dateTimeException) {
            dateTimeException.getStackTrace();
        }
        scheduleNotification(getNotification(event.getEventName().concat("\n").concat("දිනය: ").concat(event.getEventDate())
                .concat("\n").concat("වෙලාව: ").concat(event.getEventTime()).concat("\n").concat("ස්ථානය: ")
                .concat(event.getEventPlace())), delay, num);
    }

    private void scheduleNotification (Notification notification, long delay, int num) {
        Intent notificationIntent = new Intent(this, NotificationPage.class);
        notificationIntent.putExtra("NOTIFICATION_ID", num);
        notificationIntent.putExtra("NOTIFICATION", notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, num-1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification (String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, default_notification_channel_id);
        builder.setContentTitle("පිංකම් දැනුම්දීම");
        builder.setContentText(content);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setAutoCancel(true);
        builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        return builder.build();
    }

    public void showErrorDialog(String errorMessage) {
        new SweetAlertDialog(UserEventCalendar.this, SweetAlertDialog.ERROR_TYPE)
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

//    public class FetchTempleDataForUser extends AsyncTask<String, Void, String> {
//
//        ProgressDialog progress = new ProgressDialog(UserEventCalendar.this);
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            progress.setTitle("Loading");
//            progress.setMessage("Wait while loading...");
//            progress.setCancelable(false);
//            progress.show();
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.common_temple);
//            try {
//                if (templesJson != null) {
//                    JSONObject fullObject = new JSONObject(templesJson);
//                    JSONArray keys = fullObject.names();
//
//                    for (int i = 0; keys.length() > i; i++) {
//                        String key = keys.getString(i);
//                        JSONObject temple = fullObject.getJSONObject(key);
//                        templeIdList.add(temple.getString("userId"));
//                        templeNamesList.add(temple.getString("templeName"));
//                        wiharadhipathiHimiNamesList.add(temple.getString("wiharadhipathiHimi"));
//                        addressList.add(temple.getString("templeAddress"));
//                        telNoList.add(temple.getString("telNo"));
//                        emailList.add(temple.getString("email"));
//                        descriptionList.add(temple.getString("userStatus"));
//                        templeImageList.add(Bitmap.createScaledBitmap(bitmap, 500, 500, true));
//                    }
//                } else {
//                    showErrorDialog("There is a internal server error");
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            executeListView(templeIdList, templeNamesList, wiharadhipathiHimiNamesList, telNoList, addressList, emailList, descriptionList,templeImageList);
//            progress.dismiss();
//        }
//
//        @Override
//        protected String doInBackground(String... stringsArray) {
//            HttpURLConnection urlConnection = null;
//            BufferedReader reader = null;
//
//            try {
//                final String BASE_URL = "https://pansala-android-project-default-rtdb.firebaseio.com/TEMPLE.json";
//                URL url = new URL(BASE_URL);
//
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//
//                InputStream inputStream = urlConnection.getInputStream();
//                StringBuffer buffer = new StringBuffer();
//
//                if (inputStream == null) {
//                    return null;
//                }
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                String line1;
//
//                while ((line1 = reader.readLine()) != null) {
//                    buffer.append(line1 + "\n");
//                }
//                if (buffer.length() == 0) {
//                    return null;
//                }
//                templesJson = buffer.toString();
//
//            } catch (IOException e) {
//                Log.e("Hi", "Error", e);
//                return null;
//            } finally {
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (final IOException e) {
//                        Log.e("Hi", "Error closing stream", e);
//                    }
//                }
//            }
//            return templesJson;
//        }
//    }
}