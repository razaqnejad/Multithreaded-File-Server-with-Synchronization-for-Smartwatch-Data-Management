package com.project.FileServerProject;

public class WatchClient4 extends WatchClient {

    public WatchClient4(String serverAddress, int port, String imei) {
        super(serverAddress, port, imei);
    }

    public static void main(String[] args) {
        WatchClient4 watchClient = new WatchClient4("127.0.0.1", 12345, "9988776655");
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
