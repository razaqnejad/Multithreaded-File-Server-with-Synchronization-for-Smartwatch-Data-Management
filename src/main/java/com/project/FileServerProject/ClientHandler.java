package com.project.FileServerProject;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.*;

public class ClientHandler implements Runnable {
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private Socket clientSocket;
    private static ReentrantLock lock;
    private static ReentrantLock logLock;

    public ClientHandler(Socket socket, ReentrantLock logLock) {
        this.clientSocket = socket;
        ClientHandler.lock = new ReentrantLock();
        ClientHandler.logLock = logLock;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String request;
            while ((request = in.readLine()) != null) {
                logger.info("Received: " + request);
                String response = handleRequest(request);
                out.println(response);
                logToFile(request, response);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Client handler exception: ", e);
        }
    }

    private String handleRequest(String request) {
        String[] parts = request.split("\\*");
        if (parts.length < 3) {
            return "Invalid command format";
        }
        
        String command = parts[2];
        String imei = parts[1];

        switch (command) {
            case "POWEROFF":
                return powerOff(imei);
            case "FIND":
                return findWatch(imei);
            case "HEALTH":
                return healthData(parts);
            case "UD":
                return locationData(parts);
            default:
                return "Unknown command: " + command;
        }
    }

    private String powerOff(String imei) {
        logger.info("Power off command received for IMEI: " + imei);
        return "Power off command sent to IMEI: " + imei;
    }

    private String findWatch(String imei) {
        logger.info("Find command received for IMEI: " + imei);
        return "Find command sent to IMEI: " + imei;
    }

    private String healthData(String[] parts) {
        if (parts.length < 5) {
            return "Invalid health data format";
        }
        String data = parts[3] + ", " + parts[4];
        logger.info("Health data received: " + data);
        return "Health data received";
    }

    private String locationData(String[] parts) {
        if (parts.length < 4) {
            return "Invalid location data format";
        }
        String data = parts[3];
        logger.info("Location data received: " + data);
        return "Location data received";
    }

    private void logToFile(String request, String response) {
        logLock.lock();
        try (FileWriter fw = new FileWriter("src/main/resources/logs/activity.log", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter logOut = new PrintWriter(bw)) {
            logOut.println("Request: " + request);
            logOut.println("Response: " + response);
            logOut.println();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Logging exception: ", e);
        } finally {
            logLock.unlock();
        }
    }
}
