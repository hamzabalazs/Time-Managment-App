package com.example.tma.suggestion.service;

import com.example.tma.Event;
import com.example.tma.Priority;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//The suggestion service is giving suggestions to the user, taking in consideration the zone and the priority of the event
public class SuggestionService {
    private List<Event> events;

    public SuggestionService(List<Event> events) {
        this.events = events;
    }

//    public Event highPriority(Event firstEvent,Event secondEvent){
//        if(firstEvent.getPriorityLevel() == Priority.CRITICAL && secondEvent.getPriorityLevel() != Priority.CRITICAL){
//            return firstEvent;
//        }
//    }

    //Sorts the events(higher priority first)
    public void sortEventsByPriority() {
        Collections.sort(events,new Event.SortByPriority());
    }

    //Sorts the events by zones and within the zone by priority
    public void sortByZone(){


    }
}

