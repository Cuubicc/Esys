package org.cubic.esys;

public interface EventHook<T> {

    void invoke(T event);
}
