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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

/**
 * Created by Amann Malik (amannmalik@gmail.com) on 7/12/2015.
 */


@WebServlet
public class IcalGenerationEndpoint extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Map<String, String[]> map = req.getParameterMap();

        String filename = map.containsKey("filename") ? map.get("filename")[0] : "event.ics";
        if (!filename.endsWith(".ics")) {
            filename = filename + ".ics";
        }

        String title = map.containsKey("title") ? map.get("title")[0] : null;
        String description = map.containsKey("description") ? map.get("description")[0] : null;
        String organizerName = map.containsKey("organizerName") ? map.get("organizerName")[0] : null;
        String organizerEmail = map.containsKey("organizerEmail") ? map.get("organizerEmail")[0] : null;
        String attendeeName = map.containsKey("attendeeName") ? map.get("attendeeName")[0] : null;
        String attendeeEmail = map.containsKey("attendeeEmail") ? map.get("attendeeEmail")[0] : null;
        String location = map.containsKey("location") ? map.get("location")[0] : null;
        int alarm = map.containsKey("reminderOffset") ? Integer.parseInt(map.get("reminderOffset")[0]) : -15;



        String startTimestampString = map.containsKey("startTimestamp") ? map.get("startTimestamp")[0] : null;
        String endTimestampString = map.containsKey("endTimestamp") ? map.get("endTimestamp")[0] : null;
        LocalDateTime startInstant = LocalDateTime.parse(startTimestampString);
        LocalDateTime endInstant = LocalDateTime.parse(endTimestampString);

        String timezoneString = map.containsKey("timezone") ? map.get("timezone")[0] : null;
        ZoneId timezone = ZoneId.of(timezoneString);


        CalendarEvent calendarEvent = new CalendarEvent(
                title,
                description,
                organizerName,
                organizerEmail,
                attendeeName,
                attendeeEmail,
                location,
                startInstant,
                endInstant,
                timezone,
                alarm
        );


        Calendar calendar = IcalFactory.generateCalendar();
        try {
            VEvent vEvent = IcalFactory.generateEvent(calendarEvent);
            calendar.getComponents().add(vEvent);
        } catch (ParseException | URISyntaxException e) {
            LoggerFactory.getLogger(IcalGenerationEndpoint.class).error("error while generating ical event", e);
            resp.sendError(500);
            return;
        }

        resp.setContentType("text/calendar");
        resp.setHeader("Content-Disposition", "attachment; filename=" + filename);

        try (OutputStream stream = resp.getOutputStream()) {
            CalendarOutputter outputter = new CalendarOutputter();
            outputter.output(calendar, stream);
        } catch (ValidationException e) {
            LoggerFactory.getLogger(IcalGenerationEndpoint.class).error("exception while writing ical response", e);
            resp.sendError(500);
        }

    }


}