package com.amannmalik.service.icalgeneration.endpoint;

import java.time.Instant;

/**
 * Created by Amann on 8/21/2015.
 */
public class CalendarEvent {

    public final String eventName;
    public final String eventDescription;
    public final String eventSummary;
    public final String organizerName;
    public final String organizerEmail;
    public final String attendeeName;
    public final String attendeeEmail;
    public final String location;
    public final Instant startInstant;
    public final Instant endInstant;
    public final int reminder;

    public CalendarEvent(String eventName, String eventDescription, String eventSummary, String organizerName, String organizerEmail, String attendeeName, String attendeeEmail, String location, Instant startInstant, Instant endInstant, int reminder) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventSummary = eventSummary;
        this.organizerName = organizerName;
        this.organizerEmail = organizerEmail;
        this.attendeeName = attendeeName;
        this.attendeeEmail = attendeeEmail;
        this.location = location;
        this.startInstant = startInstant;
        this.endInstant = endInstant;
        this.reminder = reminder;
    }
}
