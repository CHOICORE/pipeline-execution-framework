package me.choicore.samples.pipeline;

public sealed interface Execution<T> permits Execution.Completed, Execution.Stopped, Execution.Failed {

    static <T> Completed<T> completed(T result) {
        return new Completed<>(result);
    }

    static <T> Stopped<T> stopped(T result, String reason) {
        return new Stopped<>(result, reason);
    }

    static <T> Failed<T> failed(T source, String reason, Throwable cause) {
        return new Failed<>(source, reason, cause);
    }

    static <T> Failed<T> failed(T source, String reason) {
        return new Failed<>(source, reason, null);
    }

    record Completed<T>(T result) implements Execution<T> {
    }

    record Stopped<T>(T result, String reason) implements Execution<T> {
    }

    record Failed<T>(
            T source,
            String reason,
            Throwable cause
    ) implements Execution<T> {
    }
}
