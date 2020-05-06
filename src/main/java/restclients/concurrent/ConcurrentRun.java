package restclients.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.LongStream.range;

/**
 * @author Yuriy Tumakha.
 */
public class ConcurrentRun {

    public ConcurrentRun(int threads, int times, Runnable runnable) {
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        range(0, times).forEach(t ->
                executor.submit(runnable)
        );

        executor.shutdown();
        try {
            executor.awaitTermination(60, SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
