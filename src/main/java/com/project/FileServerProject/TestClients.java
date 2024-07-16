package com.project.FileServerProject;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestClients {
    private static final int THREAD_COUNT = 10;
    private static ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_COUNT);

    public static void main(String[] args) {
        // ایجاد و اجرای نخ‌ها با استفاده از مخزن نخی
        for (int i = 1; i <= 4; i++) {
            final String imei = "123456789" + i;
            final String readFileName = "readfile" + i + ".txt";
            final String writeFileName = "writefile" + i + ".txt";
            threadPool.submit(() -> runTests("127.0.0.1", 12345, imei, readFileName, writeFileName));
        }

        threadPool.shutdown();
    }

    private static void runTests(String serverAddress, int port, String imei, String readFileName, String writeFileName) {
        WatchClient watchClient = new WatchClient(serverAddress, port, imei);
        System.out.println("Testing WatchClient (IMEI: " + imei + ")");

        // ارسال دستورات مختلف به سرور
        watchClient.sendPowerOffCommand();
        watchClient.sendFindCommand();

        // عملیات خواندن و نوشتن فایل‌ها
        testFileOperations(serverAddress, port, imei, readFileName, writeFileName);

        // تست نوشتن همزمان
        testSimultaneousWrites(serverAddress, port, imei);
    }

    private static void testFileOperations(String serverAddress, int port, String imei, String readFileName, String writeFileName) {
        try (Socket socket = new Socket(serverAddress, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Client " + imei + " reading " + readFileName);
            out.println("READ " + readFileName);
            String response = in.readLine();
            System.out.println("Response: " + response);

            System.out.println("Client " + imei + " writing to " + writeFileName);
            out.println("WRITE " + writeFileName + " Hello from client " + imei + "!");
            response = in.readLine();
            System.out.println("Response: " + response);

            System.out.println("Client " + imei + " reading " + writeFileName);
            out.println("READ " + writeFileName);
            response = in.readLine();
            System.out.println("Response: " + response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testSimultaneousWrites(String serverAddress, int port, String imei) {
        try {
            Socket socket1 = new Socket(serverAddress, port);
            Socket socket2 = new Socket(serverAddress, port);

            PrintWriter out1 = new PrintWriter(socket1.getOutputStream(), true);
            PrintWriter out2 = new PrintWriter(socket2.getOutputStream(), true);

            out1.println("WRITE writefile" + imei + ".txt Hello from client 1!");
            out2.println("WRITE writefile" + imei + ".txt Hello from client 2!");

            BufferedReader in1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
            String response1 = in1.readLine();

            BufferedReader in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
            String response2 = in2.readLine();

            assert "File written successfully".equals(response1) : "Expected 'File written successfully' but got: " + response1;
            assert "File written successfully".equals(response2) : "Expected 'File written successfully' but got: " + response2;

            out1.close();
            out2.close();
            in1.close();
            in2.close();
            socket1.close();
            socket2.close();

            // Check the file content
            Socket socketCheck = new Socket(serverAddress, port);
            PrintWriter outCheck = new PrintWriter(socketCheck.getOutputStream(), true);
            BufferedReader inCheck = new BufferedReader(new InputStreamReader(socketCheck.getInputStream()));

            outCheck.println("READ writefile" + imei + ".txt");
            String responseCheck = inCheck.readLine();
            System.out.println("Content of writefile" + imei + ".txt after simultaneous writes: " + responseCheck);

            outCheck.close();
            inCheck.close();
            socketCheck.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
