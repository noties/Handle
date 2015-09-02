package ru.noties.handle.benchmark.base;

/**
 * Created by Dimitry Ivanov on 02.09.2015.
 */
public interface IEventBus {

    String getName();

    void init();

    void register(Object object);
    void unregister(Object object);
    void post(Object object);

    Object newEvent();

    EventReceiver getEventReceiver(int expected, EventReceiver.OnComplete onComplete);
}
