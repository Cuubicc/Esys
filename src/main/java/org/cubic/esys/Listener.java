package org.cubic.esys;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.function.Predicate;

public abstract class Listener<T> {

    protected final Class<T> eventType;

    protected final SubscribeInfo subscribeInfo;

    protected boolean isSleeping;

    protected final List<Predicate<T>> sleepFilters;

    public Listener(Class<T> eventType, SubscribeInfo subscribeInfo){
        this.eventType = eventType;
        this.subscribeInfo = subscribeInfo;
        this.isSleeping = false;
        this.sleepFilters = new Vector<>();
    }

    public Class<?> getEventType() {
        return eventType;
    }

    public SubscribeInfo getSubscribeInfo() {
        return subscribeInfo;
    }

    public boolean isSleeping() {
        return isSleeping;
    }

    public List<Predicate<T>> getSleepFilters() {
        return sleepFilters;
    }

    public void sleep(){
        this.isSleeping = true;
    }

    public void wakeUp(){
        this.isSleeping = false;
    }

    @SafeVarargs
    public final void sleepFilters(Predicate<T>... filters){
        this.sleepFilters.clear();
        this.sleepFilters.addAll(Arrays.asList(filters));
    }

    @SafeVarargs
    public final void addSleepFilters(Predicate<T>... filters){
        this.sleepFilters.addAll(Arrays.asList(filters));
    }

    public boolean shouldSleep(T event){
        if(isSleeping)
            return true;
        for(Predicate<T> filter : sleepFilters)
            if(filter.test(event))
                return true;
        return false;
    }

    public final void invoke(T event){
        if(shouldSleep(event))
            return;
        call(event);
    }

    protected abstract void call(T event);
}
