package ru.noties.handle.benchmark.base;

/**
 * Created by Dimitry Ivanov on 02.09.2015.
 */
public interface EventReceiver {

    interface OnComplete {
        void apply();
    }
}
