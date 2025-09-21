package me.choicore.samples.pipeline.exception;

public class PipelineDefinitionException extends PipelineException {
    public PipelineDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public PipelineDefinitionException(String message) {
        super(message);
    }
}