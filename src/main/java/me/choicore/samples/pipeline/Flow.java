package me.choicore.samples.pipeline;

public sealed interface Flow<T> permits Flow.Next, Flow.Abort, Flow.Stop, Flow.Done {
    record Next<T>(T item) implements Flow<T> {
    }

    record Abort<T>(
            String reason,
            Throwable cause
    ) implements Flow<T> {
        public Abort(String reason) {
            this(reason, null);
        }
    }

    record Stop<T>(
            T item,
            String reason
    ) implements Flow<T> {
        public Stop(T item) {
            this(item, null);
        }
    }

    record Done<T>(T item) implements Flow<T> {
    }
}
