package ru.noties.handle.events;

/**
 * Created by Dimitry Ivanov on 23.07.2015.
 */
public final class StickyEventNotUsedEvent {

    private final Object stickyEvent;

    public StickyEventNotUsedEvent(Object stickyEvent) {
        this.stickyEvent = stickyEvent;
    }

    public Object getStickyEvent() {
        return stickyEvent;
    }
}
