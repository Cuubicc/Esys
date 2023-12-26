package org.cubic.esys;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Predicate;

@WorkInProgress
public class AsyncEventDispatcher implements AsyncEventBus {

    private final EventBus bus;

    private final AttachableEventBus attachableEventBus;

    private final ExecutorService executor;

    public AsyncEventDispatcher(EventBus bus, AttachableEventBus attachableEventBus) {
        this.bus = bus;
        this.attachableEventBus = attachableEventBus;
        this.executor = new ThreadPoolExecutor(1, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
    }

    private void run(Runnable runnable) {
        executor.execute(runnable);
    }

    @Override
    public void attach(EventBus bus) {
        run(() -> attachableEventBus.attach(bus));
    }

    @Override
    public void detach(EventBus bus) {
        run(() -> attachableEventBus.detach(bus));
    }

    @Override
    public List<EventBus> eventBusesForName(String name) {
        return attachableEventBus.eventBusesForName(name);
    }

    @Override
    public String name() {
        return bus.name();
    }

    @Override
    public void subscribe(Class<?> cls) {
        run(() -> bus.subscribe(cls));
    }

    @Override
    public void subscribe(Object o) {
        run(() -> bus.subscribe(o));
    }

    @Override
    public void subscribe(Class<?> cls, Predicate<Listener<?>>... filters) {
        run(() -> bus.subscribe(cls, filters));
    }

    @Override
    public void subscribe(Object o, Predicate<Listener<?>>... filters) {
        run(() -> bus.subscribe(o, filters));
    }

    @Override
    public <T> void subscribe(EventHook<T> eventHook) {
        run(() -> bus.subscribe(eventHook));
    }

    @Override
    public <T> void subscribe(SubscribeInfo subscribeInfo, EventHook<T> eventHook) {
        run(() -> bus.subscribe(subscribeInfo, eventHook));
    }

    @Override
    public void unsubscribe(Class<?> cls) {
        run(() -> bus.unsubscribe(cls));
    }

    @Override
    public void unsubscribe(Object o) {
        run(() -> bus.unsubscribe(o));
    }

    @Override
    public <T> void unsubscribe(EventHook<T> eventHook) {
        run(() -> bus.unsubscribe(eventHook));
    }

    @Override
    public void unsubscribeAll(Class<?> cls) {
        run(() -> bus.unsubscribeAll(cls));
    }

    @Override
    public void post(Object event) {
        run(() -> bus.post(event));
    }

    @Override
    public void post(Object event, PostInfo info) {
        run(() -> bus.post(event, info));
    }

    @Override
    public <T> void post(T event, Predicate<T>... filters) {
        run(() -> bus.post(event, filters));
    }

    @Override
    public <T> void post(T event, PostInfo info, Predicate<T>... filters) {
        run(() -> bus.post(event, info, filters));
    }

    @Override
    public Set<Listener<?>> getListeners() {
        return bus.getListeners();
    }

    @Override
    public Map<Class<?>, List<Listener<?>>> getListenerMap() {
        return bus.getListenerMap();
    }

    @Override
    public List<Listener<?>> getListenerForEvent(Class<?> cls) {
        return bus.getListenerForEvent(cls);
    }

    @Override
    public <T> Listener<T> getListener(String name) {
        return bus.getListener(name);
    }

    @Override
    public AsyncEventBus toAsync() {
        return this;
    }

    @Override
    public <T extends EventBus> T toSynced() {
        return (T) bus;
    }

    @Override
    protected void finalize() throws Throwable {
        executor.shutdownNow();
    }
}
