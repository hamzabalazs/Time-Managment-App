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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddEventActivity extends AppCompatActivity {

    Map<String, Object> event = new HashMap<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CalendarView calendar;
    EditText name, time;
    Spinner zone, priority;
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        name = findViewById(R.id.eventName);
        time = findViewById(R.id.eventTime);
        zone = findViewById(R.id.eventZone);
        priority = findViewById(R.id.eventPriority);
        calendar = findViewById(R.id.calendarView);
        button = findViewById(R.id.eventButton);

        zone.setAdapter(new ArrayAdapter<Zone>(this,R.layout.support_simple_spinner_dropdown_item,Zone.values()));
        priority.setAdapter(new ArrayAdapter<Priority>(this,R.layout.support_simple_spinner_dropdown_item,Priority.values()));

        FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
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
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    String mDate = sdf.format(new Date(calendar.getDate()));
                    String mTime = time.getText().toString().trim();
                    String mUid = CurrentUser.getUid();
                    String mName = name.getText().toString().trim();
                    String mZone = zone.getSelectedItem().toString();
                    String mPriority = priority.getSelectedItem().toString();

                    if(TextUtils.isEmpty(mName)){
                        name.setError("Event name is required!");
                        return;
                    }
                    if(TextUtils.isEmpty(mTime)){
                        time.setError("Event time is required!");
                        return;
                    }

                    event.put("UID",mUid);
                    event.put("name",mName);
                    event.put("time",mTime);
                    event.put("date",mDate);
                    event.put("zone",mZone);
                    event.put("priority",mPriority);

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