
package com.clockwork.asset;

import java.util.concurrent.*;

/**
 * <code>ThreadingManager</code> manages the threads used to load content
 * within the Content Manager system. A pool of threads and a task queue
 * is used to load resource data and perform I/O while the application's
 * render thread is active. 
 */
public class ThreadingManager {

    protected final ExecutorService executor =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), 
                                         new LoadingThreadFactory());

    protected final AssetManager owner;
    protected int nextThreadId = 0;

    public ThreadingManager(AssetManager owner){
        this.owner = owner;
    }

    protected class LoadingThreadFactory implements ThreadFactory {
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "jME3-threadpool-" + (nextThreadId++));
            t.setDaemon(true);
            t.setPriority(Thread.MIN_PRIORITY);
            return t;
        }
    }

    protected class LoadingTask<T> implements Callable<T> {

        private final AssetKey<T> assetKey;

        public LoadingTask(AssetKey<T> assetKey) {
            this.assetKey = assetKey;
        }

        public T call() throws Exception {
            return owner.loadAsset(assetKey);
        }
    }

    public <T> Future<T> loadAsset(AssetKey<T> assetKey) {
        return executor.submit(new LoadingTask(assetKey));
    }

    public static boolean isLoadingThread() {
        return Thread.currentThread().getName().startsWith("jME3-threadpool");
    }


}
