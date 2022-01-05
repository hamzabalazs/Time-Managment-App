package com.example.tma.suggestion.service;

import androidx.annotation.NonNull;

import com.example.tma.Event;
import com.example.tma.Priority;
import com.example.tma.Zone;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.QueryListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

//The suggestion service is giving suggestions to the user, taking in consideration the zone and the priority of the event
public class SuggestionService {
    private List<Event> events = Collections.synchronizedList(new ArrayList<Event>());
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Priority priorityLevel;
    private Zone zoneOfTheEvent;

    public SuggestionService(String UID) {
        getEventsFromDb(UID);
    }
    public void getEventsFromDb(String UID){
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
                        priorityLevel = Priority.valueOf(priority);
                        zoneOfTheEvent = Zone.valueOf(zone);
                        Event event = new Event(eventId, UID, selectedDate, title, description, startDate, endDate, priorityLevel, zoneOfTheEvent);
                        events.add(event);
                    }
                }else{
                    if(task.isComplete()){
                        System.out.println("Creating event done");
                    }
                }
            }
        });
    }

    //Sorts the events(higher priority first)
    public void sortEventsByPriority(List<Event> events) {
        Collections.sort(events, new Event.SortByPriority());
    }

    //Sorts the events by zones and within the zone by priority
    public void sortByZone(List<Event> eventsToSort) {
        HashMap<Zone, List<Event>> eventsInZones = new HashMap<>();
        List<Event> nullListOfEvent = Collections.emptyList();

        //sort all the events by priority so the events will be added to the list by zones already sorted
        sortEventsByPriority(eventsToSort);

        //Initialize for every zone an emptyList
        eventsInZones.put(Zone.TUDOR, nullListOfEvent);
        eventsInZones.put(Zone.LIBERTATII, nullListOfEvent);
        eventsInZones.put(Zone.DAMBU, nullListOfEvent);
        eventsInZones.put(Zone.UNIRII, nullListOfEvent);
        eventsInZones.put(Zone.SAPTENOIEMBRIE, nullListOfEvent);
        eventsInZones.put(Zone.MURESENI, nullListOfEvent);
        eventsInZones.put(Zone.CENTRU, nullListOfEvent);

        for (Event zoneEvent : eventsToSort) {
            switch (zoneEvent.getZoneOfTheEvent()) {
                case TUDOR:
                    eventsInZones.get(Zone.TUDOR).add(zoneEvent);
                    break;
                case LIBERTATII:
                    eventsInZones.get(Zone.LIBERTATII).add(zoneEvent);
                    break;
                case DAMBU:
                    eventsInZones.get(Zone.DAMBU).add(zoneEvent);
                    break;
                case UNIRII:
                    eventsInZones.get(Zone.UNIRII).add(zoneEvent);
                    break;
                case SAPTENOIEMBRIE:
                    eventsInZones.get(Zone.SAPTENOIEMBRIE).add(zoneEvent);
                    break;
                case MURESENI:
                    eventsInZones.get(Zone.MURESENI).add(zoneEvent);
                    break;
                case CENTRU:
                    eventsInZones.get(Zone.CENTRU).add(zoneEvent);
                    break;
                default:
                    System.out.println("Invalid zone for an event");
            }
        }
        //Create a new list of events with all of the zones already in sorted order by zones and by priority
        List<Event> eventListSortedByZones = new LinkedList<>();
        for (Zone key : eventsInZones.keySet()) {
            for (int i = 0; i < eventsInZones.get(key).size(); i++) {
                eventListSortedByZones.add(eventsInZones.get(key).get(i));
            }
        }

        //Delete the unsorted events from the list and add the sorted one to it instead.
        eventsToSort.clear();
        eventsToSort.addAll(eventListSortedByZones);

    }

    //If the parameter passed is false it means that the method sorts the whole list of events by starting time
    //if it's true it sorts the events only in the zones for every zone which will help for the sortByZone() method.
    public void sortByStartTime(Boolean sortByTimeInZones,List<Event> eventsToSort) {
        if (sortByTimeInZones) {
            List<Event> eventsSortedByTime = Collections.emptyList();

            //sort the whole list of events
            sortByZone(eventsToSort);
            Zone zone = events.get(0).getZoneOfTheEvent();
            for (int i = 0; i < eventsToSort.size(); i++) {
                int helpIndexEnd = 0;
                int helpIndexStart = 0;
                if (eventsToSort.get(i).getZoneOfTheEvent() != eventsToSort.get(i + 1).getZoneOfTheEvent()) {
                    helpIndexEnd = i;
                    List<Event> helperListOfEvents = eventsToSort.subList(helpIndexStart, helpIndexEnd);
                    Collections.sort(helperListOfEvents, new Event.SortByStartTime());
                    eventsSortedByTime.addAll(helperListOfEvents);
                    helpIndexStart = i + 1;

                }
            }
            eventsToSort.clear();
            eventsToSort.addAll(eventsSortedByTime);
        } else {
            Collections.sort(eventsToSort, new Event.SortByStartTime());
        }
    }

    public static SuggestionService getSuggestionService(String UID){
        return  new SuggestionService(UID);
    }

    public List<Event> getEvents() {
        return events;
    }
}

