package me.choicore.samples.pipeline;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

class AbstractPipelineExecutorTests {
    @Test
    @DisplayName("기본 생성")
    void t1() {
        assertThatNoException().isThrownBy(() -> {
            AbstractPipelineExecutor<Object> abstractPipelineExecutor = new AbstractPipelineExecutor<>(List.of(Flow.Next::new, Flow.Next::new)) {
            };
            assertThat(abstractPipelineExecutor.pipelines()).hasSize(2);
            assertThat(abstractPipelineExecutor.terminationStrategy()).isEqualTo(TerminationStrategy.COMPLETED);
        });
    }
}
