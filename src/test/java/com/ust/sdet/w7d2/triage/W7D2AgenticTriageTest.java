package com.ust.sdet.w7d2.triage;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class W7D2AgenticTriageTest {
    private static final Path ARTIFACTS = Path.of("src/test/resources/w7d2/failed-run");
    private static final ToolRequest FAILED_RUN = new ToolRequest("run-142", "coupon-checkout", "staging");

    @Test
    void boundedLoopLinksEvidenceRerunsOnceAndOnlyProposesFix() throws IOException {
        AtomicInteger reruns = new AtomicInteger();
        List<TriageTool> tools = tools(reruns);
        BoundedTriageAgent agent = new BoundedTriageAgent(
            tools,
            new OfflineInventoryOutagePlanner(),
            6,
            Duration.ofSeconds(60)
        );

        TriageReport report = agent.investigate(FAILED_RUN);
        String markdown = report.toMarkdown();
        Path output = Path.of("target/w7d2-triage-report.md");
        Files.createDirectories(output.getParent());
        Files.writeString(output, markdown, StandardCharsets.UTF_8);

        assertThat(report.cause()).contains("Inventory dependency returned HTTP 503");
        assertThat(report.confidence()).isEqualTo("high");
        assertThat(report.toolTrace()).containsExactly(
            "read_allure_results - Confirm the failed scenario and its symptom",
            "read_logs - Check whether application logs confirm the dependency failure",
            "read_trace - Correlate the checkout and inventory spans",
            "rerun_scenario - Test the outage hypothesis with one staging recovery rerun"
        );
        assertThat(report.stopReason()).startsWith("PLANNER_DONE");
        assertThat(report.humanApprovalRequired()).isTrue();
        assertThat(report.fixApplied()).isFalse();
        assertThat(reruns).hasValue(1);
        assertThat(tools).allSatisfy(tool -> {
            assertThat(tool.description()).isNotBlank();
            assertThat(tool.inputSchema()).contains("required");
        });
        assertThat(markdown)
            .contains("<redacted-token>", "<redacted-email>", "<redacted-id>", "<redacted-secret>")
            .doesNotContain(
                "training-secret-123",
                "alice@shopkart.test",
                "customerId\": 501",
                "demo-api-key-456",
                "demo-password-789",
                "demo-client-secret-012"
            );
    }

    @Test
    void rerunToolRejectsProductionAndUnknownScenarios() {
        StagingRerunTool rerun = new StagingRerunTool(Set.of("coupon-checkout"), scenario -> "PASSED");

        assertThatThrownBy(() -> rerun.execute(new ToolRequest("run-142", "coupon-checkout", "production")))
            .isInstanceOf(SecurityException.class)
            .hasMessageContaining("restricted to staging");
        assertThatThrownBy(() -> rerun.execute(new ToolRequest("run-142", "full-regression", "staging")))
            .isInstanceOf(SecurityException.class)
            .hasMessageContaining("not allowlisted");
    }

    @Test
    void iterationCapStopsAPlannerThatNeverFinishes() {
        AtomicInteger calls = new AtomicInteger();
        TriageTool logs = countingTool("read_logs", calls);
        TriagePlanner loopingPlanner = new TriagePlanner() {
            @Override
            public TriageStep nextStep(List<ToolObservation> evidence) {
                return TriageStep.call("read_logs", "keep investigating");
            }

            @Override
            public TriageReport report(List<ToolObservation> evidence, String stopReason) {
                return minimalReport(evidence, stopReason);
            }
        };
        BoundedTriageAgent agent = new BoundedTriageAgent(
            List.of(logs),
            loopingPlanner,
            2,
            Duration.ofSeconds(60)
        );

        TriageReport report = agent.investigate(FAILED_RUN);

        assertThat(calls).hasValue(2);
        assertThat(report.stopReason()).isEqualTo("MAX_ITERATIONS_REACHED");
    }

    @Test
    void timeBudgetStopsTheLoopBeforeAnotherToolCall() {
        MutableClock clock = new MutableClock();
        AtomicInteger calls = new AtomicInteger();
        TriageTool logs = new TriageTool() {
            @Override
            public String name() {
                return "read_logs";
            }

            @Override
            public String description() {
                return "advances the test clock after one read";
            }

            @Override
            public ToolObservation execute(ToolRequest request) {
                calls.incrementAndGet();
                clock.advance(Duration.ofSeconds(2));
                return new ToolObservation(name(), "memory", "run-142 evidence");
            }
        };
        TriagePlanner loopingPlanner = new TriagePlanner() {
            @Override
            public TriageStep nextStep(List<ToolObservation> evidence) {
                return TriageStep.call("read_logs", "continue");
            }

            @Override
            public TriageReport report(List<ToolObservation> evidence, String stopReason) {
                return minimalReport(evidence, stopReason);
            }
        };
        BoundedTriageAgent agent = new BoundedTriageAgent(
            List.of(logs),
            loopingPlanner,
            6,
            Duration.ofSeconds(1),
            clock
        );

        TriageReport report = agent.investigate(FAILED_RUN);

        assertThat(calls).hasValue(1);
        assertThat(report.stopReason()).isEqualTo("TIME_BUDGET_REACHED");
    }

    @Test
    void timeBudgetCancelsAToolThatDoesNotReturn() throws InterruptedException {
        CountDownLatch interrupted = new CountDownLatch(1);
        TriageTool hangingLogs = new TriageTool() {
            @Override
            public String name() {
                return "read_logs";
            }

            @Override
            public String description() {
                return "simulates a stalled artifact reader";
            }

            @Override
            public ToolObservation execute(ToolRequest request) {
                try {
                    Thread.sleep(5_000);
                } catch (InterruptedException exception) {
                    interrupted.countDown();
                    Thread.currentThread().interrupt();
                }
                return new ToolObservation(name(), "memory", "late observation");
            }
        };
        TriagePlanner planner = new TriagePlanner() {
            @Override
            public TriageStep nextStep(List<ToolObservation> evidence) {
                return TriageStep.call("read_logs", "read the stalled log source");
            }

            @Override
            public TriageReport report(List<ToolObservation> evidence, String stopReason) {
                return minimalReport(evidence, stopReason);
            }
        };
        BoundedTriageAgent agent = new BoundedTriageAgent(
            List.of(hangingLogs),
            planner,
            6,
            Duration.ofMillis(100)
        );

        TriageReport report = agent.investigate(FAILED_RUN);

        assertThat(report.stopReason()).isEqualTo("TIME_BUDGET_REACHED_DURING_TOOL: read_logs");
        assertThat(report.evidence()).isEmpty();
        assertThat(interrupted.await(1, TimeUnit.SECONDS)).isTrue();
    }

    @Test
    void writeOrDeployToolsCannotEnterTheRegistry() {
        TriageTool deploy = countingTool("deploy_to_prod", new AtomicInteger());

        assertThatThrownBy(() -> new BoundedTriageAgent(
            List.of(deploy),
            new OfflineInventoryOutagePlanner(),
            6,
            Duration.ofSeconds(60)
        ))
            .isInstanceOf(SecurityException.class)
            .hasMessageContaining("outside the triage boundary");
    }

    private static List<TriageTool> tools(AtomicInteger reruns) {
        return List.of(
            new FileArtifactTool(
                "read_allure_results",
                "Read the failed run summary",
                ARTIFACTS.resolve("allure-summary.json")
            ),
            new FileArtifactTool(
                "read_logs",
                "Read application logs for the run id",
                ARTIFACTS.resolve("application.log")
            ),
            new FileArtifactTool(
                "read_trace",
                "Read one distributed trace",
                ARTIFACTS.resolve("trace.json")
            ),
            new StagingRerunTool(Set.of("coupon-checkout"), scenario -> {
                reruns.incrementAndGet();
                return "scenario=" + scenario + " result=PASSED_AFTER_RECOVERY environment=staging";
            })
        );
    }

    private static TriageTool countingTool(String name, AtomicInteger calls) {
        return new TriageTool() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public String description() {
                return "test tool";
            }

            @Override
            public ToolObservation execute(ToolRequest request) {
                calls.incrementAndGet();
                return new ToolObservation(name, "memory", "observation");
            }
        };
    }

    private static TriageReport minimalReport(List<ToolObservation> evidence, String stopReason) {
        return new TriageReport(
            "test symptom",
            "not enough evidence",
            evidence.stream().map(ToolObservation::evidenceRef).toList(),
            "human investigates",
            "low",
            evidence.stream().map(ToolObservation::tool).toList(),
            stopReason,
            true,
            false
        );
    }

    private static final class MutableClock extends Clock {
        private Instant now = Instant.parse("2026-07-14T00:00:00Z");

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return now;
        }

        void advance(Duration duration) {
            now = now.plus(duration);
        }
    }
}
