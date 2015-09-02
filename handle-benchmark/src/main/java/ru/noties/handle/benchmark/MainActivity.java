package ru.noties.handle.benchmark;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ru.noties.debug.Debug;
import ru.noties.debug.out.AndroidLogDebugOutput;
import ru.noties.handle.benchmark.base.BenchmarkParams;
import ru.noties.handle.benchmark.base.BenchmarkRunnable;
import ru.noties.handle.benchmark.base.IEventBus;
import ru.noties.handle.benchmark.base.subjects.GreenRobotEventBus;
import ru.noties.handle.benchmark.base.subjects.HandleEventBus;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Debug.init(new AndroidLogDebugOutput(true));

        final IEventBus[] buses = new IEventBus[] {
                new HandleEventBus(),
                new GreenRobotEventBus(),
        };

        final BenchmarkParams[] params = new BenchmarkParams[] {
                new BenchmarkParams(1, 10),
                new BenchmarkParams(10, 1),
                new BenchmarkParams(10, 10),
                new BenchmarkParams(10, 100),
                new BenchmarkParams(100, 1),
                new BenchmarkParams(100, 1000),
                new BenchmarkParams(1000, 1),
//                new BenchmarkParams(1000, 10000)
        };

        final Executor executor = Executors.newFixedThreadPool(1);

        for (IEventBus bus: buses) {
            for (BenchmarkParams p: params) {
                executor.execute(new BenchmarkRunnable(p, bus));
            }
        }
    }
}
