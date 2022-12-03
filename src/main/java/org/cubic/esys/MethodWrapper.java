package org.cubic.esys;

import java.lang.reflect.Method;
import java.util.Objects;

public final class MethodWrapper {

    public static final MethodWrapper EMPTY = new MethodWrapper(null, null);

    private final Method method;

    private final Object instance;

    public MethodWrapper(Method method, Object instance){
        this.method = method;
        this.instance = instance;
    }

    public Method getMethod(){
        return method;
    }

    public Object getInstance(){
        return instance;
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof MethodWrapper))
            return false;
        MethodWrapper wrapper = (MethodWrapper) o;
        return Objects.equals(method, wrapper.method) && Objects.equals(instance, wrapper.instance);
    }
}
