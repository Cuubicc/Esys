package org.cubic.esys;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AttachableEventDispatcher extends EventDispatcher implements AttachableEventBus {

    protected final Map<String, List<EventBus>> attached;

    protected final boolean attachPostInfo;

    public AttachableEventDispatcher(String name, boolean superListeners, boolean searchFields, boolean attachPostInfo) {
        super(name, superListeners, searchFields);
        this.attached = new ConcurrentHashMap<>();
        this.attachPostInfo = attachPostInfo;
    }

    public AttachableEventDispatcher(String name) {
        super(name);
        this.attached = new ConcurrentHashMap<>();
        this.attachPostInfo = false;
    }

    @Override
    public void post(Object event, PostInfo info) {
        super.post(event, info);
        for(Map.Entry<String, List<EventBus>> entry : attached.entrySet())
            for(EventBus eventBus : entry.getValue())
                if(attachPostInfo)
                    eventBus.post(event, info);
                else
                    eventBus.post(event);
    }

    @Override
    public void attach(EventBus bus) {
        List<EventBus> list = attached.computeIfAbsent(bus.name(), t -> new ArrayList<>());
        list.add(bus);
    }

    @Override
    public void detach(EventBus bus) {
        List<EventBus> list = attached.get(bus.name());
        if(list == null)
            return;
        list.remove(bus);
    }

    @Override
    public List<EventBus> eventBusesForName(String name) {
        return attached.computeIfAbsent(name, t -> new ArrayList<>());
    }
}
