package ru.noties.handle.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Main annotation with Events classes that should be included in generated IEventHandler
 * Created by Dimitry Ivanov on 21.07.2015.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface EventHandler {
    Class<?>[] value();
}
