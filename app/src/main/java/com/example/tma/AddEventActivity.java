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
import android.widget.Spinner;

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
    CalendarView calendarV;
    EditText name, time;
    Spinner zone, priority;
    Button button;
    Calendar calendar;
    TimeZone tz;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        name = findViewById(R.id.eventName);
        time = findViewById(R.id.eventTime);
        zone = findViewById(R.id.eventZone);
        priority = findViewById(R.id.eventPriority);
        calendarV = findViewById(R.id.calendarView);
        button = findViewById(R.id.eventButton);
        calendar = Calendar.getInstance();
        tz = TimeZone.getDefault();

        zone.setAdapter(new ArrayAdapter<Zone>(this,R.layout.support_simple_spinner_dropdown_item,Zone.values()));
        priority.setAdapter(new ArrayAdapter<Priority>(this,R.layout.support_simple_spinner_dropdown_item,Priority.values()));

        FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        calendarV.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                name.setVisibility(View.VISIBLE);
                time.setVisibility(View.VISIBLE);
                zone.setVisibility(View.VISIBLE);
                priority.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(event!=null){
                    //Event event = new Event();
                    SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
                    String mDate = sdfDate.format(new Date(calendarV.getDate()));
                    String mTime = time.getText().toString().trim();
                    String mUid = CurrentUser.getUid();
                    String mName = name.getText().toString().trim();
                    String mZone = zone.getSelectedItem().toString();
                    String mPriority = priority.getSelectedItem().toString();
                    calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
                    SimpleDateFormat sdfTimeZone = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    Date currentTimeZone = new Date((long)1379487711*1000);
                    String mCurrentTimeZone = sdfTimeZone.format(currentTimeZone);


                    
                    if(TextUtils.isEmpty(mName)){
                        name.setError("Event name is required!");
                        return;
                    }
                    if(TextUtils.isEmpty(mTime)){
                        time.setError("Event time is required!");
                        return;
                    }

                    CollectionReference events = db.collection("events");

                    event.put("UID",mUid);
                    event.put("name",mName);
                    event.put("time",mTime);
                    event.put("date",mDate);
                    event.put("zone",mZone);
                    event.put("priority",mPriority);
                    event.put("lastUpdate",mCurrentTimeZone);




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