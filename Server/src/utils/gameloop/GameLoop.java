package utils.gameloop;

import java.util.concurrent.*;

public class GameLoop {
    public static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(6); // Tùy server

    public static void schedule(Runnable task, long delayMs, long periodMs) {
        EXECUTOR.scheduleAtFixedRate(task, delayMs, periodMs, TimeUnit.MILLISECONDS);
    }

    public static void execute(Runnable task) {
        EXECUTOR.execute(task);
    }
}
