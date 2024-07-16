package com.project.FileServerProject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    private static final int THREAD_COUNT = 10;
    private ExecutorService executorService;

    public ThreadPool() {
        this.executorService = Executors.newFixedThreadPool(THREAD_COUNT);
    }

    public void submitTask(Runnable task) {
        executorService.submit(task);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
