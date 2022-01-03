package com.example.tma.suggestion.service;

import androidx.annotation.NonNull;

import com.example.tma.Event;
import com.example.tma.Priority;
import com.example.tma.Zone;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
    private List<Event> events;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();

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

    public SuggestionService(FirebaseUser currentUser) {
        DocumentReference docRef = db.collection("events").document(currentUser.getUid());
//        Event event = new Event(currentUser.getUid(),currentUser.getEventTitle,currentUser.getEventDescription,currentUser.getEventStartsAtDate,currentUser.getEventEndsAtDate,currentUser.)
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        for (Map.Entry<String, Object> dataMap : document.getData().entrySet()) {
                            String key = dataMap.getKey();
                            String value = (String) dataMap.getValue();
                            switch (key) {
                                case "UID":
                                    userUid = value;
                                    break;
                                case "StartsAtDate":
                                    eventStartsAtDate = value;
                                    break;
                                case "EndsAtDate":
                                    eventEndsAtDate = value;
                                    break;
                                case "Date":
                                    selectedDate = value;
                                    break;
                                case "Title":
                                    eventTitle = value;
                                    break;
                                case "Description":
                                    eventDescription = value;
                                    break;
                                case "zone": zoneOfTheEvent = Zone.valueOf(value);
                                    break;
                                case "priority": priorityLevel = Priority.valueOf(value);
                                    break;
                                case "createdAtTimestamp": eventCreatedOnTimestamp = new Date(value);
                                    break;
                            }
                            Event event = new Event(userUid,selectedDate,eventTitle,eventDescription,eventStartsAtDate,eventEndsAtDate,priorityLevel,zoneOfTheEvent);
                            events.add(event);
                        }
                    } else {
                        System.out.println("No such document");
                    }
                } else {
                    System.out.println("Task failed with " + task.getException());
                }
            }
        });
    }


    //Sorts the events(higher priority first)
    public void sortEventsByPriority(List<Event> events) {
        Collections.sort(events, new Event.SortByPriority());
    }

    //Sorts the events by zones and within the zone by priority
    public void sortByZone() {
        HashMap<Zone, List<Event>> eventsInZones = new HashMap<>();
        List<Event> nullListOfEvent = Collections.emptyList();

        //sort all the events by priority so the events will be added to the list by zones already sorted
        sortEventsByPriority(events);

        //Initialize for every zone an emptyList
        eventsInZones.put(Zone.TUDOR, nullListOfEvent);
        eventsInZones.put(Zone.LIBERTATII, nullListOfEvent);
        eventsInZones.put(Zone.DAMBU, nullListOfEvent);
        eventsInZones.put(Zone.UNIRII, nullListOfEvent);
        eventsInZones.put(Zone.SAPTENOIEMBRIE, nullListOfEvent);
        eventsInZones.put(Zone.MURESENI, nullListOfEvent);
        eventsInZones.put(Zone.CENTRU, nullListOfEvent);

        for (Event zoneEvent : events) {
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
        events.clear();
        events.addAll(eventListSortedByZones);

    }

    //If the parameter passed is false it means that the method sorts the whole list of events by starting time
    //if it's true it sorts the events only in the zones for every zone which will help for the sortByZone() method.
    public void sortByStartTime(Boolean sortByTimeInZones) {
        if (sortByTimeInZones) {
            List<Event> eventsSortedByTime = Collections.emptyList();

            //sort the whole list of events
            sortByZone();
            Zone zone = events.get(0).getZoneOfTheEvent();
            for (int i = 0; i < events.size(); i++) {
                int helpIndexEnd = 0;
                int helpIndexStart = 0;
                if (events.get(i).getZoneOfTheEvent() != events.get(i + 1).getZoneOfTheEvent()) {
                    helpIndexEnd = i;
                    List<Event> helperListOfEvents = events.subList(helpIndexStart, helpIndexEnd);
                    Collections.sort(helperListOfEvents, new Event.SortByStartTime());
                    eventsSortedByTime.addAll(helperListOfEvents);
                    helpIndexStart = i + 1;

                }
            }
            events.clear();
            events.addAll(eventsSortedByTime);
        } else {
            Collections.sort(events, new Event.SortByStartTime());
        }
    }
}

