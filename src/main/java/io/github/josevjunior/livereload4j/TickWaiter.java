package io.github.josevjunior.livereload4j;

import io.github.josevjunior.livereload4j.utils.Logger;
import java.util.concurrent.atomic.AtomicInteger;

public class TickWaiter {

    private static final int SLEEP_FLAG = -1;
    private static final int WAIT_FLAG = 0;
    private static final int WILLNOTIFY_FLAG = 1;

    private AtomicInteger monitor = new AtomicInteger(-1);
    private long waitUntilAdviceInMillis = 500;
    
    private final static Logger LOGGER = Logger.getLogger(TickWaiter.class);
    

    public TickWaiter(Runnable callback) {
        Thread thread = new Thread(() -> {
            try {
                do {
                    if (monitor.get() == SLEEP_FLAG) {
                        synchronized (monitor) {
                            LOGGER.debug("Irei dormir");
                            monitor.wait();
                            LOGGER.debug("Acordei");
                        }
                    }
                    while (monitor.get() == WAIT_FLAG) {
                        monitor.compareAndSet(WAIT_FLAG, WILLNOTIFY_FLAG);
                        synchronized (monitor) {
                            LOGGER.debug("Irei esperar");
                            monitor.wait(waitUntilAdviceInMillis);
                            LOGGER.debug("Esperei");
                        }
                    }
                    
                    callback.run();
                    monitor.set(monitor.get() == WILLNOTIFY_FLAG ? SLEEP_FLAG : WAIT_FLAG);
                } while (true);
            } catch (InterruptedException e) {
            }
        });
        thread.setName("TickWaiter [" + Thread.currentThread().getName() + "]");
        thread.start();
    }

    public void notifyOrWait() {
        switch (monitor.get()) {
            case SLEEP_FLAG:
                monitor.compareAndSet(SLEEP_FLAG, WAIT_FLAG);
                synchronized (monitor) {
                    monitor.notifyAll();
                }
                break;
            case WILLNOTIFY_FLAG:
                monitor.compareAndSet(WILLNOTIFY_FLAG, WAIT_FLAG);
                synchronized (monitor) {
                    monitor.notifyAll();
                }
                break;
        }
    }
}
