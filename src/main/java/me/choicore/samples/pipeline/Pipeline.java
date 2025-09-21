package me.choicore.samples.pipeline;

@FunctionalInterface
public interface Pipeline<T> {
    Flow run(T item);

    // FIXME: 함수형 인터페이스로 제공하고 싶어서 임시로 default 메서드로 제공
    default int order() {
        return 0;
    }
}
