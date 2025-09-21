package me.choicore.samples.pipeline;

@FunctionalInterface
public interface Pipeline<T> {
    Flow run(T item);

    default int order() {
        return 0;
    }
}
