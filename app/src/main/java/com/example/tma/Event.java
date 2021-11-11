package com.example.tma;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class Event {
    private int eventId;
    private String eventTitle;
    private String eventDescription;
    private Date eventCreatedOnTimestamp;
    private SimpleDateFormat eventStartsAtDate;
    private SimpleDateFormat eventEndsAtDate;
    private Priority priorityLevel;

    public Event(int eventId, String eventTitle, String eventDescription, SimpleDateFormat eventStartsAtDate, SimpleDateFormat eventEndsAtDate,Priority priorityLevel) {
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventCreatedOnTimestamp = new Date(System.currentTimeMillis());
        this.eventStartsAtDate = eventStartsAtDate;
        this.eventEndsAtDate = eventEndsAtDate;
        this.priorityLevel = priorityLevel;
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
}
