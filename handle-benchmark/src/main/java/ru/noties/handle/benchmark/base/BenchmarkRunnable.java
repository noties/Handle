package ru.noties.handle.benchmark.base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dimitry Ivanov on 02.09.2015.
 */
public class BenchmarkRunnable extends AbsBenchmarkRunnable {

    private final BenchmarkParams mBenchmarkParams;
    private final IEventBus mEventBus;

    public BenchmarkRunnable(BenchmarkParams params, IEventBus bus) {
        this.mBenchmarkParams = params;
        this.mEventBus = bus;
    }

    @Override
    public void run() {

        final IEventBus bus = mEventBus;

        final int receivers = mBenchmarkParams.subscribers;
        final int events = mBenchmarkParams.events;

        final List<EventReceiver> eventReceivers = new ArrayList<>();

        final EventReceiver.OnComplete onComplete = new EventReceiver.OnComplete() {

            int mCompleted = 0;

            @Override
            public void apply() {
                if (++mCompleted == receivers) {
                    end("%s, Received %d events in %d receivers", bus.getName(), events, receivers);
                    printResults();
                }
            }
        };

        start();
        for (int i = 0; i < receivers; i++) {
            eventReceivers.add(bus.getEventReceiver(events, onComplete));
        }
        end("%s, Generated %d receivers", bus.getName(), receivers);

        start();
        for (int i = 0; i < receivers; i++) {
            bus.register(eventReceivers.get(i));
        }
        end("%s, Registered %d receivers", bus.getName(), receivers);

        start();
        Object o;
        for (int i = 0; i < events; i++) {
            o = bus.newEvent();
            bus.post(o);
        }
    }
}
