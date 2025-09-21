package me.choicore.samples.pipeline;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import me.choicore.samples.pipeline.exception.PipelineExecutionException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Accessors(fluent = true)
@Getter(AccessLevel.PROTECTED)
public abstract class AbstractPipelineExecutor<T> implements PipelineExecutor<T> {
    private final List<Pipeline<T>> pipelines;
    private final TerminationStrategy terminationStrategy;

    public AbstractPipelineExecutor(final List<Pipeline<T>> pipelines) {
        this(pipelines, TerminationStrategy.COMPLETED);
    }

    public AbstractPipelineExecutor(final List<Pipeline<T>> pipelines, final TerminationStrategy terminationStrategy) {
        this.pipelines = initialize(pipelines);
        this.terminationStrategy = terminationStrategy;
    }

    private List<Pipeline<T>> initialize(List<Pipeline<T>> pipelines) {
        if (pipelines == null || pipelines.isEmpty()) return List.of();

        List<Pipeline<T>> sorted = pipelines.stream()
                .sorted(Comparator.comparingInt(Pipeline::order))
                .toList();

        log.debug("Initialized {} pipeline(s):\n{}", sorted.size(), sorted
                .stream()
                .map(p -> String.format("\t [%d/%d] %s", p.order(), sorted.size(), p.getClass().getName()))
                .collect(Collectors.joining("\n")));

        return sorted;
    }

    @SuppressWarnings("unchecked")
    public <S extends T> Execution<S> execute(final S initial) {
        if (this.pipelines == null || this.pipelines.isEmpty()) {
            log.info("No pipelines configured; returning initial.");
            return Execution.completed(initial);
        }

        S item = initial;
        int step = 1;
        int total = pipelines.size();

        while (step <= total) {
            final Pipeline<T> pipeline = pipelines.get(step - 1);
            final Flow flow;

            try {
                flow = pipeline.run(item);
            } catch (Throwable t) {
                throw new PipelineExecutionException(String.format("Pipeline step %d/%d failed", step, total), t);
            }

            switch (flow) {
                case Flow.Next<?> n -> {
                    log.debug("Pipeline proceeding to step {}/{}", step, total);
                    item = (S) n.item();
                    step++;
                }
                case Flow.Abort a -> {
                    log.error("Aborting pipeline execution: {}/{}", step, total, a.cause());
                    return Execution.failed(item, a.reason(), a.cause());
                }
                case Flow.Stop<?> s -> {
                    log.info("Pipeline stopped at step {}/{}", step, total);
                    return Execution.stopped((S) s.item(), s.reason());
                }

                case Flow.Done<?> d -> {
                    log.info("Pipeline done at step {}/{}", step, total);
                    return Execution.completed((S) d.item());
                }
            }
        }

        return switch (this.terminationStrategy) {
            case COMPLETED -> Execution.completed(item);
            case FAILED -> {
                String reason = String.format("Pipeline completed (%d steps) without explicit termination", total);
                yield Execution.failed(item, reason, null);
            }
            case STOPPED -> Execution.stopped(item, "Pipeline reached end without explicit termination");
        };
    }
}
