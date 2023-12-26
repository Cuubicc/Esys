package org.cubic.esys;

import java.util.function.Consumer;

public class EventBusMaker {

    public String name;

    public boolean superListeners;

    public boolean attachPostInfo;

    public boolean searchFields;

    public Builder newBuilder() {
        return new Builder();
    }

    public class Builder {

        public EventDispatcher asDefault() {
            return new EventDispatcher(name, superListeners, searchFields);
        }

        public AttachableEventDispatcher asAttachable() {
            return new AttachableEventDispatcher(name, superListeners, searchFields, attachPostInfo);
        }

        public AsyncEventDispatcher asAsync() {
            AttachableEventBus bus = asAttachable();
            return new AsyncEventDispatcher(bus, bus);
        }
    }

    public static Builder make(Consumer<EventBusMaker> consumer) {
        EventBusMaker maker = new EventBusMaker();
        consumer.accept(maker);
        return maker.newBuilder();
    }
}
