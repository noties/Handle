package ru.noties.handle.benchmark.base;

/**
 * Created by Dimitry Ivanov on 02.09.2015.
 */
public class BenchmarkParams {

    final int subscribers;
    final int events;

    public BenchmarkParams(int subscribers, int events) {
        this.subscribers = subscribers;
        this.events = events;
    }
}
