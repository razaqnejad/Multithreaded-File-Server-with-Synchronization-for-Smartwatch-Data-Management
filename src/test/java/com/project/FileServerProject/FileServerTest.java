package com.project.FileServerProject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.Assert.*;

public class FileServerTest {
    private Thread serverThread;

    @Before
    public void setUp() {
        serverThread = new Thread(() -> {
            FileServer.main(new String[]{});
        });
        serverThread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        serverThread.interrupt();
    }

    @Test
    public void testServerConnection() {
        try {
            Socket socket = new Socket("localhost", 12345);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("TEST MESSAGE");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = in.readLine();

            out.close();
            in.close();
            socket.close();

            assertEquals("Server should respond correctly", "Unknown command: TEST", response);

        } catch (IOException e) {
            fail("IOException occurred");
        }
    }

    @Test
    public void testFileReadWrite() {
        testFileOperation("readfile1.txt", "writefile1.txt");
        testFileOperation("readfile2.txt", "writefile2.txt");
        testFileOperation("readfile3.txt", "writefile3.txt");
        testFileOperation("readfile4.txt", "writefile4.txt");
    }

    private void testFileOperation(String readFileName, String writeFileName) {
        try {
            Socket socket = new Socket("localhost", 12345);

            // ارسال درخواست خواندن فایل
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("READ " + readFileName);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = in.readLine();

            assertEquals("Server should respond with file content", "File Content: Hello, World!", response);

            // ارسال درخواست نوشتن فایل جدید
            out.println("WRITE " + writeFileName + " Hello from test!");

            response = in.readLine();

            assertEquals("Server should respond with success message for write operation", "File written successfully", response);

            out.close();
            in.close();
            socket.close();

        } catch (IOException e) {
            fail("IOException occurred");
        }
    }

    @Test
    public void testConcurrency() {
        try {
            Socket socket1 = new Socket("localhost", 12345);
            Socket socket2 = new Socket("localhost", 12345);

            PrintWriter out1 = new PrintWriter(socket1.getOutputStream(), true);
            PrintWriter out2 = new PrintWriter(socket2.getOutputStream(), true);

            out1.println("READ readfile1.txt");
            out2.println("READ readfile2.txt");

            BufferedReader in1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
            String response1 = in1.readLine();

            BufferedReader in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
            String response2 = in2.readLine();

            assertEquals("Server should respond with file content for client 1", "File Content: Hello, World!", response1);
            assertEquals("Server should respond with file content for client 2", "File Content: Hello, World!", response2);

            out1.close();
            out2.close();
            in1.close();
            in2.close();
            socket1.close();
            socket2.close();

        } catch (IOException e) {
            fail("IOException occurred");
        }
    }

    @Test
    public void testPowerOffCommand() {
        try {
            Socket socket = new Socket("localhost", 12345);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("3G*1234567890*POWEROFF");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = in.readLine();

            out.close();
            in.close();
            socket.close();

            assertEquals("Server should respond with poweroff command", "Power off command sent to IMEI: 1234567890", response);

        } catch (IOException e) {
            fail("IOException occurred");
        }
    }

    @Test
    public void testFindCommand() {
        try {
            Socket socket = new Socket("localhost", 12345);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("3G*1234567890*FIND");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = in.readLine();

            out.close();
            in.close();
            socket.close();

            assertEquals("Server should respond with find command", "Find command sent to IMEI: 1234567890", response);

        } catch (IOException e) {
            fail("IOException occurred");
        }
    }

    @Test
    public void testHealthData() {
        try {
            Socket socket = new Socket("localhost", 12345);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("3G*1234567890*HEALTH*80,120,80");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = in.readLine();

            out.close();
            in.close();
            socket.close();

            assertEquals("Server should respond with health data", "Health data received", response);

        } catch (IOException e) {
            fail("IOException occurred");
        }
    }

    @Test
    public void testLocationData() {
        try {
            Socket socket = new Socket("localhost", 12345);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("3G*1234567890*UD*35.6892,51.3890");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = in.readLine();

            out.close();
            in.close();
            socket.close();

            assertEquals("Server should respond with location data", "Location data received", response);

        } catch (IOException e) {
            fail("IOException occurred");
        }
    }

    @Test
    public void testSimultaneousWrites() {
        try {
            Socket socket1 = new Socket("localhost", 12345);
            Socket socket2 = new Socket("localhost", 12345);

            PrintWriter out1 = new PrintWriter(socket1.getOutputStream(), true);
            PrintWriter out2 = new PrintWriter(socket2.getOutputStream(), true);

            out1.println("WRITE writefile1.txt Hello from client 1!");
            out2.println("WRITE writefile1.txt Hello from client 2!");

            BufferedReader in1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
            String response1 = in1.readLine();

            BufferedReader in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
            String response2 = in2.readLine();

            assertEquals("File written successfully", response1);
            assertEquals("File written successfully", response2);

            out1.close();
            out2.close();
            in1.close();
            in2.close();
            socket1.close();
            socket2.close();

            // Check the file content
            Socket socketCheck = new Socket("localhost", 12345);
            PrintWriter outCheck = new PrintWriter(socketCheck.getOutputStream(), true);
            BufferedReader inCheck = new BufferedReader(new InputStreamReader(socketCheck.getInputStream()));

            outCheck.println("READ writefile1.txt");
            String responseCheck = inCheck.readLine();
            System.out.println("Content of writefile1.txt after simultaneous writes: " + responseCheck);

            outCheck.close();
            inCheck.close();
            socketCheck.close();

        } catch (IOException e) {
            fail("IOException occurred");
        }
    }
}
