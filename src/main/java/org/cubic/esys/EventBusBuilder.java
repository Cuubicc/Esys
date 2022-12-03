package org.cubic.esys;

import java.util.Objects;

public class EventBusBuilder<T extends EventBus> {

    private String name;

    private boolean superListeners;

    private boolean attachable;

    private boolean attachPostInfo;

    private boolean searchFields;

    public EventBusBuilder(){
        this.name = null;
        this.superListeners = false;
        this.attachable = false;
        this.attachPostInfo = false;
        this.searchFields = false;
    }

    public EventBusBuilder<T> name(String name){
        this.name = name;
        return this;
    }

    public EventBusBuilder<T> superListeners(){
        this.superListeners = true;
        return this;
    }

    @SuppressWarnings("unchecked")
    public EventBusBuilder<AttachableEventBus> attachable(){
        this.attachable = true;
        return (EventBusBuilder<AttachableEventBus>) this;
    }

    public EventBusBuilder<T> attachPostInfo(){
        this.attachPostInfo = true;
        return this;
    }

    public EventBusBuilder<T> searchFields(){
        this.searchFields = true;
        return this;
    }

    @SuppressWarnings("unchecked")
    public T build(){
        Objects.requireNonNull(name);
        if(this.attachable)
            return (T) new AttachableEventDispatcher(this.name, this.superListeners, this.searchFields, this.attachPostInfo);
        return (T) new EventDispatcher(this.name, this.superListeners, this.searchFields);
    }
}
