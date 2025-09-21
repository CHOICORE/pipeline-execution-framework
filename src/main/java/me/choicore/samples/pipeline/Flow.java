package me.choicore.samples.pipeline;

public sealed interface Flow permits Flow.Next, Flow.Abort, Flow.Stop, Flow.Done {
    record Next<T>(T item) implements Flow {
    }

    record Abort(
            String reason,
            Throwable cause
    ) implements Flow {
        public Abort(String reason) {
            this(reason, null);
        }
    }

    record Stop<T>(
            T item,
            String reason
    ) implements Flow {
        public Stop(T item) {
            this(item, null);
        }
    }

    record Done<T>(T item) implements Flow {
    }
}
