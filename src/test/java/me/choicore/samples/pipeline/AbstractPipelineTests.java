package me.choicore.samples.pipeline;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

class AbstractPipelineTests {
    @Test
    @DisplayName("기본 생성")
    void t1() {
        assertThatNoException().isThrownBy(() -> {
            AbstractPipeline<Object> abstractPipelineExecutor = new AbstractPipeline<>(List.of(Flow.Next::new, Flow.Next::new)) {
            };
            assertThat(abstractPipelineExecutor.tasks()).hasSize(2);
            assertThat(abstractPipelineExecutor.terminationStrategy()).isEqualTo(TerminationStrategy.COMPLETED);
        });
    }
}
