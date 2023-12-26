package org.cubic.esys;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

@SuppressWarnings("ALL")
public interface EventBus {

    String name();

    void subscribe(Class<?> cls);

    void subscribe(Object o);

    void subscribe(Class<?> cls, Predicate<Listener<?>>... filters);

    void subscribe(Object o, Predicate<Listener<?>>... filters);

    <T> void subscribe(EventHook<T> eventHook);

    <T> void subscribe(SubscribeInfo subscribeInfo, EventHook<T> eventHook);

    void unsubscribe(Class<?> cls);

    void unsubscribe(Object o);

    <T> void unsubscribe(EventHook<T> eventHook);

    void unsubscribeAll(Class<?> cls);

    void post(Object event);

    void post(Object event, PostInfo info);

    <T> void post(T event, Predicate<T>... filters);

    <T> void post(T event, PostInfo info, Predicate<T>... filters);

    Set<Listener<?>> getListeners();

    Map<Class<?>, List<Listener<?>>> getListenerMap();

    List<Listener<?>> getListenerForEvent(Class<?> cls);

    <T> Listener<T> getListener(String name);

    @WorkInProgress
    AsyncEventBus toAsync();


    public static EventBusBuilder<EventBus> builder() {
        return new EventBusBuilder<>();
    }

    public static EventBus buildDefault(String name) {
        return new EventDispatcher(name);
    }

    public static AttachableEventBus buildAttachable(String name) {
        return new AttachableEventDispatcher(name);
    }

    public static AsyncEventBus buildAsync(String name) {
        AttachableEventBus bus = buildAttachable(name);
        return new AsyncEventDispatcher(bus, bus);
    }
}
