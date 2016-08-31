/**
 * @author dawson dong
 */

package com.applikey.mattermost.utils.kissUtils.thread;

import android.support.annotation.NonNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class KissExecutor {

    private static final String TAG = KissExecutor.class.getSimpleName();

    private KissExecutor() {
    }

    public static Executor createExecutor(int poolSize, int priority) {
        priority = buildPriority(priority);
        final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>();
        final ThreadFactory threadFactory = new DefaultThreadFactory(priority,
                "kiss-executor-");
        return new ThreadPoolExecutor(poolSize, poolSize, 0L,
                TimeUnit.MILLISECONDS, taskQueue, threadFactory);
    }

    private static int buildPriority(int priority) {
        if (priority < Thread.MIN_PRIORITY) {
            return Thread.MIN_PRIORITY;
        } else if (priority > Thread.MAX_PRIORITY) {
            return Thread.MAX_PRIORITY;
        } else {
            return priority;
        }
    }

    private static class DefaultThreadFactory implements ThreadFactory {

        private static final AtomicInteger poolCount = new AtomicInteger(1);

        private final ThreadGroup group;
        private final AtomicInteger threadNumber;
        private final String namePrefix;
        private final int threadPriority;

        DefaultThreadFactory(int threadPriority, String namePrefix) {
            this.threadNumber = new AtomicInteger(1);
            this.threadPriority = threadPriority;
            this.group = Thread.currentThread().getThreadGroup();
            this.namePrefix = namePrefix + poolCount.getAndIncrement();
        }

        @Override
        public Thread newThread(@NonNull Runnable runnable) {
            final String threadName = namePrefix + "-thread-"
                    + threadNumber.getAndIncrement();
            final Thread thread = new Thread(group, runnable, threadName, 0);
            if (thread.isDaemon()) {
                thread.setDaemon(false);
            }
            thread.setPriority(threadPriority);
            return thread;
        }
    }
}
