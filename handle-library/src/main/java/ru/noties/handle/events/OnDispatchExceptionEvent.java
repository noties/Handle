package ru.noties.handle.events;

/**
 * This event will be posted when {@link ru.noties.handle.IEventHandler} threw an Exception
 * during onEvent method execution
 * Created by Dimitry Ivanov on 21.07.2015.
 */
public final class OnDispatchExceptionEvent {

    private final Class<?> eventHandler;
    private final Object event;
    private final Throwable throwable;

    public OnDispatchExceptionEvent(Class<?> eventHandler, Object event, Throwable throwable) {
        this.eventHandler = eventHandler;
        this.event = event;
        this.throwable = throwable;
    }

    public Class<?> getEventHandler() {
        return eventHandler;
    }

    public Object getEvent() {
        return event;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
