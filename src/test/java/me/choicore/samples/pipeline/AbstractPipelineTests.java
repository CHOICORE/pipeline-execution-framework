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

    @Test
    @DisplayName("예제")
    void t2() {
        record Invoice(
                int amount
        ) {
        }

        Task<Invoice> validator = (Invoice invoice) -> {
            int amount = invoice.amount();
            if (amount < 0) {
                return new Flow.Abort<>("Amount is negative", null);
            }

            return new Flow.Next<>(invoice);
        };

        Task<Invoice> calculator = (Invoice invoice) -> {
            int factor = 1;
            if (invoice.amount() == 0) {
                return new Flow.Stop<>(invoice, "Amount is zero");
            }

            return new Flow.Done<>(new Invoice(invoice.amount() * factor));
        };

        Pipeline<Invoice> pipeline = new AbstractPipeline<>(List.of(validator, calculator), TerminationStrategy.COMPLETED) {
        };

        assertThat(pipeline.execute(new Invoice(-1))).isInstanceOf(Execution.Failed.class);
        assertThat(pipeline.execute(new Invoice(0))).isInstanceOf(Execution.Stopped.class);
        assertThat(pipeline.execute(new Invoice(1))).isInstanceOf(Execution.Completed.class);
    }
}
