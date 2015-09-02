package ru.noties.handle.benchmark.base;

import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;

import ru.noties.debug.Debug;

/**
 * Created by Dimitry Ivanov on 02.09.2015.
 */
public abstract class AbsBenchmarkRunnable implements Runnable {

    private final List<Item> mItems = new ArrayList<>();

    private long mStart;

    protected void start() {
        mStart = SystemClock.elapsedRealtime();
    }

    protected void end(String message, Object... args) {
        final long end = SystemClock.elapsedRealtime();
        mItems.add(new Item(
                String.format(message, args),
                end - mStart
        ));
    }

    public List<Item> getItems() {
        return mItems;
    }

    protected void printResults() {
        Debug.i("######################################################");
        for (Item item: mItems) {
            Debug.i("%s, %d ms", item.message, item.time);
        }
        Debug.i("######################################################");
    }

    public static class Item {

        private final String message;
        private final long time;

        Item(String message, long time) {
            this.message = message;
            this.time = time;
        }

        public String getMessage() {
            return message;
        }

        public long getTime() {
            return time;
        }
    }
}
