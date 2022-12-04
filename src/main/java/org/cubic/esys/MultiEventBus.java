package org.cubic.esys;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MultiEventBus {

    List<EventBus> getEventBusList();

    boolean addEventBus(EventBus eventBus);

    boolean removeEventBus(EventBus eventBus);

    EventBus removeEventBus(String name);

    EventBus getEventBus(String name);

    Map<String, EventBus> getEventBusMap();

    Set<Listener<?>> getListeners(String eventBusName);

    <T> Listener<T> getListener(String eventBusName, String listenerName);

    void postToAll(Object event);

    void postToAll(Object object, PostInfo postInfo);

    void post(Object event, String... eventBusNames);

    void post(Object event, PostInfo postInfo, String... eventBusNames);
}
