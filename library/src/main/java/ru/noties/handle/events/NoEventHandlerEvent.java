package ru.noties.handle.events;

/**
 * This event will be posted when no event handler was found to handle a certain event.
 * Contains event that was not delivered to any {@link ru.noties.handle.IEventHandler}
 * Created by Dimitry Ivanov on 21.07.2015.
 */
public final class NoEventHandlerEvent {

    private final Object event;

    public NoEventHandlerEvent(Object event) {
        this.event = event;
    }

    public Object getEvent() {
        return event;
    }
}
