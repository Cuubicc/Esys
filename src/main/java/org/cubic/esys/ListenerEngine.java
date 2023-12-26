package org.cubic.esys;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

@WorkInProgress
public interface ListenerEngine<O> {

    <T> Listener<T> makeListener(EventHook<T> eventHook, SubscribeInfo subscribeInfo);

    Listener<?> makeListener(Method m, Object instance);

    List<Listener<?>> findAllStatic(Class<?> cls, Predicate<Listener<?>> filter);

    List<Listener<?>> findAll(Object instance, Predicate<Listener<?>> filter);

    void scanAllStatic(Class<?> cls, Consumer<Listener<?>> consumer);

    void scanAll(Object instance, Consumer<Listener<?>> consumer);

    O getOptions();

    public static ListenerEngine<DefaultListenerEngineOptions> makeDefault(Consumer<DefaultListenerEngineOptions> consumer) {
        DefaultListenerEngineOptions options = new DefaultListenerEngineOptions();
        consumer.accept(options);
        return new DefaultListenerEngine(options);
    }

    public static ListenerEngine<DefaultListenerEngineOptions> makeDefault() {
        return makeDefault(options -> {});
    }
}
