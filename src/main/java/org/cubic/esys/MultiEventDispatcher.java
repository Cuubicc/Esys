package org.cubic.esys;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MultiEventDispatcher implements MultiEventBus {

    private final Map<String, EventBus> eventBusMap;

    private final List<EventBus> eventBusList;

    public MultiEventDispatcher(){
        this.eventBusMap = new ConcurrentHashMap<>();
        this.eventBusList = new ArrayList<>();
    }

    @Override
    public List<EventBus> getEventBusList() {
        return eventBusList;
    }

    @Override
    public boolean addEventBus(EventBus eventBus) {
        if(eventBus == null)
            return false;
        if(eventBusMap.containsKey(eventBus.name()))
            return false;
        eventBusMap.put(eventBus.name(), eventBus);
        eventBusList.add(eventBus);
        return true;
    }

    @Override
    public boolean removeEventBus(EventBus eventBus) {
        eventBusList.remove(eventBus);
        return eventBusMap.remove(eventBus.name(), eventBus);
    }

    @Override
    public EventBus removeEventBus(String name) {
        EventBus bus = eventBusMap.get(name);
        if(bus == null)
            return null;
        eventBusList.remove(bus);
        return eventBusMap.remove(name);
    }

    @Override
    public EventBus getEventBus(String name) {
        return eventBusMap.get(name);
    }

    @Override
    public Map<String, EventBus> getEventBusMap() {
        return eventBusMap;
    }

    @Override
    public Set<Listener<?>> getListeners(String eventBusName) {
        EventBus eventBus = eventBusMap.get(eventBusName);
        if(eventBus == null)
            return null;
        return eventBus.getListeners();
    }

    @Override
    public <T> Listener<T> getListener(String eventBusName, String listenerName) {
        EventBus eventBus = eventBusMap.get(eventBusName);
        if(eventBus == null)
            return null;
        return eventBus.getListener(listenerName);
    }

    @Override
    public void postToAll(Object event) {
        for(EventBus eventBus : eventBusList)
            eventBus.post(event);
    }

    @Override
    public void postToAll(Object object, PostInfo postInfo) {
        for(EventBus eventBus : eventBusList)
            eventBus.post(object, postInfo);
    }

    @Override
    public void post(Object event, String... eventBusNames) {
        for(String busName : eventBusNames){
            EventBus eventBus = eventBusMap.get(busName);
            if(eventBus == null)
                continue;
            eventBus.post(event);
        }
    }

    @Override
    public void post(Object event, PostInfo postInfo, String... eventBusNames) {
        for(String busName : eventBusNames){
            EventBus eventBus = eventBusMap.get(busName);
            if(eventBus == null)
                continue;
            eventBus.post(event, postInfo);
        }
    }
}
