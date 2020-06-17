package tech.zg.research.jdk.semaphore;

public class Main {

    public static void main(String[] args) {
        SemaphoreService semaphoreService = new SemaphoreService();

        for (int i = 0; i < 10; i++) {
            SemaphoreThread semaphoreThread = new SemaphoreThread("thread" + i, semaphoreService);
            semaphoreThread.start();
        }
    }
}
