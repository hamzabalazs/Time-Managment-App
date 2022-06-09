package com.example.tma;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CompleteEventActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    TextView dataView, back;
    Button eventCompletedButton, eventNotFinishedButton;
    private List<Event> eventek = new ArrayList<Event>();
    private int counter = 0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_event);
        dataView = findViewById(R.id.eventView);
        back = findViewById(R.id.backToMain2);
        eventCompletedButton = findViewById(R.id.eventCompleteDButton);
        eventNotFinishedButton = findViewById(R.id.eventNotCompleted);


        db.collection("events").whereEqualTo("UID", currentUser.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot docsnap : queryDocumentSnapshots) {
                    String eventId = docsnap.getId();
                    String selectedDate = docsnap.get("Date").toString();
                    String title = docsnap.get("Title").toString();
                    String startDate = docsnap.get("StartsAtDate").toString();
                    String endDate = docsnap.get("EndsAtDate").toString();
                    String description = docsnap.get("Description").toString();
                    String priority = docsnap.get("priority").toString();
                    String zone = docsnap.get("zone").toString();
                    Priority priorityLevel = Priority.valueOf(priority);
                    Zone zoneOfTheEvent = Zone.valueOf(zone);
                    Boolean completed = (Boolean) docsnap.get("completed");
                    Event event = new Event(eventId, currentUser.getUid(), selectedDate, title, description, startDate, endDate, priorityLevel, zoneOfTheEvent, completed);
                    if (!event.isCompleted()) {
                        eventek.add(event);
                    }
                }
            }
        });
        if (counter == 0) {
            if (eventek.get(counter + 1) == null) {
                dataView.setText("There are no events to complete");
            } else {
                dataView.setText(getEventTextForIndex(counter));
            }

        }

        eventCompletedButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if (eventek.get(counter + 1) == null) {
                    dataView.setText("There are no events to complete");
                } else {
                    db.collection("events").document(eventek.get(counter).getEventId()).update("completed", true);
                    dataView.setText(getEventTextForIndex(++counter));
                }
            }
        });

        eventNotFinishedButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if (eventek.get(counter + 1) == null) {
                    dataView.setText("There are no events to complete");
                } else {
                    dataView.setText(getEventTextForIndex(++counter));
                }

            }
        });

    }

    public void BacktoMain(View v) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    public String getEventTextForIndex(int index) {
        String eventText = "";
        if (!eventek.get(index).isCompleted()) {
//            String date = eventek.get(index).getSelectedDate();
//            Date c = Calendar.getInstance().getTime();
//            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//            String currDate = sdf.format(c);

            String title = eventek.get(index).getEventTitle();
            String startDate = eventek.get(index).getEventStartsAtDate();
            String endDate = eventek.get(index).getEventEndsAtDate();
            String description = eventek.get(index).getEventDescription();
            String priority = eventek.get(index).getPriorityLevel().toString();
            String zone = eventek.get(index).getZoneOfTheEvent().toString();

            eventText = eventText + title + "\n" + description + "\n" + startDate + " - " + endDate + "\n" + zone + "\n" + priority + "\n\n";

        }

        return eventText;
    }
}
