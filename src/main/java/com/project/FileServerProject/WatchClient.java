package com.project.FileServerProject;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.logging.*;

public class WatchClient {
    private static final Logger logger = Logger.getLogger(WatchClient.class.getName());
    private String serverAddress;
    private int port;
    private String imei;
    private ScheduledExecutorService scheduler;

    public WatchClient(String serverAddress, int port, String imei) {
        this.serverAddress = serverAddress;
        this.port = port;
        this.imei = imei;
        this.scheduler = Executors.newScheduledThreadPool(1);
        scheduleTasks();
    }

    private void scheduleTasks() {
        scheduler.scheduleAtFixedRate(() -> sendHealthData("80", "120", "80"), 0, 30, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(() -> sendLocation("35.6892", "51.3890"), 0, 45, TimeUnit.SECONDS);
    }

    public void sendPowerOffCommand() {
        sendMessage("3G*" + imei + "*POWEROFF");
    }

    public void sendFindCommand() {
        sendMessage("3G*" + imei + "*FIND");
    }

    public void sendHealthData(String heartRate, String bpLow, String bpHigh) {
        sendMessage("3G*" + imei + "*HEALTH*" + heartRate + "," + bpLow + "," + bpHigh);
    }

    public void sendLocation(String lat, String lon) {
        sendMessage("3G*" + imei + "*UD*" + lat + "," + lon);
    }

    private void sendMessage(String message) {
        try (Socket socket = new Socket(serverAddress, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(message);
            logger.info("Sent: " + message);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Client exception: ", e);
        }
    }

    public static void main(String[] args) {
        WatchClient watchClient = new WatchClient("127.0.0.1", 12345, "1234567890");
        watchClient.sendPowerOffCommand();
        watchClient.sendFindCommand();
        // Running indefinitely to send health data and location periodically
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
