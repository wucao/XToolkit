package com.xxg.xtoolkit.function;

@FunctionalInterface
public interface CheckedRunnable {
    void run() throws Exception;
}
