package me.choicore.samples.pipeline;

public interface Pipeline<T> {
    Flow run(T item);

    int order();
}
