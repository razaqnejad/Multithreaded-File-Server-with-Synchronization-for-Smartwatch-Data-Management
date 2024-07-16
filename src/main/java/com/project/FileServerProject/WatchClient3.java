package com.project.FileServerProject;

public class WatchClient3 extends WatchClient {

    public WatchClient3(String serverAddress, int port, String imei) {
        super(serverAddress, port, imei);
    }

    public static void main(String[] args) {
        WatchClient3 watchClient = new WatchClient3("127.0.0.1", 12345, "1122334455");
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
