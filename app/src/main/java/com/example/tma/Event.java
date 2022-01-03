package com.example.tma;

import static android.content.ContentValues.TAG;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

public class Event {
    private String eventId;
    private String eventTitle;
    private String eventDescription;
    private Date eventCreatedOnTimestamp;
    private String eventStartsAtDate;
    private String eventEndsAtDate;
    private Priority priorityLevel;
    private Zone zoneOfTheEvent;
    private String userUid;
    private String selectedDate;

    public Event(String userUid, String selectedDate, String eventTitle, String eventDescription, String eventStartsAtDate, String eventEndsAtDate, Priority priorityLevel, Zone zoneOfTheEvent) {
        this.userUid = userUid;
        this.selectedDate = selectedDate;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventCreatedOnTimestamp = new Date(System.currentTimeMillis());
        this.eventStartsAtDate = eventStartsAtDate;
        this.eventEndsAtDate = eventEndsAtDate;
        this.priorityLevel = priorityLevel;
        this.zoneOfTheEvent = zoneOfTheEvent;
    }

    public static class SortByPriority implements Comparator<Event> {

        @Override
        public int compare(Event e1, Event e2) {
            if(e1.getPriorityLevel() == e2.getPriorityLevel()){
                return 0;
            }else{
                if(e1.getPriorityLevel() == Priority.CRITICAL && e2.getPriorityLevel() != Priority.CRITICAL){
                    return 1;
                }else {
                    if (e1.getPriorityLevel() == Priority.HIGH && e2.getPriorityLevel() != Priority.CRITICAL && e2.getPriorityLevel() != Priority.HIGH){
                        return 1;
                    }else{
                        if(e1.getPriorityLevel() == Priority.NORMAL && e2.getPriorityLevel() != Priority.CRITICAL && e2.getPriorityLevel() != Priority.HIGH && e2.getPriorityLevel() != Priority.NORMAL ){
                            return 1;
                        }else{
                            if(e1.getPriorityLevel() == Priority.LOW && e2.getPriorityLevel() != Priority.LOW){
                                return -1;
                            }
                        }
                    }
                }
            }

            if(e2.getPriorityLevel() == Priority.CRITICAL && e1.getPriorityLevel() != Priority.CRITICAL){
                return -1;
            }else {
                if (e2.getPriorityLevel() == Priority.HIGH && e1.getPriorityLevel() != Priority.CRITICAL && e1.getPriorityLevel() != Priority.HIGH){
                    return -1;
                }else{
                    if(e2.getPriorityLevel() == Priority.NORMAL && e1.getPriorityLevel() != Priority.CRITICAL && e1.getPriorityLevel() != Priority.HIGH && e1.getPriorityLevel() != Priority.NORMAL ){
                        return -1;
                    }else{
                        if(e2.getPriorityLevel() == Priority.LOW && e1.getPriorityLevel() != Priority.LOW){
                            return 1;
                        }
                    }
                }
            }
            return 0;
        }

    }

    public static class SortByStartTime implements Comparator<Event>{
        @Override
        public int compare(Event event, Event t1) {
            return t1.eventStartsAtDate.compareToIgnoreCase(event.eventStartsAtDate);
        }
    }

    public static void addEventOnClick(FirebaseFirestore db,String userUid, CalendarView calendarDate, NumberPicker startTimeHour, NumberPicker startTimeMin, NumberPicker endTimeHour, NumberPicker endTimeMin, EditText eventTitle, EditText eventDescription, Spinner zone, Spinner priority) {
        Map<String, Object> event = new HashMap<>();
        String eventStartTime = startTimeHour.getValue() + ":" + startTimeMin.getValue();
        String eventEndTime = endTimeHour.getValue() + ":" + endTimeMin.getValue();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String selectedDate = sdf.format(new Date(calendarDate.getDate()));
        Priority priority1 = Priority.NORMAL;
        for (Priority priority2 : Priority.values()) {
            if (priority.getSelectedItem().toString().trim() == priority2.name()) {
                priority1 = priority2;
            }
        }
        Zone zone2 = Zone.CENTRU;
        for (Zone zone1 : Zone.values()) {
            if (zone.getSelectedItem().toString() == zone1.name()) {
                zone2 = zone1;
            }
        }
        //userId can't be empty because then we don't know who created the event.
        if (userUid.isEmpty()) {
            Log.e("AddEventActivity", "Error creating the event, userId null.");
            return;
        }
        if (eventTitle.getText().toString().isEmpty()) {
            eventTitle.setError("Event title is required!");
            return;
        }
        //Create event for code
        Event event1 = new Event(userUid, selectedDate, eventTitle.getText().toString().trim(), "Empty", eventStartTime, eventEndTime, priority1, zone2);
        //Create an event as a map for the DB
        event.put("UID", userUid);
        event.put("Date", selectedDate);
        event.put("Title", eventTitle.getText().toString().trim());
        event.put("Description", eventDescription.getText().toString().trim());
        event.put("StartsAtDate", eventStartTime);
        event.put("EndsAtDate", eventEndTime);
        event.put("zone", zone.getSelectedItem().toString());
        event.put("priority", priority.getSelectedItem().toString());
        event.put("createdAtTimestamp", new Date(System.currentTimeMillis()));

        //Adding the created event to the db
        db.collection("events")
                .add(event)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Event has been added!");
                        event1.setEventId(db.collection("events").document().getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding event", e);
                    }
                });
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Zone getZoneOfTheEvent() {
        return zoneOfTheEvent;
    }

    public void setZoneOfTheEvent(Zone zoneOfTheEvent) {
        this.zoneOfTheEvent = zoneOfTheEvent;
    }

    public Priority getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(Priority priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public Date getEventCreatedOnTimestamp() {
        return eventCreatedOnTimestamp;
    }

    public String getEventStartsAtDate() {
        return eventStartsAtDate;
    }

    public void setEventStartsAtDate(String eventStartsAtDate) {
        this.eventStartsAtDate = eventStartsAtDate;
    }

    public String getEventEndsAtDate() {
        return eventEndsAtDate;
    }

    public void setEventEndsAtDate(String eventEndsAtDate) {
        this.eventEndsAtDate = eventEndsAtDate;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }
}
