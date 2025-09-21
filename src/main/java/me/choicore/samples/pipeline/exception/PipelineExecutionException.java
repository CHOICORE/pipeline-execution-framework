package me.choicore.samples.pipeline.exception;

public class PipelineExecutionException extends PipelineException {
    public PipelineExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public PipelineExecutionException(String message) {
        super(message);
    }
}