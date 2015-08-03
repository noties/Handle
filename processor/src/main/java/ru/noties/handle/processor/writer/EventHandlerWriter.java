package ru.noties.handle.processor.writer;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import ru.noties.handle.processor.Logger;
import ru.noties.handle.processor.parser.EventHandlerHolder;

/**
 * Created by Dimitry Ivanov on 21.07.2015.
 */
public class EventHandlerWriter {

    private static final String OBJECT = "java.lang.Object";

    private final Logger mLogger;
    private final Elements mElements;
    private final Filer mFiler;

    public EventHandlerWriter(Logger logger, Elements elements, Filer filer) {
        this.mLogger = logger;
        this.mElements = elements;
        this.mFiler = filer;
    }

    public void write(EventHandlerHolder holder) {

        final TypeElement element = holder.element;

        final String holderPackage      = mElements.getPackageOf(element).toString();
        final String holderClassName    = createClassName(element);

        final Indent indent = new Indent();

        final StringBuilder builder = new StringBuilder();
        builder.append("package ")
                .append(holderPackage)
                .append(";\n");
        builder.append("public abstract class ")
                .append(holderClassName)
                .append(" implements ru.noties.handle.IEventHandler")
                .append(" {\n");

        indent.increment();

        final List<String> events = holder.eventClasses;
        if (events.contains(OBJECT)) {
            events.remove(OBJECT);
        }

        if (events.size() == 0) {
            return;
        }

        for (String event: events) {
            builder.append(indent);
            builder.append("public abstract void onEvent(")
                    .append(event)
                    .append(" event);");
            builder.append('\n');
        }

        builder
                .append(indent)
                .append("public final void onEvent(Object o) {\n");
        indent.increment();
        builder
                .append(indent)
                .append("final Class<?> c = o.getClass();\n");

        boolean isFirst = true;
        for (String event: events) {
            builder.append(indent);
            if (!isFirst) {
                builder.append("else ");
            } else {
                isFirst = false;
            }
            builder
                    .append("if (c == ")
                    .append(event)
                    .append(".class) this.onEvent((")
                    .append(event)
                    .append(") o);\n");
        }
        builder.append(indent.decrement())
                .append("}\n");

        builder.append(indent)
                .append("public final boolean isEventRegistered(Class<?> c) {\n")
                .append(indent.increment())
                .append("return ");

        isFirst = true;
        for (String event: events) {
            if (!isFirst) {
                builder.append(" || ");
            } else {
                isFirst = false;
            }
            builder.append("c == ")
                    .append(event)
                    .append(".class");
        }
        builder.append(";\n")
                .append(indent.decrement())
                .append("}\n");

        builder.append("}");

        Writer writer = null;
        try {
            final JavaFileObject javaFileObject = mFiler.createSourceFile(holderPackage + "." + holderClassName);
            writer = javaFileObject.openWriter();
            writer.write(builder.toString());
        } catch (IOException e) {
            mLogger.log(Diagnostic.Kind.ERROR, "Error writing java source file, e: %s", e);
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {}
            }
        }
    }

    private static String createClassName(Element e) {
        return e.getSimpleName().toString() + "EventHandler";
    }
}
