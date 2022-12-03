package org.cubic.esys;

public final class EventHookListener<T> extends Listener<T> {

    private final EventHook<T> eventHook;

    private EventHookListener(EventHook<T> eventHook, Class<T> eventType, SubscribeInfo subscribeInfo) {
        super(eventType, subscribeInfo);
        this.eventHook = eventHook;
    }

    @Override
    protected void call(T event) {
        this.eventHook.invoke(event);
    }

    public static <T> EventHookListener<T> newListener(EventHook<T> eventHook, SubscribeInfo subscribeInfo, Class<T> eventType){
        return new EventHookListener<>(eventHook, eventType, subscribeInfo);
    }

    public static EventHookListener<?> newUnknown(EventHook<?> eventHook, SubscribeInfo subscribeInfo, Class<?> eventType){
        return new EventHookListener<>((EventHook<Object>) eventHook, (Class<Object>) eventType, subscribeInfo);
    }
}
