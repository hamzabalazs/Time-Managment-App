package com.example.tma;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventInfoActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    TextView sort1,sort2,sort3,sort4,back,dataView;
    private List<Event> events = new ArrayList<Event>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);

        dataView.findViewById(R.id.eventListView);



        db.collection("events").whereEqualTo("UID",currentUser.getUid())
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
                    Event event = new Event(eventId,currentUser.getUid(), selectedDate, title, description, startDate, endDate, priorityLevel, zoneOfTheEvent);
                    events.add(event);
                }
            }
        });

        //createEventText(events);



        sort1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void createEventText(List<Event> events){
        String eventText = "";
        for(Event event : events){
            String title = event.getEventTitle();
            String startDate = event.getEventStartsAtDate();
            String endDate = event.getEventEndsAtDate();
            String description = event.getEventDescription();
            String priority = event.getPriorityLevel().toString();
            String zone = event.getZoneOfTheEvent().toString();

            eventText = eventText + title + "\n" + description + "\n" + startDate + " - " + endDate + "\n" + zone + "\n" + priority + "\n\n";
        }
        dataView.setText(eventText);
    }
}