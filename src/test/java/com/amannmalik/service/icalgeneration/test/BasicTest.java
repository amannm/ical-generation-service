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
import java.time.LocalDateTime;
import java.time.ZoneId;
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

        LocalDateTime eventStartInstant = LocalDateTime.now().plus(1L, ChronoUnit.HOURS).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime eventEndInstant = LocalDateTime.now().plus(2L, ChronoUnit.HOURS).truncatedTo(ChronoUnit.SECONDS);

        sb.append("?filename=").append("test.ics")
                .append("&title=").append("Test%20Event")
                .append("&description=").append("This%20is%20a%20Test%20Event")
                .append("&organizerName=").append("Amann%20Malik")
                .append("&organizerEmail=").append("amannmalik@gmail.com")
                .append("&attendeeName=").append("John%20Doe")
                .append("&attendeeEmail=").append("johndoe@test.com")
                .append("&location=").append("http%3A%2F%2Fgoogle.com")
                .append("&startTimestamp=").append(eventStartInstant.toString())
                .append("&endTimestamp=").append(eventEndInstant.toString())
                .append("&timezone=").append("America/Chicago")
                .append("&reminderOffset=").append(-15);

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
        assertEquals(eventStartInstant.atZone(ZoneId.of("America/Chicago")).toInstant().toEpochMilli(), dtstart.toInstant().toEpochMilli());

        DateTime dtend = new DateTime(vevent.getProperty("DTEND").getValue());
        assertEquals(eventEndInstant.atZone(ZoneId.of("America/Chicago")).toInstant().toEpochMilli(), dtend.toInstant().toEpochMilli());


    }
}
