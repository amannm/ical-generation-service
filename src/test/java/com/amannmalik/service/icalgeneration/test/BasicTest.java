package com.amannmalik.service.icalgeneration.test;

import com.amannmalik.service.icalgeneration.Server;
import com.amannmalik.service.test.LocalEnvironment;
import com.amannmalik.service.test.ServiceRequestFactory;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.Method;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Amann on 8/21/2015.
 */
public class BasicTest {


    private static int testPort = LocalEnvironment.findRandomOpenPort();
    private static String testScheme = "http://";
    private static String testAddress = "127.0.0.1";
    private static String testEndpoint = "/ical";
    private static Server server;

    @BeforeClass
    public static void setup() {
        try {
            server = new Server(testAddress, testPort);
            server.start();
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterClass
    public static void shutdown() {
        if (server != null) {
            server.stop();
        }
    }


    @Test
    public void test() throws IOException, ParserException, ParseException {

        StringBuilder sb = new StringBuilder(testScheme + testAddress + ":" + testPort + testEndpoint);

        Instant eventStartInstant = Instant.now().plus(1L, ChronoUnit.HOURS).truncatedTo(ChronoUnit.SECONDS);
        Instant eventEndInstant = Instant.now().plus(2L, ChronoUnit.HOURS).truncatedTo(ChronoUnit.SECONDS);

        sb.append("?eventName=").append("test")
                .append("&eventDescription=").append("test")
                .append("&eventSummary=").append("test")
                .append("&organizerName=").append("Amann%20Malik")
                .append("&organizerEmail=").append("amannmalik@gmail.com")
                .append("&attendeeName=").append("Amann%20Malik")
                .append("&attendeeEmail=").append("amannmalik@gmail.com")
                .append("&location=").append("test")
                .append("&startTimestamp=").append(eventStartInstant.toString())
                .append("&endTimestamp=").append(eventEndInstant.toString())
                .append("&alarmOffset=").append(-15);

        HttpURLConnection connection = ServiceRequestFactory.get(sb.toString());
        int responseCode = connection.getResponseCode();

        assertEquals("Response must be 200 OK", 200, responseCode);

        Calendar cal;
        try (InputStream is = connection.getInputStream()) {
            CalendarBuilder builder = new CalendarBuilder();
            cal = builder.build(is);
        }

        assertEquals(cal.getMethod(), Method.REQUEST);

        CalendarComponent vevent = cal.getComponent("VEVENT");
        assertNotNull(vevent);

        DateTime dtstart = new DateTime(vevent.getProperty("DTSTART").getValue());
        assertEquals(eventStartInstant.toEpochMilli(), dtstart.toInstant().toEpochMilli());

        DateTime dtend = new DateTime(vevent.getProperty("DTSTART").getValue());
        assertEquals(eventEndInstant.toEpochMilli(), dtend.toInstant().toEpochMilli());


    }
}
