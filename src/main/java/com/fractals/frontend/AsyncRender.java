package com.fractals.frontend;

import javafx.application.Platform;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Runs back-end computations off the JavaFX thread, so the UI (and the JPro connection) stays
 * responsive. Requests are coalesced: while a job runs only the latest new request is kept and
 * it starts as soon as the current one finishes. All public methods must be called on the FX thread.
 */
final class AsyncRender<R> {

    /** Shared by all browser sessions of the JPro server. */
    private static final ExecutorService POOL = Executors.newCachedThreadPool(runnable -> {
        Thread thread = new Thread(runnable, "fractal-render");
        thread.setDaemon(true);
        return thread;
    });

    private final Consumer<R> onResult;
    private final Consumer<Throwable> onError;
    private Function<BooleanSupplier, R> pending;
    private boolean running;
    private volatile boolean disposed;

    AsyncRender(Consumer<R> onResult, Consumer<Throwable> onError) {
        this.onResult = onResult;
        this.onError = onError;
    }

    /** @param job receives a "cancelled" flag and returns the result (or null when cancelled) */
    void request(Function<BooleanSupplier, R> job) {
        if (disposed) {
            return;
        }
        if (running) {
            pending = job;
        } else {
            start(job);
        }
    }

    private void start(Function<BooleanSupplier, R> job) {
        running = true;
        POOL.execute(() -> {
            R result = null;
            Throwable error = null;
            try {
                result = job.apply(() -> disposed);
            } catch (Throwable t) {
                error = t;
            }
            R finalResult = result;
            Throwable finalError = error;
            Platform.runLater(() -> {
                running = false;
                if (!disposed) {
                    if (finalError != null) {
                        onError.accept(finalError);
                    } else if (finalResult != null) {
                        onResult.accept(finalResult);
                    }
                }
                if (pending != null && !disposed) {
                    Function<BooleanSupplier, R> next = pending;
                    pending = null;
                    start(next);
                }
            });
        });
    }

    /** Stops accepting work; a running job is asked to cancel. */
    void dispose() {
        disposed = true;
        pending = null;
    }
}
