package com.project.FileServerProject;

public class WatchClient1 extends WatchClient {

    public WatchClient1(String serverAddress, int port, String imei) {
        super(serverAddress, port, imei);
    }

    public static void main(String[] args) {
        WatchClient1 watchClient = new WatchClient1("127.0.0.1", 12345, "1234567890");
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
