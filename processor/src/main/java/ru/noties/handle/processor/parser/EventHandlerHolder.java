package ru.noties.handle.processor.parser;

import java.util.List;

import javax.lang.model.element.TypeElement;

/**
 * Created by Dimitry Ivanov on 21.07.2015.
 */
public class EventHandlerHolder {

    public final TypeElement element;
    public final List<String> eventClasses;

    EventHandlerHolder(TypeElement element, List<String> eventClasses) {
        this.element = element;
        this.eventClasses = eventClasses;
    }
}
