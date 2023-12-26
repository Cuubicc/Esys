package org.cubic.esys;

import net.jodah.typetools.TypeResolver;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

@WorkInProgress
public class DefaultListenerEngine implements ListenerEngine<DefaultListenerEngineOptions> {

    private final DefaultListenerEngineOptions options;

    DefaultListenerEngine(DefaultListenerEngineOptions options) {
        this.options = options;
    }

    @Override
    public <T> Listener<T> makeListener(EventHook<T> eventHook, SubscribeInfo subscribeInfo) {
        return EventHookListener.newListener(eventHook, subscribeInfo, (Class<T>) TypeResolver.resolveRawArgument(EventHook.class, eventHook.getClass()));
    }

    @Override
    public Listener<?> makeListener(Method m, Object instance) {
        MethodWrapper wrapper = new MethodWrapper(m, instance);
        return MethodListener.newListener(wrapper);
    }

    @Override
    public List<Listener<?>> findAllStatic(Class<?> cls, Predicate<Listener<?>> filter) {
        List<Listener<?>> list = new ArrayList<>();
        scan(cls, null, listener -> {
            if (listener == null || !filter.test(listener)) {
                return;
            }
            list.add(listener);
        });
        return list;
    }

    @Override
    public List<Listener<?>> findAll(Object instance, Predicate<Listener<?>> filter) {
        List<Listener<?>> list = new ArrayList<>();
        scan(instance.getClass(), instance, listener -> {
            if (listener == null || !filter.test(listener)) {
                return;
            }
            list.add(listener);
        });
        return list;
    }

    @Override
    public void scanAllStatic(Class<?> cls, Consumer<Listener<?>> consumer) {
        scan(cls, null, consumer);
    }

    @Override
    public void scanAll(Object instance, Consumer<Listener<?>> consumer) {
        scan(instance.getClass(), instance, consumer);
    }

    @Override
    public DefaultListenerEngineOptions getOptions() {
        return options;
    }

    private void scan(Class<?> cls, Object instance, Consumer<Listener<?>> consumer) {
        if ((options.mode & DefaultListenerEngineOptions.SEARCH_METHODS) != 0) {
            for (Method m : cls.getDeclaredMethods()) {
                if (instance == null && !Modifier.isStatic(m.getModifiers()))
                    continue;
                m.setAccessible(true);
                MethodWrapper wrapper = new MethodWrapper(m, instance);
                MethodListener<?> listener = MethodListener.newListener(wrapper);
                consumer.accept(listener);
            }
        }
        if ((options.mode & DefaultListenerEngineOptions.SEARCH_FIELDS) != 0) {
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
                consumer.accept(listener);
            }
        }
    }
}
