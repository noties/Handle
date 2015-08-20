package ru.noties.handle;

/**
 * Created by Dimitry Ivanov on 21.07.2015.
 */
public interface IEventHandler {
    void onEvent(Object event);
    boolean isEventRegistered(Class<?> cl);
}
