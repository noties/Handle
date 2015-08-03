package ru.noties.handle.sample;

import android.app.Application;

import ru.noties.debug.Debug;
import ru.noties.debug.out.AndroidLogDebugOutput;

/**
 * Created by Dimitry Ivanov on 21.07.2015.
 */
public class MainApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Debug.init(new AndroidLogDebugOutput(BuildConfig.DEBUG));
    }
}
