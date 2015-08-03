package ru.noties.handle.test;

import junit.framework.TestCase;

import ru.noties.handle.Handle;
import ru.noties.handle.HandleMediator;
import ru.noties.handle.IEventHandler;
import ru.noties.handle.events.NoEventHandlerEvent;
import ru.noties.handle.events.OnDispatchExceptionEvent;
import ru.noties.handle.events.StickyEventNotUsedEvent;

/**
 * Created by Dimitry Ivanov on 03.08.2015.
 */
public class HandleTest extends TestCase {

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        HandleMediator.clear();
    }

    public void testRegister() {
        final IEventHandler handler = new NoOpEventHandler();
        final boolean result = Handle.register(handler);
        assertTrue(result);
        assertTrue(Handle.isRegistered(handler));
    }

    public void testNotRegistered() {
        assertFalse(Handle.isRegistered(new NoOpEventHandler()));
    }

    public void testUnregister() {
        final IEventHandler handler = new NoOpEventHandler();
        final boolean result = Handle.register(handler);
        assertTrue(result);
        assertTrue(Handle.isRegistered(handler));
        final boolean unregister = Handle.unregister(handler);
        assertTrue(unregister);
        assertFalse(Handle.isRegistered(handler));
    }

    public void testEventHandler() {
        final IEventHandler handler = new IEventHandler() {
            @Override
            public void onEvent(Object event) {

            }

            @Override
            public boolean isEventRegistered(Class<?> cl) {
                return cl == String.class;
            }
        };
        assertTrue(handler.isEventRegistered(String.class));
        assertFalse(handler.isEventRegistered(Integer.class));
    }

    public void testDeliverySingleRecipient() {
        final int count = 10000;
        final CountEventHandler handler = new CountEventHandler();
        Handle.register(handler);
        for (int i = count; --i > -1; ) {
            Handle.post("");
        }

        sleep(5000L);

        assertEquals(count, handler.received);
    }

    public void testDeliveryMultipleRecipients() {
        final int handlersCount = 100;
        final CountEventHandler[] handlers = new CountEventHandler[handlersCount];
        for (int i = handlersCount; --i > -1; ) {
            handlers[i] = new CountEventHandler();
            Handle.register(handlers[i]);
        }

        final int count = 1000;
        for (int i = count; --i > -1; ) {
            Handle.post("");
        }

        sleep(5000L);

        for (CountEventHandler handler: handlers) {
            assertEquals(count, handler.received);
        }
    }

    public void testStickies() {
        final CountEventHandler handler = new CountEventHandler();
        Handle.register(handler);
        assertEquals(0, handler.received);

        final String s = "";

        Handle.postSticky(s);

        sleep(1000L);

        assertEquals(1, handler.received);

        final CountEventHandler otherHandler = new CountEventHandler();
        Handle.register(otherHandler);
        assertEquals(1, otherHandler.received);
    }

    public void testCancelSticky() {
        final String s = "";
        Handle.postSticky(s);
        Handle.cancelSticky(s);
        final CountEventHandler handler = new CountEventHandler();
        Handle.register(handler);
        assertEquals(0, handler.received);
    }

    public void testNotUsedSticky() {
        final ClassEventHandler handler = new ClassEventHandler(StickyEventNotUsedEvent.class);
        Handle.register(handler);
        final long valid = 1000L;
        Handle.postSticky("sticky", valid);

        sleep(valid * 2);

        assertEquals(1, handler.received);
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void testNoHandler() {
        final ClassEventHandler handler = new ClassEventHandler(NoEventHandlerEvent.class);
        Handle.register(handler);
        Handle.post("");
        sleep(2000L);
        assertEquals(1, handler.received);
    }

    public void testException() {
        final ClassEventHandler handler = new ClassEventHandler(OnDispatchExceptionEvent.class);
        final IEventHandler ex = new IEventHandler() {
            @Override
            public void onEvent(Object event) {
                throw new NullPointerException();
            }

            @Override
            public boolean isEventRegistered(Class<?> cl) {
                return cl == String.class;
            }
        };
        Handle.register(handler);
        Handle.register(ex);
        Handle.post("");

        sleep(2000L);
        assertEquals(1, handler.received);
    }

    private static class NoOpEventHandler implements IEventHandler {

        @Override
        public void onEvent(Object event) {

        }

        @Override
        public boolean isEventRegistered(Class<?> cl) {
            return false;
        }
    }

    private static class CountEventHandler implements IEventHandler {

        int received;

        @Override
        public void onEvent(Object event) {
            received++;
        }

        @Override
        public boolean isEventRegistered(Class<?> cl) {
            return true;
        }
    }

    private static class ClassEventHandler implements IEventHandler {

        final Class<?> cl;
        int received;

        private ClassEventHandler(Class<?> cl) {
            this.cl = cl;
        }

        @Override
        public void onEvent(Object event) {
            received++;
        }

        @Override
        public boolean isEventRegistered(Class<?> cl) {
            return this.cl == cl;
        }
    }
}
