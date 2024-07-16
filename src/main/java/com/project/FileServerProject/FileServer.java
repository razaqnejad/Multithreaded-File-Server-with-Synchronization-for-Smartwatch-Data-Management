package com.project.FileServerProject;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.*;

public class FileServer {
    private static final int PORT = 12345;
    private static final Logger logger = Logger.getLogger(FileServer.class.getName());
    private static ExecutorService threadPool = Executors.newFixedThreadPool(5);
    private static ReentrantLock logLock = new ReentrantLock();

    public static void main(String[] args) {
        setupLogger();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("File server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("Accepted connection from " + clientSocket.getInetAddress());
                threadPool.submit(new ClientHandler(clientSocket, logLock));
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Server exception: ", e);
        }
    }

    private static void setupLogger() {
        try {
            FileHandler fileHandler = new FileHandler("src/main/resources/logs/server.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to setup logger: ", e);
        }
    }
}
