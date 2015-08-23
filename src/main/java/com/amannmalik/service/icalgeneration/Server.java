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

}
