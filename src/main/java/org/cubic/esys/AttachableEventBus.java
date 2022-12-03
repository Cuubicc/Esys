package org.cubic.esys;

import java.util.List;

public interface AttachableEventBus extends EventBus {

    void attach(EventBus bus);

    void detach(EventBus bus);

    List<EventBus> eventBusesForName(String name);
}
