package com.amannmalik.service.icalgeneration;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;


/**
 * Created by Amann on 8/11/2015.
 */
public class Server {

    private final Undertow server;

    public Server(String address, int port) throws ServletException {
        server = Undertow.builder()
                .addHttpListener(port, address)
                .setHandler(getServiceProvider())
                .build();
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop();
    }

    private static HttpHandler getServiceProvider() throws ServletException {

        DeploymentInfo servletDeployment = new DeploymentInfo()
                .setClassLoader(Thread.currentThread().getContextClassLoader())
                .setDeploymentName("Services")
                .setContextPath("/")
                .addServlets(
                        Servlets.servlet(com.amannmalik.service.icalgeneration.endpoint.HealthServlet.class).addMapping("/health"),
                        Servlets.servlet(com.amannmalik.service.icalgeneration.endpoint.IcalGenerationEndpoint.class).addMapping("/ical")
                );

        ServletContainer container = Servlets.defaultContainer();

        DeploymentManager deploymentManager = container.addDeployment(servletDeployment);
        deploymentManager.deploy();
        HttpHandler handler = deploymentManager.start();
        return handler;
    }

    public static void main(String... args) throws ServletException {
        String address = getAddress(args);
        int port = getPort(args);
        ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(Level.DEBUG);
        Server server = new Server(address, port);
        server.start();

        generateTestRequest();
    }

    private static String getAddress(String... args) {
        if (args.length > 0) {
            return args[0];
        }
        return "0.0.0.0";
    }

    private static int getPort(String... args) {
        if (args.length > 1) {
            try {
                return Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
            }
        }
        return 8080;
    }

    private static void generateTestRequest() {
        Instant eventStartInstant = Instant.now().plus(1L, ChronoUnit.HOURS).truncatedTo(ChronoUnit.SECONDS);
        Instant eventEndInstant = Instant.now().plus(2L, ChronoUnit.HOURS).truncatedTo(ChronoUnit.SECONDS);
        StringBuilder sb = new StringBuilder();
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
                .append("&reminderOffset=").append(-15);
        System.out.println(sb.toString());
    }

}
