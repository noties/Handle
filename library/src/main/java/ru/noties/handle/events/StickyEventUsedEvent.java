package ru.noties.handle.events;

/**
 * Created by Dimitry Ivanov on 23.07.2015.
 */
public final class StickyEventUsedEvent {

    private final Object stickyEvent;

    public StickyEventUsedEvent(Object stickyEvent) {
        this.stickyEvent = stickyEvent;
    }

    public Object getStickyEvent() {
        return stickyEvent;
    }
}
