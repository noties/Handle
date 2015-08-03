package ru.noties.handle.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import ru.noties.handle.processor.parser.EventHandlerHolder;
import ru.noties.handle.processor.parser.EventHandlerParser;
import ru.noties.handle.processor.writer.EventHandlerWriter;

public class HandleProcessor extends AbstractProcessor implements Logger {

    private Types mTypes;
    private Elements mElements;
    private Filer mFiler;
    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        mTypes = env.getTypeUtils();
        mElements = env.getElementUtils();
        mFiler = env.getFiler();
        mMessager = env.getMessager();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(ru.noties.handle.annotations.EventHandler.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            for (TypeElement typeElement: annotations) {
                processInner(roundEnv.getElementsAnnotatedWith(typeElement));
            }
            return true;
        } catch (Throwable t) {
            log(Diagnostic.Kind.ERROR, "Error while code generation, t: %s", t);
        }
        return false;
    }

    private void processInner(Set<? extends Element> elements) throws Throwable {
        final EventHandlerParser parser = new EventHandlerParser(this);
        final List<EventHandlerHolder> holders = new ArrayList<>();
        EventHandlerHolder holder;
        for (Element element: elements) {
            holder = parser.parse((TypeElement) element);
            if (holder != null) {
                holders.add(holder);
            }
        }
        if (holders.size() > 0) {
            final EventHandlerWriter writer = new EventHandlerWriter(this, mElements, mFiler);
            for (EventHandlerHolder h: holders) {
                writer.write(h);
            }
        }
    }

    @Override
    public void log(Diagnostic.Kind level, String message, Object... args) {
        if (args == null
                || args.length == 0) {
            mMessager.printMessage(level, message);
            return;
        }
        mMessager.printMessage(level, String.format(message, args));
    }
}
