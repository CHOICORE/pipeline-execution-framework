package me.choicore.samples.pipeline.exception;

public class PipelineException extends RuntimeException {
    public PipelineException(String message, Throwable cause) {
        super(message, cause);
    }

    public PipelineException(String message) {
        super(message);
    }
}