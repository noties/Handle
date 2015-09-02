package ru.noties.handle.benchmark.base.subjects;

import de.greenrobot.event.EventBus;
import ru.noties.handle.benchmark.base.EventReceiver;
import ru.noties.handle.benchmark.base.IEventBus;

/**
 * Created by Dimitry Ivanov on 02.09.2015.
 */
public class GreenRobotEventBus implements IEventBus {

    @Override
    public String getName() {
        return "GreenRobot";
    }

    @Override
    public void init() {

    }

    @Override
    public void register(Object object) {
        EventBus.getDefault().register(object);
    }

    @Override
    public void unregister(Object object) {
        EventBus.getDefault().unregister(object);
    }

    @Override
    public void post(Object object) {
        EventBus.getDefault().post(object);
    }

    @Override
    public Object newEvent() {
        return new GreenRobotEvent();
    }

    @Override
    public EventReceiver getEventReceiver(int expected, EventReceiver.OnComplete onComplete) {
        return new GreenRobotEventReceiver(expected, onComplete);
    }

    private static class GreenRobotEvent {

    }

    private static class GreenRobotEventReceiver implements EventReceiver {

        private final int mExpected;
        private final EventReceiver.OnComplete mOnComplete;

        private int mReceived;

        GreenRobotEventReceiver(int expected, EventReceiver.OnComplete onComplete) {
            this.mExpected = expected;
            this.mOnComplete = onComplete;
        }

        public void onEventMainThread(GreenRobotEvent o) {
            if (++mReceived == mExpected) {
                mOnComplete.apply();
            }
        }
    }
}
