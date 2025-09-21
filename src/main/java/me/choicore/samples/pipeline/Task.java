package me.choicore.samples.pipeline;

@FunctionalInterface
public interface Task<T> {
    Flow run(T item);

    default int order() {
        return 0;
    }
}
