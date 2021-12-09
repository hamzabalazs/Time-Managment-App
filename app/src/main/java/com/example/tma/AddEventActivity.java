package com.example.tma;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class AddEventActivity extends AppCompatActivity {

    Map<String, Object> event = new HashMap<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    NumberPicker startTimeHour,startTimeMin,endTimeHour,endTimeMin;
    TextView startTimeTextView,endTimeTextView;
    CalendarView calendarV;
    EditText name;
    Spinner zone, priority;
    Button button;
    Calendar calendar;
    TimeZone tz;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        name = findViewById(R.id.eventName);
        zone = findViewById(R.id.eventZone);
        priority = findViewById(R.id.eventPriority);
        calendarV = findViewById(R.id.calendarView);
        button = findViewById(R.id.eventButton);
        calendar = Calendar.getInstance();
        tz = TimeZone.getDefault();
        startTimeTextView = findViewById(R.id.startTimeText);
        startTimeHour = findViewById(R.id.StartNumPickHour);
        startTimeMin = findViewById(R.id.StartNumPickMin);
        endTimeTextView = findViewById(R.id.endTimeText);
        endTimeHour = findViewById(R.id.EndNumPickHour);
        endTimeMin = findViewById(R.id.EndNumPickMin);

        zone.setAdapter(new ArrayAdapter<Zone>(this,R.layout.support_simple_spinner_dropdown_item,Zone.values()));
        priority.setAdapter(new ArrayAdapter<Priority>(this,R.layout.support_simple_spinner_dropdown_item,Priority.values()));

        FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        calendarV.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                name.setVisibility(View.VISIBLE);
                zone.setVisibility(View.VISIBLE);
                priority.setVisibility(View.VISIBLE);
                startTimeTextView.setVisibility(View.VISIBLE);
                startTimeHour.setVisibility(View.VISIBLE);
                startTimeMin.setVisibility(View.VISIBLE);
                endTimeTextView.setVisibility(View.VISIBLE);
                endTimeHour.setVisibility(View.VISIBLE);
                endTimeMin.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
                startTimeHour.setMinValue(0);
                startTimeHour.setMaxValue(23);
                startTimeMin.setMinValue(0);
                startTimeMin.setMaxValue(59);
                endTimeHour.setMinValue(0);
                endTimeHour.setMaxValue(23);
                endTimeMin.setMinValue(0);
                endTimeMin.setMaxValue(59);

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(event!=null){
                    //Event event = new Event();
                    SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
                    String eventDate = sdfDate.format(new Date(calendarV.getDate()));
                    String userUid = CurrentUser.getUid();
                    String eventTitle = name.getText().toString().trim();
                    String eventZone = zone.getSelectedItem().toString();
                    String eventPriority = priority.getSelectedItem().toString();
                    calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
                    SimpleDateFormat sdfTimeZone = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    Date currentTimeZone = new Date((long)1379487711*1000);
                    String mCurrentTimeZone = sdfTimeZone.format(currentTimeZone);
                    String eventStartTime = startTimeHour.getValue() + ":" + startTimeMin.getValue();
                    String eventEndTime = endTimeHour.getValue() + ":" + endTimeMin.getValue();


                    if(TextUtils.isEmpty(eventTitle)){
                        name.setError("Event name is required!");
                        return;
                    }


                    CollectionReference events = db.collection("events");

                    event.put("UID",userUid);
                    event.put("Title",eventTitle);
                    event.put("date",eventDate);
                    event.put("zone",eventZone);
                    event.put("priority",eventPriority);
                    event.put("lastUpdate",mCurrentTimeZone);
                    event.put("startTime",eventStartTime);
                    event.put("endTime",eventEndTime);




                    db.collection("events")
                            .add(event)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "Event has been added!");
                                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG,"Error adding event",e);
                                }
                            });
                }
            }
        });

    }
}