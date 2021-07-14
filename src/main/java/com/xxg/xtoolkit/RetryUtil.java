package com.xxg.xtoolkit;

import com.xxg.xtoolkit.function.CheckedRunnable;
import com.xxg.xtoolkit.function.CheckedSupplier;

public class RetryUtil {

    public static<T> T get(CheckedSupplier<T> task, int retries) throws Exception {
        if (retries <= 0) {
            throw new IllegalArgumentException("retries 必须大于 0");
        }

        Exception throwable = null;
        for (int i = 0; i < retries; i++) {
            try {
                return task.get();
            } catch (Exception e) {
                throwable = e;
            }
        }
        throw throwable;
    }

    public static void run(CheckedRunnable task, int retries) throws Exception {
        get(() -> {
            task.run();
            return null;
        }, retries);
    }
}
