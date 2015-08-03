package ru.noties.handle;

import android.app.Activity;
import android.app.Fragment;
import android.os.Looper;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Main class for this library.
 *
 * The idea behind Handle is simple - create an event bus implementation specific for the Android platform.
 * This library relies on {@link android.os.Handler} to queue and deliver events and {@link IEventHandler}
 * to process these events.
 *
 * With the help of this library it becomes easy to reuse event handlers
 *
 * This library does not use reflection.
 *
 * Note that all events are delivered in Main thread
 *
 * @see #register(IEventHandler)
 * @see #unregister(IEventHandler)
 * @see #isRegistered(IEventHandler)
 *
 * @see #post(Object)
 * @see #postDelayed(Object, long)
 * @see #postAtTime(Object, long)
 * @see #cancel(Object)
 *
 * @see #postSticky(Object)
 * @see #cancelSticky(Object)
 *
 * @see #cancelAll()
 *
 * Created by Dimitry Ivanov on 21.07.2015.
 */
public class Handle {

    private static volatile Handle sInstance = null;

    public static Handle getInstance() {
        Handle local = sInstance;
        if (local == null) {
            synchronized (Handle.class) {
                local = sInstance;
                if (local == null) {
                    local = sInstance = new Handle();
                }
            }
        }
        return local;
    }

    public static final long DEF_STICKY_VALID_MILLIS = 1000L * 30;

    private final HandleHandler mHandler;
    private final Set<IEventHandler> mListeners;
    private final Set<Object> mStickies;

    private Handle() {
        this.mHandler   = new HandleHandler(Looper.getMainLooper());
        this.mListeners = new HashSet<>();
        this.mStickies  = new HashSet<>();
    }

    /**
     * Registers {@link IEventHandler} with this bus.
     * The best place to register for objects with lyfecycle is {@link Activity#onStart()} and {@link Fragment#onStart()}
     * @param who {@link IEventHandler} to handle events
     * @return true if {@link IEventHandler} was registered
     *          false if {@link IEventHandler} was already registered
     */
    public static boolean register(final IEventHandler who) {
        final Handle handle = Handle.getInstance();
        final boolean result = handle._register(who);
        if (result) {
            handle._deliverStickies(who);
        }
        return result;
    }

    /**
     * Unregisters an {@link IEventHandler}
     * @param who {@link IEventHandler} that wish to be unregistered
     * @return true if {@link IEventHandler} was unregistered
     *          false if {@link IEventHandler} was not registered with this bus
     */
    public static boolean unregister(IEventHandler who) {
        final Handle handle = Handle.getInstance();
        return handle._unregister(who);
    }

    /**
     * Checks whether {@link IEventHandler} is registered
     * @param who {@link IEventHandler} to query the registration state
     * @return true if {@link IEventHandler} is registered, false otherwise
     */
    public static boolean isRegistered(IEventHandler who) {
        return Handle.getInstance()._isRegistered(who);
    }

    /**
     * Places with Object in queue to be delivered. If no {@link IEventHandler} is registered
     * for this type of event {@link ru.noties.handle.events.NoEventHandlerEvent} will be posted
     * @param what an Object that should be delivered
     */
    public static void post(Object what) {
        Handle.getInstance().mHandler.post(what);
    }

    /**
     * The same as {@link #post(Object)} but with a delay before delivering an event
     * @see #post(Object)
     * @param what an Object to be queued
     * @param delay in milliseconds before delivery
     */
    public static void postDelayed(Object what, long delay) {
        Handle.getInstance().mHandler.postDelayed(what, delay);
    }

    /**
     * The same as {@link #post(Object)} but with a time when this event should be delivered
     * @see SystemClock#elapsedRealtime()
     * @see #post(Object)
     * @param what an Object to be queued
     * @param uptimeMillis the time when this event should be delivered
     */
    public static void postAtTime(Object what, long uptimeMillis) {
        Handle.getInstance().mHandler.postAtTime(what, uptimeMillis);
    }

    /**
     * Cancels an event delivery triggered with {@link #post(Object)}, {@link #postDelayed(Object, long)}
     * and {@link #postAtTime(Object, long)}
     * @param what an event to be removed for delivery queue
     */
    public static void cancel(Object what) {
        Handle.getInstance().mHandler.cancel(what);
    }

    /**
     * Posts a sticky event that will be stored in memory until {@link #cancelSticky(Object)} is called.
     * This event will be posted as a usual event if it has event handlers or
     * when an {@link IEventHandler} registers that could handle this event {@link #register(IEventHandler)}
     * If sticky event was not removed from the memory storage after {@link #DEF_STICKY_VALID_MILLIS} a
     * {@link ru.noties.handle.events.StickyEventUsedEvent} will be posted. If you wish to modify
     * a valid millis parameter see {@link #_postSticky(Object, long)}
     * @see #_postSticky(Object, long)
     * @param what a sticky event
     */
    public static void postSticky(Object what) {
        Handle.getInstance()._postSticky(what, DEF_STICKY_VALID_MILLIS);
    }

    public static void postSticky(Object what, long validMillis) {
        Handle.getInstance()._postSticky(what, validMillis);
    }

    /**
     * Cancels sticky event (removes from memory storage).
     * It's crucial to call this method when sticky event is no longer needed.
     * @param what a sticky event to be canceled
     */
    public static void cancelSticky(Object what) {
        Handle.getInstance()._cancelSticky(what);
    }

    /**
     * Cancels all pending event deliveries (including sticky events)
     */
    public static void cancelAll() {
        Handle.getInstance()._cancelAll();
    }

    private synchronized boolean _register(IEventHandler who) {
        return mListeners.add(who);
    }

    private synchronized boolean _unregister(IEventHandler who) {
        return mListeners.remove(who);
    }

    private synchronized boolean _isRegistered(IEventHandler who) {
        return mListeners.contains(who);
    }

    private synchronized void _cancelAll() {
        mHandler.removeCallbacksAndMessages(null);
        mStickies.clear();
    }

    private synchronized void _postSticky(Object what, long validMillis) {
        if (mListeners.size() != 0) {
            mHandler.postSticky(what);
        }
        mStickies.add(what);
        mHandler.postStickyValid(what, validMillis);
    }

    private synchronized void _cancelSticky(Object what) {
        mStickies.remove(what);
        mHandler.removeMessages(HandleHandler.HandleMessageType.STICKY.type, what);
        mHandler.removeMessages(HandleHandler.HandleMessageType.STICKY_VALID.type, what);
    }

    private synchronized void _deliverStickies(IEventHandler eventHandler) {
        if (mStickies.size() == 0) {
            return;
        }
        final Set<Object> tempSet = new HashSet<>(mStickies);
        for (Object o: tempSet) {
            if (eventHandler.isEventRegistered(o.getClass())) {
                eventHandler.onEvent(o);
            }
        }
    }

    static List<IEventHandler> getEventHandlers(Class<?> cl) {
        return Handle.getInstance()._getEventHandlers(cl);
    }

    private synchronized List<IEventHandler> _getEventHandlers(Class<?> cl) {
        final List<IEventHandler> out = new ArrayList<>();
        for (IEventHandler eventHandler: mListeners) {
            if (eventHandler.isEventRegistered(cl)) {
                out.add(eventHandler);
            }
        }
        if (out.size() == 0) {
            return null;
        }
        return out;
    }
}
