package ru.noties.handle.sample;

import android.app.Activity;

import ru.noties.debug.Debug;
import ru.noties.handle.Handle;
import ru.noties.handle.IEventHandler;
import ru.noties.handle.events.NoEventHandlerEvent;
import ru.noties.handle.events.OnDispatchExceptionEvent;
import ru.noties.handle.events.StickyEventUsedEvent;
import ru.noties.handle.EventHandler;

/**
 * Created by Dimitry Ivanov on 21.07.2015.
 */
@EventHandler({ OnDispatchExceptionEvent.class, NoEventHandlerEvent.class, StickyEventUsedEvent.class})
public class BaseActivity extends Activity {

    private final IEventHandler mHandler = new BaseActivityEventHandler() {
        @Override
        public void onEvent(OnDispatchExceptionEvent event) {
            Debug.e(event.getThrowable(), "Handler: %s, event: %s", event.getEventHandler(), event.getEvent());
        }

        @Override
        public void onEvent(NoEventHandlerEvent event) {
            Debug.i("noEventHandler: %s", event.getEvent());
        }

        @Override
        public void onEvent(StickyEventUsedEvent event) {
            final Object sticky = event.getStickyEvent();
            Debug.i("sticky no used, removing, sticky: %s", sticky);
            Handle.cancelSticky(sticky);
        }
    };

    @Override
    public void onStart() {
        super.onStart();

        Handle.register(mHandler);
    }

    @Override
    public void onStop() {
        super.onStop();

        Handle.unregister(mHandler);
    }


}
