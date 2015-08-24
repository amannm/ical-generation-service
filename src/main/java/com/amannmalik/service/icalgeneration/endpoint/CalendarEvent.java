package com.amannmalik.service.icalgeneration.endpoint;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created by Amann on 8/21/2015.
 */
public class CalendarEvent {

    public final String title;
    public final String description;
    public final String organizerName;
    public final String organizerEmail;
    public final String attendeeName;
    public final String attendeeEmail;
    public final String location;
    public final LocalDateTime startInstant;
    public final LocalDateTime endInstant;
    public final ZoneId timezone;
    public final int reminder;

    public CalendarEvent(String title, String description, String organizerName, String organizerEmail, String attendeeName, String attendeeEmail, String location, LocalDateTime startInstant, LocalDateTime endInstant, ZoneId timezone, int reminder) {
        this.title = title;
        this.description = description;
        this.organizerName = organizerName;
        this.organizerEmail = organizerEmail;
        this.attendeeName = attendeeName;
        this.attendeeEmail = attendeeEmail;
        this.location = location;
        this.startInstant = startInstant;
        this.endInstant = endInstant;
        this.timezone = timezone;
        this.reminder = reminder;
    }
}
