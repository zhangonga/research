package tech.zg.research.jdk.semaphore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Semaphore;

public class SemaphoreService {

    /**
     * 信号量 permits 许可，允许几个通路
     */
    private Semaphore semaphore = new Semaphore(2);

    public void doSomething() {
        try {
            // acquire的数字，表示线程获取几个通路，如果一共两个通路，这里直接两个，则还是只有一个线程能运行
            semaphore.acquire(2);
            // semaphore.acquireUninterruptibly(2); 非特殊情况不要使用这种方式。
            System.out.println(Thread.currentThread().getName() + "-start-" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            Thread.sleep(2000);
            System.out.println(Thread.currentThread().getName() + "-end-" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release(2);
        }
    }
}
