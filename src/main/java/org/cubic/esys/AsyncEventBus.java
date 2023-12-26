package org.cubic.esys;

@WorkInProgress
public interface AsyncEventBus extends AttachableEventBus {

    <T extends EventBus> T toSynced();
}
