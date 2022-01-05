package com.example.tma;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tma.suggestion.service.SuggestionService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.FutureCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    CollectionReference eventRef = db.collection("events");
    Button calendarButton;
    Button userButton;
    TextView welcomeView, dataView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarButton = findViewById(R.id.EventButton);
        userButton = findViewById(R.id.userInfoButton);
        welcomeView = findViewById(R.id.welcomeTextView);
        dataView = findViewById(R.id.currTaskTextView);

        db.collection("users").document(CurrentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot docsnap = task.getResult();
                            if (docsnap.exists()) {
                                String lastname = docsnap.get("Last Name").toString();
                                String message = "Welcome, " + lastname;
                                welcomeView.setText(message);
                            }
                        }
                    }
                });


        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddEventActivity.class));
            }
        });

        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DisplayUserInfoActivity.class));
            }
        });

        String UID = CurrentUser.getUid();
        SuggestionService suggestionService = new SuggestionService(UID);
        List<Event> eventList = new ArrayList<Event>();
        List<Event> finalEventList = eventList;
        db.collection("events").whereEqualTo("UID", UID)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot docsnap : task.getResult()) {
                        String eventId = docsnap.getId();
                        String selectedDate = docsnap.get("Date").toString();
                        String title = docsnap.get("Title").toString();
                        String startDate = docsnap.get("StartsAtDate").toString();
                        String endDate = docsnap.get("EndsAtDate").toString();
                        String description = docsnap.get("Description").toString();
                        String priority = docsnap.get("priority").toString();
                        String zone = docsnap.get("zone").toString();
                        Priority priorityLevel = Priority.valueOf(priority);
                        Zone  zoneOfTheEvent = Zone.valueOf(zone);
                        Event event = new Event(eventId, UID, selectedDate, title, description, startDate, endDate, priorityLevel, zoneOfTheEvent);
                        finalEventList.add(event);
                    }
                    String eventText = "";
                    if (finalEventList.size() == 0) {
                        eventText = "It looks like you have no tasks for today!";
                        dataView.setText(eventText);
                    }
                    if (finalEventList != null) {
                        for (Event event : finalEventList) {
                            String date = event.getSelectedDate();
                            Date c = Calendar.getInstance().getTime();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            String currDate = sdf.format(c);
                            if (!date.equals(currDate)) {
                                continue;
                            }
                            String title = event.getEventTitle();
                            String startDate = event.getEventStartsAtDate();
                            String endDate = event.getEventEndsAtDate();
                            String zone = event.getZoneOfTheEvent().toString();
                            String priority = event.getPriorityLevel().toString();

                            eventText = eventText + title + "\n" + startDate + " - " + endDate + "\n" + zone + "\n" + priority + "\n\n";
                            dataView.setText(eventText);
                        }
                    }
                }
            }
        });

    }


    public void logout(View v){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }


}