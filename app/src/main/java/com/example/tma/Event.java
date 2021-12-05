package com.example.tma;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;

public class Event {
    private int eventId;
    private String eventTitle;
    private String eventDescription;
    private Date eventCreatedOnTimestamp;
    private SimpleDateFormat eventStartsAtDate;
    private SimpleDateFormat eventEndsAtDate;
    private Priority priorityLevel;
    private Zone zoneOfTheEvent;

    public Event(int eventId, String eventTitle, String eventDescription, SimpleDateFormat eventStartsAtDate, SimpleDateFormat eventEndsAtDate,Priority priorityLevel,Zone zoneOfTheEvent) {
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventCreatedOnTimestamp = new Date(System.currentTimeMillis());
        this.eventStartsAtDate = eventStartsAtDate;
        this.eventEndsAtDate = eventEndsAtDate;
        this.priorityLevel = priorityLevel;
        this.zoneOfTheEvent = zoneOfTheEvent;
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

    public int getEventId() {
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

    public SimpleDateFormat getEventStartsAtDate() {
        return eventStartsAtDate;
    }

    public void setEventStartsAtDate(SimpleDateFormat eventStartsAtDate) {
        this.eventStartsAtDate = eventStartsAtDate;
    }

    public SimpleDateFormat getEventEndsAtDate() {
        return eventEndsAtDate;
    }

    public void setEventEndsAtDate(SimpleDateFormat eventEndsAtDate) {
        this.eventEndsAtDate = eventEndsAtDate;
    }

    public static class SortByPriority implements Comparator<Event>{

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

}
