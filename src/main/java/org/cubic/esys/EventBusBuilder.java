package org.cubic.esys;

import java.util.Objects;

public class EventBusBuilder<T extends EventBus> {

    private String name;

    private boolean superListeners;

    private boolean attachable;

    private boolean attachPostInfo;

    private boolean searchFields;

    private boolean async;

    public EventBusBuilder(){
        this.name = null;
        this.superListeners = false;
        this.attachable = false;
        this.attachPostInfo = false;
        this.searchFields = false;
        this.async = false;
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
    public EventBusBuilder<AsyncEventBus> async() {
        this.async = true;
        return (EventBusBuilder<AsyncEventBus>) this;
    }

    @SuppressWarnings("unchecked")
    public T build(){
        Objects.requireNonNull(name);
        EventBus eventBus;
        if(this.attachable)
            eventBus = new AttachableEventDispatcher(this.name, this.superListeners, this.searchFields, this.attachPostInfo);
        else
            eventBus = new EventDispatcher(this.name, this.superListeners, this.searchFields);
        if (this.async)
            return (T) new AsyncEventDispatcher(eventBus, this.attachable ? (AttachableEventBus) eventBus : null);
        return (T) eventBus;
    }
}
