package me.choicore.samples.pipeline;

@FunctionalInterface
public interface Pipeline<T> {
    /**
     * Execute the pipeline starting with the given item.
     * The returned Execution type parameter <S> is guaranteed to match the type of the input item.
     */
    <S extends T> Execution<S> execute(S initial);
}
