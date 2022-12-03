package org.cubic.esys;

public class SubscribeInfo {

    private final String name;

    private final int priority;

    public SubscribeInfo(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    public SubscribeInfo(int priority){
        this.name = "";
        this.priority = priority;
    }

    public SubscribeInfo(String name){
        this.name = name;
        this.priority = 0;
    }

    public SubscribeInfo() {
        this("", 0);
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }
}
