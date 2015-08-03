package ru.noties.handle;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.List;

import ru.noties.handle.events.NoEventHandlerEvent;
import ru.noties.handle.events.OnDispatchExceptionEvent;
import ru.noties.handle.events.StickyEventNotUsedEvent;

/**
 * Created by Dimitry Ivanov on 21.07.2015.
 */
class HandleHandler extends Handler {

    enum HandleMessageType {

        NORMAL(1), DELAYED(2), AT_TIME(3), STICKY(4), STICKY_VALID(5), SPECIAL(6);

        final int type;

        HandleMessageType(int type) {
            this.type = type;
        }

        static HandleMessageType forValue(int v) {
            final HandleMessageType[] types = HandleMessageType.values();
            for (int i = types.length; --i >= 0; ) {
                if (types[i].type == v) {
                    return types[i];
                }
            }
            return null;
        }
    }

    void post(Object what) {
        final Message msg = obtain(HandleMessageType.NORMAL, what);
        sendMessage(msg);
    }

    private void postSpecial(Object what) {
        final Message msg = obtain(HandleMessageType.SPECIAL, what);
        sendMessage(msg);
    }

    void postDelayed(Object what, long delayMillis) {
        final Message msg = obtain(HandleMessageType.DELAYED, what);
        sendMessageDelayed(msg, delayMillis);
    }

    void postAtTime(Object what, long uptimeMillis) {
        final Message msg = obtain(HandleMessageType.AT_TIME, what);
        sendMessageAtTime(msg, uptimeMillis);
    }

    void postSticky(Object what) {
        final Message msg = obtain(HandleMessageType.STICKY, what);
        sendMessage(msg);
    }

    void postStickyValid(Object what, long delay) {
        final Message msg = obtain(HandleMessageType.STICKY_VALID, what);
        sendMessageDelayed(msg, delay);
    }

    void cancel(Object what) {
        final HandleMessageType[] types = HandleMessageType.values();
        for (int i = types.length - 1; --i >= 0; ) { // ignore stickies
            removeMessages(types[i].type, what);
        }
    }

    private Message obtain(HandleMessageType type, Object what) {
        return obtainMessage(type.type, what);
    }

    public HandleHandler(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        final HandleMessageType type = HandleMessageType.forValue(msg.what);
        if (type == null
                || msg.obj == null) {
            return;
        }

        final Object obj;
        switch (type) {
            case STICKY_VALID:
                obj = new StickyEventNotUsedEvent(msg.obj);
                break;
            default:
                obj = msg.obj;
                break;
        }

        final List<IEventHandler> handlers = Handle.getEventHandlers(obj.getClass());
        if (handlers != null
                && handlers.size() > 0) {
            for (IEventHandler eventHandler: handlers) {
                try {
                    eventHandler.onEvent(obj);
                } catch (Throwable t) {
                    postSpecial(new OnDispatchExceptionEvent(eventHandler.getClass(), obj, t));
                }
            }
        } else {
            // if there are no event handlers for NoEventHandlerEvent don't send it
            if (type != HandleMessageType.SPECIAL) {
                postSpecial(new NoEventHandlerEvent(obj));
            }
        }
    }
}
