[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Handle-green.svg?style=flat)](https://android-arsenal.com/details/1/2437)

# Handle 
Handler-based Eventbus for Android


### Features
* No Reflection during runtime
* Reusable Event-handlers
* Sticky events with controllable lifetime
* android.os.Handler underneath
* Extremely fast and lightweight
* Ability to generate event handlers with `apt`

### Basic usage
[![core](https://img.shields.io/maven-central/v/ru.noties.handle/core.svg)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties.handle%22%20AND%20a%3A%22core%22)
```groovy
compile 'ru.noties.handle:core:x.x.x'
```
#### Introduction
The Handle library revolves around event handlers. To start using Handle one must create an implementation of the `ru.noties.handle.IEventHandler`
```java
public interface IEventHandler {
    void onEvent(Object event);
    boolean isEventRegistered(Class<?> cl);
}
```
Then, as usual
```java
Handle.register(IEventHandler);
Handle.unregister(IEventHandler);
```

#### Simple Posting
As long as Handle hides `android.os.Handler` posting could be done with these methods:
```java
Handle.post(Object); // simple
Handle.postDelayed(Object, long); // with delay
Handle.postAtTime(Object, SystemUptimeMillis + delay); // posting at specific time in the future
```
Also, there is a possibility to cancel every enqueued simple event:
```java
Handle.cancel(Object)
```

#### Sticky posting
Handle gives an ability to post sticky events and control its' lifetime
```java
Handle.postSticky(Object); // simple with default lifetime (currently 30 seconds)
Handle.postSticky(Object, long validMillis); // with custom lifetime
```
Every posted sticky event is intended to be cancelled at some point, that's why after sticky event is recieved one should cancel its delivery
```java
Handle.cancelSticky(Object);
```
If sticky event is not cancelled after it's specified duration of lifetime `ru.noties.handle.events.StickyEventNotUsedEvent` is fired, which gives an ability to cancel it.

If you wish to cancel all pending events (including sticky) call:
```java
Handle.cancelAll();
```

#### Special events
* `ru.noties.handle.events.StickyEventNotUsedEvent` is fired when StickyEvent is not cancelled after its lifetime
* `ru.noties.handle.events.OnDispatchExceptionEvent` is fired when an Exception was thrown during event delivery
* `ru.noties.handle.events.NoEventHandlerEvent` is fired when posted Event has no IEventHandler which can receive it

### Code generation (apt)
Handle also comes with `processor` and `annotations` modules

[![processor](https://img.shields.io/maven-central/v/ru.noties.handle/processor.svg)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties.handle%22%20AND%20a%3A%22processor%22)
[![annotations](https://img.shields.io/maven-central/v/ru.noties.handle/annotations.svg)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties.handle%22%20AND%20a%3A%22annotations%22)
```groovy
apt 'ru.noties.handle:processor:x.x.x'
compile 'ru.noties.handle:annotations:x.x.x'
```
Annotate the class, which would receive events with `@ru.noties.handle.annotations.EventHandler`, for example (from sample application)
```java
@EventHandler({ OnDispatchExceptionEvent.class, NoEventHandlerEvent.class, StickyEventNotUsedEvent.class})
public class BaseActivity extends Activity { }
```
After `build` there will be a generated IEventHandler in the package of annotated class with the name `*EventHandler` (continued from sample application):
```java
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
	public void onEvent(StickyEventNotUsedEvent event) {
		final Object sticky = event.getStickyEvent();
		Debug.i("sticky no used, removing, sticky: %s", sticky);
		Handle.cancelSticky(sticky);
	}
};
```

## License

```
  Copyright 2015 Dimitry Ivanov (mail@dimitryivanov.ru)

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
```