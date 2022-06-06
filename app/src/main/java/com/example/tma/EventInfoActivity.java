package com.example.tma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tma.suggestion.service.SuggestionService;
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

public class EventInfoActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    TextView back,dataView;
    SuggestionService service = new SuggestionService(currentUser.getUid());
    private List<Event> eventek = new ArrayList<Event>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);

        dataView = findViewById(R.id.eventListView);
        dataView.setMovementMethod(new ScrollingMovementMethod());
        back = findViewById(R.id.backToMain);



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
                    Boolean completed = (Boolean) docsnap.get("completed");
                    Event event = new Event(eventId,currentUser.getUid(), selectedDate, title, description, startDate, endDate, priorityLevel, zoneOfTheEvent,completed);
                    eventek.add(event);
                }
                String eventText = "";
                for(Event event : eventek){
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
                    String description = event.getEventDescription();
                    String priority = event.getPriorityLevel().toString();
                    String zone = event.getZoneOfTheEvent().toString();
                    Boolean completed = event.isCompleted();
                    String completedOrNot = "";
                    if(completed){
                        completedOrNot = "This task is done";
                    }else{
                        completedOrNot = "This task isn't done";
                    }

                    eventText = eventText + title + "\n" + description + "\n" + startDate + " - " + endDate + "\n" + zone + "\n" + priority + "\n"+ completedOrNot +"\n\n";
                }
                dataView.setText(eventText);

            }
        });
//        String complete
        dataView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu popupMenu = new PopupMenu(EventInfoActivity.this, view);

                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        // Toast message on menu item clicked
                        Toast.makeText(EventInfoActivity.this, "You Clicked " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                // Showing the popup menu
                popupMenu.show();
                return true;
            }
        });





    }


    public void BacktoMain(View v) {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }

    public void sortByPriority(View v){

                String eventText = "";
                dataView.setText(R.string.defaultString);
                service.sortEventsByPriority(eventek);

                for(Event event : eventek){
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
                    String description = event.getEventDescription();
                    String priority = event.getPriorityLevel().toString();
                    String zone = event.getZoneOfTheEvent().toString();

                    eventText = eventText + title + "\n" + description + "\n" + startDate + " - " + endDate + "\n" + zone + "\n" + priority + "\n\n";
                }
                dataView.setText(eventText);

    }

    public void sortByZoneAndPriority(View v){
        String eventText = "";
        dataView.setText(R.string.defaultString);
        service.sortByZone(eventek);


        for(Event event : eventek){
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
            String description = event.getEventDescription();
            String priority = event.getPriorityLevel().toString();
            String zone = event.getZoneOfTheEvent().toString();

            eventText = eventText + title + "\n" + description + "\n" + startDate + " - " + endDate + "\n" + zone + "\n" + priority + "\n\n";
        }
        dataView.setText(eventText);

    }

    public void sortByTimeAndZone(View v){
        String eventText = "";
        dataView.setText(R.string.defaultString);
        service.sortByStartTime(true,eventek);


        for(Event event : eventek){
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
            String description = event.getEventDescription();
            String priority = event.getPriorityLevel().toString();
            String zone = event.getZoneOfTheEvent().toString();

            eventText = eventText + title + "\n" + description + "\n" + startDate + " - " + endDate + "\n" + zone + "\n" + priority + "\n\n";
        }
        dataView.setText(eventText);

    }

    public void sortByTime(View v){
        String eventText = "";
        dataView.setText(R.string.defaultString);
        service.sortByStartTime(false,eventek);


        for(Event event : eventek){
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
            String description = event.getEventDescription();
            String priority = event.getPriorityLevel().toString();
            String zone = event.getZoneOfTheEvent().toString();

            eventText = eventText + title + "\n" + description + "\n" + startDate + " - " + endDate + "\n" + zone + "\n" + priority + "\n\n";
        }
        dataView.setText(eventText);

    }



}