package me.choicore.samples.pipeline;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import me.choicore.samples.pipeline.exception.PipelineDefinitionException;
import me.choicore.samples.pipeline.exception.PipelineExecutionException;

import java.util.*;
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

        List<Pipeline<T>> sorted = new ArrayList<>(pipelines);

        sorted.sort(Comparator.comparingInt(Pipeline::order));

        Map<Integer, List<String>> duplicates = new LinkedHashMap<>();
        int total = sorted.size();
        for (int i = 0; i < total; ) {
            int o = sorted.get(i).order();
            int j = i + 1;
            while (j < total && sorted.get(j).order() == o) j++;
            int count = j - i;
            if (count > 1) {
                List<String> classes = new ArrayList<>(count);
                for (int k = i; k < j; k++) {
                    classes.add(sorted.get(k).getClass().getName());
                }
                duplicates.put(o, classes);
            }
            i = j;
        }

        if (!duplicates.isEmpty()) {
            StringBuilder msg = new StringBuilder("Duplicate pipeline orders found:\n");
            duplicates.forEach((o, clazz) -> msg
                    .append("\t[")
                    .append(o)
                    .append("] -> ")
                    .append(clazz)
                    .append('\n'));
            throw new PipelineDefinitionException(msg.toString().trim());
        }

        List<Pipeline<T>> unmodifiable = Collections.unmodifiableList(sorted);

        log.debug("Initialized {} pipeline(s):\n{}", total, unmodifiable
                .stream()
                .map(p -> String.format("\t [%d/%d] %s", p.order(), total, p.getClass().getName()))
                .collect(Collectors.joining("\n")));

        return unmodifiable;
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
