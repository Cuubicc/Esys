package org.cubic.esys;

import java.util.List;
import java.util.Map;

public interface MultiEventBus {

    List<EventBus> getEventBusList();

    boolean addEventBus(EventBus eventBus);

    boolean removeEventBus(EventBus eventBus);

    EventBus removeEventBus(String name);

    EventBus getEventBus(String name);

    Map<String, EventBus> getEventBusMap();
}
