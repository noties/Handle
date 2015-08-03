package ru.noties.handle.sample;

import android.os.Bundle;
import android.os.SystemClock;

import ru.noties.debug.Debug;
import ru.noties.handle.Handle;
import ru.noties.handle.IEventHandler;
import ru.noties.handle.EventHandler;

/**
 * Created by Dimitry Ivanov on 21.07.2015.
 */

@EventHandler({NotParcelableObject.class, String.class, StickyEvent.class, StickyThatIsNotCancelledEvent.class})
public class MainActivity extends BaseActivity {

    private final IEventHandler mHandler = new MainActivityEventHandler() {

        @Override
        public void onEvent(NotParcelableObject event) {
            Debug.i("not parcelable: %s", event);
            throw new NullPointerException();
        }

        @Override
        public void onEvent(String event) {
            Debug.i("string: %s", event);
        }

        @Override
        public void onEvent(StickyEvent event) {
            Debug.i("sticky, this: %s, event: %s", MainActivity.this, event.getText());
            Handle.cancelSticky(event);
        }

        @Override
        public void onEvent(StickyThatIsNotCancelledEvent event) {
            Debug.i("received sticky that is not cancelled event: %s", event);
        }
    };

    @Override
    public void onCreate(Bundle sis) {
        super.onCreate(sis);
    }

    @Override
    public void onStart() {
        super.onStart();

        Handle.register(mHandler);

        final NotParcelableObject inner = new NotParcelableObject()
                .setI(1)
                .setL(-1L)
                .setS("inner");

        final NotParcelableObject object = new NotParcelableObject()
                .setI(12)
                .setL(System.currentTimeMillis())
                .setS("hello handler!")
                .setO(inner);

        Handle.post(object);

//        for (int i = 0; i < 10; i++) {
//            final int j = i;
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(j * 1000L);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    Handle.post(object);
//                    Handle.post(String.format("thread: %s", Thread.currentThread()));
//                    Handle.post(new Object());
//                }
//            }).start();
//        }
//
        Handle.postDelayed("Delay", 1000L);
        Handle.postAtTime("AtTime", SystemClock.uptimeMillis() + 2000L);

//        Handle.postSticky(new StickyEvent(String.format("this: %s", this)));
    }

    @Override
    public void onStop() {
        super.onStop();

        Handle.unregister(mHandler);

        Handle.postSticky(new StickyEvent(String.format("%s", this)));
        Handle.postSticky(new StickyThatIsNotCancelledEvent());
    }
}
