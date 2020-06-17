package tech.zg.research.jdk.semaphore;

public class SemaphoreThread extends Thread {

    private SemaphoreService semaphoreService;

    public SemaphoreThread(String name, SemaphoreService semaphoreService) {
        super(name);
        this.semaphoreService = semaphoreService;
    }

    @Override
    public void run() {
        this.semaphoreService.doSomething();
    }
}
