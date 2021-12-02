package com.example.tma.suggestion.service;

import com.example.tma.Event;
import com.example.tma.Priority;
import com.example.tma.Zone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//The suggestion service is giving suggestions to the user, taking in consideration the zone and the priority of the event
public class SuggestionService {
    private final List<Event> events;

    public SuggestionService(List<Event> events) {
        this.events = events;
    }

//    public Event highPriority(Event firstEvent,Event secondEvent){
//        if(firstEvent.getPriorityLevel() == Priority.CRITICAL && secondEvent.getPriorityLevel() != Priority.CRITICAL){
//            return firstEvent;
//        }
//    }

    //Sorts the events(higher priority first)
    public void sortEventsByPriority(List<Event> events) {
        Collections.sort(events,new Event.SortByPriority());
    }

    //Sorts the events by zones and within the zone by priority
    public void sortByZone(){
        HashMap<Zone,List<Event>> eventsInZones =new HashMap<>();
        List<Event> nullListOfEvent = Collections.emptyList();

        //sort all the events by priority so the events will be added to the list by zones already sorted
        sortEventsByPriority(events);
        for (Event zoneEvent: events) {
            switch (zoneEvent.getZoneOfTheEvent()) {
                case TUDOR:
                    eventsInZones.put(Zone.TUDOR, nullListOfEvent);
                    eventsInZones.get(Zone.TUDOR).add(zoneEvent);
                    break;
                case LIBERTATII:
                    eventsInZones.put(Zone.LIBERTATII, nullListOfEvent);
                    eventsInZones.get(Zone.LIBERTATII).add(zoneEvent);
                    break;
                case DAMBU:
                    eventsInZones.put(Zone.DAMBU, nullListOfEvent);
                    eventsInZones.get(Zone.DAMBU).add(zoneEvent);
                    break;
                case UNIRII:
                    eventsInZones.put(Zone.UNIRII, nullListOfEvent);
                    eventsInZones.get(Zone.UNIRII).add(zoneEvent);
                    break;
                case SAPTENOIEMBRIE:
                    eventsInZones.put(Zone.SAPTENOIEMBRIE, nullListOfEvent);
                    eventsInZones.get(Zone.SAPTENOIEMBRIE).add(zoneEvent);
                    break;
                case MURESENI:
                    eventsInZones.put(Zone.MURESENI, nullListOfEvent);
                    eventsInZones.get(Zone.MURESENI).add(zoneEvent);
                    break;
                case CENTRU:
                    eventsInZones.put(Zone.CENTRU, nullListOfEvent);
                    eventsInZones.get(Zone.CENTRU).add(zoneEvent);
                    break;
            }
        }
        for (Zone key:eventsInZones.keySet()){
            List<Event> eventsSortedByZone= new LinkedList<>();
            List<Event> eventList = eventsInZones.get(key);
//                    forEach(event -> {
//                eventsSortedByZone.add(event);
//            });
        }

    }
}

