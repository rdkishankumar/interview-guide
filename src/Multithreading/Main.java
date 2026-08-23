package Multithreading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        SharedResource sharedResource = new SharedResource();
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        // 4: Reader thread
        for (int i = 0; i < 5; i++) {
            executorService.execute(() -> {
                sharedResource.getData();
            });
        }

        // 1 writer thread
        executorService.execute(() -> {
            sharedResource.setData(2);
        });

        executorService.shutdown();


    }
}
