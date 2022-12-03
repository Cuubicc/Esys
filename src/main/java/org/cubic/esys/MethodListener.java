package org.cubic.esys;

import java.lang.reflect.Method;

public final class MethodListener<T> extends Listener<T> {

    private final Method method;

    private final Object instance;

    private MethodListener(Method method, Object instance, Class<T> eventType, SubscribeInfo subscribeInfo) {
        super(eventType, subscribeInfo);
        this.method = method;
        this.instance = instance;
    }

    @Override
    protected void call(T event) {
        try {
            method.invoke(instance, event);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static <T> MethodListener<T> newListener(MethodWrapper wrapper){
        Method m = wrapper.getMethod();
        Object instance = wrapper.getInstance();
        if(!isListener(m))
            return null;
        Subscribe subscribe = m.getAnnotation(Subscribe.class);
        return new MethodListener<>(m, instance, (Class<T>) m.getParameterTypes()[0], new SubscribeInfo(subscribe.name(), subscribe.priority()));
    }

    public static boolean isListener(Method m){
        Class<?>[] paramTypes = m.getParameterTypes();
        return paramTypes.length == 1 && m.getAnnotation(Subscribe.class) != null;
    }
}
