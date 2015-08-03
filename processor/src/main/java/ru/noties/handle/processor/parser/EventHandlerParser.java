package ru.noties.handle.processor.parser;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

import ru.noties.handle.processor.Logger;

/**
 * Created by Dimitry Ivanov on 21.07.2015.
 */
public class EventHandlerParser {

    private final Logger mLogger;

    public EventHandlerParser(Logger logger) {
        this.mLogger = logger;
    }

    public EventHandlerHolder parse(TypeElement element) {
        final ru.noties.handle.annotations.EventHandler eventHandler = element.getAnnotation(ru.noties.handle.annotations.EventHandler.class);
        if (eventHandler == null) {
            return null;
        }

        final List<String> events = new ArrayList<>();

        try {
            final Class<?>[] classes = eventHandler.value();
            for (Class<?> c: classes) {
                events.add(c.getCanonicalName());
            }
        } catch (MirroredTypesException e) {
            final List<? extends TypeMirror> typeMirrors = e.getTypeMirrors();
            if (typeMirrors != null
                    && typeMirrors.size() > 0) {
                for (TypeMirror mirror: typeMirrors) {
                    events.add(mirror.toString());
                }
            }
        }

        if (events.size() == 0) {
            return null;
        }

        return new EventHandlerHolder(element, events);
    }
}
