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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class AddEventActivity extends AppCompatActivity {

    //Map<String, Object> event = new HashMap<>();
    String date = "";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    NumberPicker startTimeHour, startTimeMin, endTimeHour, endTimeMin;
    TextView startTimeTextView, endTimeTextView;
    CalendarView calendarV;
    EditText name, description;
    Spinner zone, priority;
    Button button;
    Calendar calendar;
    TimeZone tz;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        name = findViewById(R.id.eventName);
        description = findViewById(R.id.eventDescription);
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
        calendarV.setMinDate(calendarV.getDate());

        zone.setAdapter(new ArrayAdapter<Zone>(this, R.layout.support_simple_spinner_dropdown_item, Zone.values()));
        priority.setAdapter(new ArrayAdapter<Priority>(this, R.layout.support_simple_spinner_dropdown_item, Priority.values()));

        FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        calendarV.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                date = "";
                String Day = String.valueOf(dayOfMonth);
                String Year = String.valueOf(year);
                month++;
                String Month = String.valueOf(month);
                if(dayOfMonth < 10 && month < 10){
                    date = "0" + Day+"/0"+Month+"/"+Year;
                }
                if(dayOfMonth < 10 && month >= 10){
                    date = "0" + Day+"/"+Month+"/"+Year;
                }
                if(dayOfMonth >= 10 && month < 10){
                    date = Day+"/0"+Month+"/"+Year;
                }
                if(dayOfMonth >= 10 && month >= 10){
                    date = Day+"/"+Month+"/"+Year;
                }
                name.setVisibility(View.VISIBLE);
                description.setVisibility(View.VISIBLE);
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

                Event.addEventOnClick(db,

                        CurrentUser.getUid(),
                        date,
                        startTimeHour,
                        startTimeMin,
                        endTimeHour,
                        endTimeMin,
                        name,
                        description,
                        zone,
                        priority
                );
                startActivity(new Intent(getApplicationContext(), MainActivity.class));

            }
        });

    }
}