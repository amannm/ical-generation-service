package com.amannmalik.service.icalgeneration.endpoint;


import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.Instant;
import java.util.Map;

/**
 * Created by Amann Malik (amannmalik@gmail.com) on 7/12/2015.
 */


@WebServlet
public class IcalGenerationEndpoint extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Map<String, String[]> map = req.getParameterMap();

        String eventName = map.containsKey("eventName") ? map.get("eventName")[0] : null;
        String eventDescription = map.containsKey("eventDescription") ? map.get("eventDescription")[0] : null;
        String eventSummary = map.containsKey("eventSummary") ? map.get("eventSummary")[0] : null;
        String organizerName = map.containsKey("organizerName") ? map.get("organizerName")[0] : null;
        String organizerEmail = map.containsKey("organizerEmail") ? map.get("organizerEmail")[0] : null;
        String attendeeName = map.containsKey("attendeeName") ? map.get("attendeeName")[0] : null;
        String attendeeEmail = map.containsKey("attendeeEmail") ? map.get("attendeeEmail")[0] : null;
        String location = map.containsKey("location") ? map.get("location")[0] : null;
        String startTimestampString = map.containsKey("startTimestamp") ? map.get("startTimestamp")[0] : null;
        String endTimestampString = map.containsKey("endTimestamp") ? map.get("endTimestamp")[0] : null;
        int alarm = map.containsKey("alarmOffset") ? Integer.parseInt(map.get("alarmOffset")[0]) : -15;


        Instant startInstant = Instant.parse(startTimestampString);
        Instant endInstant = Instant.parse(endTimestampString);

        CalendarEvent calendarEvent = new CalendarEvent(
                eventName,
                eventDescription,
                eventSummary,
                organizerName,
                organizerEmail,
                attendeeName,
                attendeeEmail,
                location,
                startInstant,
                endInstant,
                alarm
        );


        Calendar calendar = IcalFactory.generateCalendar();
        try {
            VEvent vEvent = IcalFactory.generateEvent(calendarEvent);
            calendar.getComponents().add(vEvent);
        } catch (ParseException | URISyntaxException e) {
            LoggerFactory.getLogger(IcalGenerationEndpoint.class).error("", e);
            resp.sendError(500);
            return;
        }

        resp.setContentType("text/calendar");

        try (OutputStream stream = resp.getOutputStream()) {
            CalendarOutputter outputter = new CalendarOutputter();
            outputter.output(calendar, stream);
        } catch (ValidationException e) {
            LoggerFactory.getLogger(IcalGenerationEndpoint.class).error("", e);
            resp.sendError(500);
        }

    }


}