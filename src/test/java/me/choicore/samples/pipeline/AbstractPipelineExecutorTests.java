package me.choicore.samples.pipeline;

import me.choicore.samples.pipeline.exception.PipelineDefinitionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AbstractPipelineExecutorTests {
    @Test
    @DisplayName("파이프라인의 지정된 순서가 중복되었을 시 예외가 발생한다.")
    void t1() {
        Pipeline<String> step1 = new Pipeline<>() {
            @Override
            public Flow run(final String item) {
                return new Flow.Next<>(item);
            }

            @Override
            public int order() {
                return 1;
            }
        };

        Pipeline<String> step2 = new Pipeline<>() {
            @Override
            public Flow run(final String item) {
                return new Flow.Next<>(item);
            }

            @Override
            public int order() {
                return 1;
            }
        };

        assertThatThrownBy(() -> new AbstractPipelineExecutor<>(List.of(step1, step2)) {
        })
                .hasMessageStartingWith("Duplicate pipeline orders found:")
                .isInstanceOf(PipelineDefinitionException.class);
    }

    @Test
    @DisplayName("파이프라인의 지정된 순서가 중복되지 않았을 시 예외가 발생하지 않는다.")
    void t2() {
        Pipeline<String> step1 = new Pipeline<>() {
            @Override
            public Flow run(final String item) {
                return new Flow.Next<>(item);
            }

            @Override
            public int order() {
                return 1;
            }
        };

        Pipeline<String> step2 = new Pipeline<>() {
            @Override
            public Flow run(final String item) {
                return new Flow.Next<>(item);
            }

            @Override
            public int order() {
                return 2;
            }
        };

        assertThatNoException().isThrownBy(() -> new AbstractPipelineExecutor<>(List.of(step1, step2)) {
        });
    }
}