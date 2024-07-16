package com.project.FileServerProject;

public class WatchClient2 extends WatchClient {

    public WatchClient2(String serverAddress, int port, String imei) {
        super(serverAddress, port, imei);
    }

    public static void main(String[] args) {
        WatchClient2 watchClient = new WatchClient2("127.0.0.1", 12345, "0987654321");
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
