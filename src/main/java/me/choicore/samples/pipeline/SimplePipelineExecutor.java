package me.choicore.samples.pipeline;

import java.util.List;

public class SimplePipelineExecutor<T> extends AbstractPipelineExecutor<T> {
    public SimplePipelineExecutor(final List<Pipeline<T>> pipelines, final TerminationStrategy terminationStrategy) {
        super(pipelines, terminationStrategy);
    }
}
