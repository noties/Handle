package ru.noties.handle.benchmark.base.subjects;

import ru.noties.handle.Handle;
import ru.noties.handle.IEventHandler;
import ru.noties.handle.benchmark.base.EventReceiver;
import ru.noties.handle.benchmark.base.IEventBus;

/**
 * Created by Dimitry Ivanov on 02.09.2015.
 */
public class HandleEventBus implements IEventBus {

    @Override
    public String getName() {
        return "Handle";
    }

    @Override
    public void init() {

    }

    @Override
    public void register(Object object) {
        Handle.register((IEventHandler) object);
    }

    @Override
    public void unregister(Object object) {
        Handle.unregister((IEventHandler) object);
    }

    @Override
    public void post(Object object) {
        Handle.post(object);
    }

    @Override
    public Object newEvent() {
        return new HandleEvent();
    }

    @Override
    public EventReceiver getEventReceiver(int expected, EventReceiver.OnComplete onComplete) {
        return new HandleEventReceiver(expected, onComplete);
    }

    private static class HandleEventReceiver implements EventReceiver, IEventHandler {

        private final int mExpected;
        private final OnComplete mOnComplete;

        private int mReceived;

        HandleEventReceiver(int expected, OnComplete onComplete) {
            this.mExpected = expected;
            this.mOnComplete = onComplete;
        }

        @Override
        public void onEvent(Object o) {
            if (++mReceived == mExpected) {
                mOnComplete.apply();
            }
        }

        @Override
        public boolean isEventRegistered(Class<?> aClass) {
            return aClass == HandleEvent.class;
        }
    }

    private static class HandleEvent {

    }
}
