package com.xxg.xtoolkit.function;

@FunctionalInterface
public interface CheckedSupplier<T> {
    T get() throws Exception;
}
