package com.amannmalik.service.icalgeneration.endpoint;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Language;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.parameter.Related;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Trigger;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

/**
 * Created by Amann on 8/21/2015.
 */
public class IcalFactory {

    public static Calendar generateCalendar() {

        Calendar calendar = new Calendar();
        PropertyList properties = calendar.getProperties();
        properties.add(Method.REQUEST);
        properties.add(new ProdId("Amann Malik's iCal Service"));
        properties.add(Version.VERSION_2_0);
        properties.add(CalScale.GREGORIAN);

        return calendar;
    }

    public static VEvent generateEvent(CalendarEvent event) throws ParseException, URISyntaxException {

        PropertyList properties = new PropertyList();
        properties.add(new Uid(UUID.randomUUID().toString()));

        addDetails(properties, event.title, event.description, event.location);
        addTimes(properties, event.timezone, LocalDateTime.now(), event.startInstant, event.endInstant);
        addOrganizer(properties, event.organizerName, event.organizerEmail);
        addAttendee(properties, event.attendeeName, event.attendeeEmail);

        VEvent vEvent = new VEvent(properties);

        VAlarm alarm = generateReminder(event.reminder);
        vEvent.getAlarms().add(alarm);

        return vEvent;
    }

    private static void addTimes(PropertyList properties, ZoneId timezone, LocalDateTime created, LocalDateTime start, LocalDateTime end) {
        DateTime currentTimestamp = new DateTime(created.atZone(timezone).toInstant().toEpochMilli());
        properties.add(new DtStamp(currentTimestamp));

        DateTime startTimestamp = new DateTime(start.atZone(timezone).toInstant().toEpochMilli());
        properties.add(new DtStart(startTimestamp));

        DateTime endTimestamp = new DateTime(end.atZone(timezone).toInstant().toEpochMilli());
        properties.add(new DtEnd(endTimestamp));
    }

    private static void addDetails(PropertyList properties, String summary, String description, String location) {
        ParameterList languageParams = new ParameterList();
        languageParams.add(new Language("en-US"));
        properties.add(new Summary(languageParams, summary));
        properties.add(new Description(languageParams, description));
        properties.add(new Location(languageParams, location));
        properties.add(Clazz.PUBLIC);
        properties.add(Status.VEVENT_CONFIRMED);
    }

    private static VAlarm generateReminder(int reminder) {
        VAlarm alarm = new VAlarm();
        alarm.getProperties().add(new Description("REMINDER"));
        ParameterList pl = new ParameterList();
        pl.add(new Related("START"));
        alarm.getProperties().add(new Trigger(pl, new Dur(0, 0, reminder, 0)));
        alarm.getProperties().add(new Action("DISPLAY"));
        return alarm;
    }

    private static void addOrganizer(PropertyList properties, String organizerName, String organizerEmail) throws URISyntaxException {
        ParameterList params = new ParameterList();
        params.add(new Cn(organizerName));
        properties.add(new Organizer(params, "MAILTO:" + organizerEmail));

        addAttendee(properties, organizerName, organizerEmail);
    }

    private static void addAttendee(PropertyList properties, String attendeeName, String attendeeEmail) throws URISyntaxException {
        ParameterList params = new ParameterList();
        params.add(Role.REQ_PARTICIPANT);
        params.add(PartStat.ACCEPTED);
        params.add(new Cn(attendeeName));
        properties.add(new Attendee(params, "MAILTO:" + attendeeEmail));
    }

}
