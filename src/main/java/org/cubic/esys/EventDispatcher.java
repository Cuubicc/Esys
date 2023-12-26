package org.cubic.esys;

import net.jodah.typetools.TypeResolver;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class EventDispatcher implements EventBus {

    protected final Map<Class<?>, List<Listener<?>>> LISTENERS;

    protected final Map<MethodWrapper, MethodListener<?>> METHOD_LISTENER_CACHE;

    protected final Map<EventHook<?>, EventHookListener<?>> EVENT_HOOK_LISTENER_CACHE;

    protected final Map<String, Listener<?>> LISTENER_CACHE;

    protected final String name;

    protected final boolean superListeners;

    protected final boolean searchFields;

    public EventDispatcher(String name, boolean superListeners, boolean searchFields){
        this.LISTENERS = new ConcurrentHashMap<>(128);
        this.METHOD_LISTENER_CACHE = new ConcurrentHashMap<>(256);
        this.EVENT_HOOK_LISTENER_CACHE = new ConcurrentHashMap<>(256);
        this.LISTENER_CACHE = new ConcurrentHashMap<>(256);
        this.name = name;
        this.superListeners = superListeners;
        this.searchFields = searchFields;
    }

    public EventDispatcher(String name){
        this(name, false, false);
    }

    public static EventBusBuilder<EventBus> builder(){
        return new EventBusBuilder<>();
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public void subscribe(Class<?> cls) {
        subscribe(cls, null, new Predicate[0]);
    }

    @Override
    public void subscribe(Object o) {
        subscribe(o.getClass(), o, new Predicate[0]);
    }

    @Override
    public void subscribe(Class<?> cls, Predicate<Listener<?>>... filters) {
        Predicate<Listener<?>>[] predicates = filters == null ? new Predicate[0] : filters;
        subscribe(cls, null, predicates);
    }

    @Override
    public void subscribe(Object o, Predicate<Listener<?>>... filters) {
        Predicate<Listener<?>>[] predicates = filters == null ? new Predicate[0] : filters;
        subscribe(o.getClass(), o, filters);
    }

    @Override
    public <T> void subscribe(EventHook<T> eventHook) {
        EventHookListener<T> listener = EventHookListener.newListener(eventHook, new SubscribeInfo("", 0), (Class<T>) TypeResolver.resolveRawArgument(EventHook.class, eventHook.getClass()));
        List<Listener<?>> listeners = LISTENERS.computeIfAbsent(listener.getEventType(), t -> new ArrayList<>(64));
        int index = listeners.size();
        for(int i = 0; i < listeners.size(); i++) {
            if (listeners.get(i).getSubscribeInfo().getPriority() >= listener.getSubscribeInfo().getPriority())
                continue;
            index = i;
            break;
        }
        listeners.add(index, listener);
        //listeners.sort(Comparator.comparingInt(l -> l.getSubscribeInfo().getPriority()));
        EVENT_HOOK_LISTENER_CACHE.put(eventHook, listener);
    }

    @Override
    public <T> void subscribe(SubscribeInfo subscribeInfo, EventHook<T> eventHook) {
        EventHookListener<T> listener = EventHookListener.newListener(eventHook, subscribeInfo, (Class<T>) TypeResolver.resolveRawArgument(EventHook.class, eventHook.getClass()));
        List<Listener<?>> listeners = LISTENERS.computeIfAbsent(listener.getEventType(), t -> new ArrayList<>(64));
        int index = listeners.size();
        for(int i = 0; i < listeners.size(); i++){
            if (listeners.get(i).getSubscribeInfo().getPriority() >= listener.getSubscribeInfo().getPriority())
                continue;
            index = i;
            break;
        }
        listeners.add(index, listener);
        //listeners.sort(Comparator.comparingInt(l -> l.getSubscribeInfo().getPriority()));
        EVENT_HOOK_LISTENER_CACHE.put(eventHook, listener);
        if(!subscribeInfo.getName().equals(""))
            LISTENER_CACHE.put(subscribeInfo.getName(), listener);
    }

    @Override
    public void unsubscribe(Class<?> cls) {
        unsubscribe(cls, null, false);
    }

    @Override
    public void unsubscribe(Object o) {
        unsubscribe(o.getClass(), o, false);
    }

    @Override
    public <T> void unsubscribe(EventHook<T> eventHook) {
        EventHookListener<?> listener = EVENT_HOOK_LISTENER_CACHE.get(eventHook);
        if(listener == null)
            return;
        List<Listener<?>> listeners = LISTENERS.computeIfAbsent(listener.getEventType(), t -> new ArrayList<>(64));
        listeners.remove(listener);
        EVENT_HOOK_LISTENER_CACHE.remove(eventHook);
        LISTENER_CACHE.remove(listener.getSubscribeInfo().getName());
    }

    @Override
    public void unsubscribeAll(Class<?> cls) {
        unsubscribe(cls, null, true);
    }

    @Override
    public void post(Object event) {
        post(event, new PostInfo(this.superListeners));
    }

    @Override
    public void post(Object event, PostInfo info) {
        if(info.superListener()){
            for(Map.Entry<Class<?>, List<Listener<?>>> entry : LISTENERS.entrySet())
                if(entry.getKey().isAssignableFrom(event.getClass()))
                    for(Listener<?> listener : entry.getValue())
                        ((Listener<Object>) listener).invoke(event);
            return;
        }
        List<Listener<?>> listeners = LISTENERS.get(event.getClass());
        if(listeners == null)
            return;
        for(Listener<?> listener : listeners){
            ((Listener<Object>) listener).invoke(event);
        }
    }

    @Override
    public <T> void post(T event, Predicate<T>... filters) {
        for(Predicate<T> filter : filters)
            if(!filter.test(event))
                return;
        post(event, new PostInfo(this.superListeners));
    }

    @Override
    public <T> void post(T event, PostInfo info, Predicate<T>... filters) {
        for(Predicate<T> filter : filters)
            if(!filter.test(event))
                return;
        post(event, info);
    }

    @Override
    public Set<Listener<?>> getListeners() {
        Collection<MethodListener<?>> methodListeners = METHOD_LISTENER_CACHE.values();
        Collection<EventHookListener<?>> eventHookListeners = EVENT_HOOK_LISTENER_CACHE.values();
        Set<Listener<?>> set = Collections.newSetFromMap(new ConcurrentHashMap<>(methodListeners.size() + eventHookListeners.size()));
        set.addAll(methodListeners);
        set.addAll(eventHookListeners);
        return set;
    }

    @Override
    public Map<Class<?>, List<Listener<?>>> getListenerMap() {
        return LISTENERS;
    }

    @Override
    public List<Listener<?>> getListenerForEvent(Class<?> cls) {
        return LISTENERS.computeIfAbsent(cls, t -> new ArrayList<>());
    }

    @Override
    public <T> Listener<T> getListener(String name) {
        return (Listener<T>) LISTENER_CACHE.get(name);
    }

    @WorkInProgress
    @Override
    public AsyncEventBus toAsync() {
        return new AsyncEventDispatcher(this, null);
    }

    private void subscribe(Class<?> cls, Object instance, Predicate<Listener<?>>[] predicates){
        for(Method m : cls.getDeclaredMethods()){
            if(instance == null && !Modifier.isStatic(m.getModifiers()))
                continue;
            m.setAccessible(true);
            MethodWrapper wrapper = new MethodWrapper(m, instance);
            MethodListener<?> listener = MethodListener.newListener(wrapper);
            if(listener == null)
                continue;
            if(!testAll(listener, predicates))
                continue;
            List<Listener<?>> listeners = LISTENERS.computeIfAbsent(listener.getEventType(), t -> new ArrayList<>(64));
            int index = listeners.size();
            for(int i = 0; i < listeners.size(); i++){
                if (listeners.get(i).getSubscribeInfo().getPriority() >= listener.getSubscribeInfo().getPriority())
                    continue;
                index = i;
                break;
            }
            listeners.add(index, listener);
            //listeners.sort(Comparator.comparingInt(l -> l.getSubscribeInfo().getPriority()));
            METHOD_LISTENER_CACHE.put(wrapper, listener);
            if(!listener.getSubscribeInfo().getName().equals(""))
                LISTENER_CACHE.put(listener.getSubscribeInfo().getName(), listener);
        }
        if(!this.searchFields)
            return;
        for(Field f : cls.getDeclaredFields()){
            if(instance == null && !Modifier.isStatic(f.getModifiers()))
                continue;
            if(!EventHook.class.isAssignableFrom(f.getType()))
                continue;
            f.setAccessible(true);
            EventHook<?> eventHook;
            try {
                eventHook = (EventHook<?>) f.get(instance);
            } catch(IllegalAccessException e){
                e.printStackTrace();
                continue;
            }
            if(eventHook == null)
                continue;
            Subscribe subscribe = f.getAnnotation(Subscribe.class);
            if(subscribe == null)
                continue;
            if(subscribe.type() == Dummy.class)
                continue;
            EventHookListener<?> listener = EventHookListener.newUnknown(eventHook, new SubscribeInfo(subscribe.name(), subscribe.priority()), subscribe.type());
            if(!testAll(listener, predicates))
                continue;
            List<Listener<?>> listeners = LISTENERS.computeIfAbsent(listener.getEventType(), t -> new ArrayList<>(64));
            int index = listeners.size();
            for(int i = 0; i < listeners.size(); i++){
                if (listeners.get(i).getSubscribeInfo().getPriority() >= listener.getSubscribeInfo().getPriority())
                    continue;
                index = i;
                break;
            }
            listeners.add(index, listener);
            //listeners.sort(Comparator.comparingInt(l -> l.getSubscribeInfo().getPriority()));
            EVENT_HOOK_LISTENER_CACHE.put(eventHook, listener);
            if(!subscribe.name().equals(""))
                LISTENER_CACHE.put(subscribe.name(), listener);
        }
    }

    private void unsubscribe(Class<?> cls, Object instance, boolean removeAll){
        for(Method m : cls.getDeclaredMethods()){
            if(!removeAll && instance == null && !Modifier.isStatic(m.getModifiers()))
                continue;
            MethodWrapper wrapper = new MethodWrapper(m, instance);
            MethodListener<?> listener = METHOD_LISTENER_CACHE.get(wrapper);
            if(listener == null)
                continue;
            List<Listener<?>> listeners = LISTENERS.computeIfAbsent(listener.getEventType(), t -> new ArrayList<>(64));
            listeners.remove(listener);
            METHOD_LISTENER_CACHE.remove(wrapper);
            LISTENER_CACHE.remove(listener.getSubscribeInfo().getName());
        }
        if(!this.searchFields)
            return;
        for(Field f : cls.getDeclaredFields()){
            if(!removeAll && instance == null && !Modifier.isStatic(f.getModifiers()))
                continue;
            if(!EventHook.class.isAssignableFrom(f.getType()))
                continue;
            f.setAccessible(true);
            EventHook<?> eventHook;
            try {
                eventHook = (EventHook<?>) f.get(instance);
            } catch(IllegalAccessException e){
                e.printStackTrace();
                continue;
            }
            if(eventHook == null)
                continue;
            Subscribe subscribe = f.getAnnotation(Subscribe.class);
            if(subscribe == null)
                continue;
            if(subscribe.type() == Dummy.class)
                continue;
            EventHookListener<?> listener = EVENT_HOOK_LISTENER_CACHE.get(eventHook);
            if(listener == null)
                continue;
            List<Listener<?>> listeners = LISTENERS.computeIfAbsent(subscribe.type(), t -> new ArrayList<>(64));
            listeners.remove(listener);
            EVENT_HOOK_LISTENER_CACHE.remove(eventHook);
            LISTENER_CACHE.remove(subscribe.name());
        }
    }

    private <T> boolean testAll(T obj, Predicate<T>[] predicates){
        for(Predicate<T> predicate : predicates)
            if(!predicate.test(obj))
                return false;
        return true;
    }
}
