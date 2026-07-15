package com.ust.sdet.w7d2.triage;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class BoundedTriageAgent {
    private static final Set<String> ALLOWED_TOOLS = Set.of(
        "read_allure_results",
        "read_logs",
        "read_trace",
        "rerun_scenario"
    );

    private final Map<String, TriageTool> tools;
    private final TriagePlanner planner;
    private final int maxIterations;
    private final Duration timeBudget;
    private final Clock clock;

    public BoundedTriageAgent(
            Collection<TriageTool> tools,
            TriagePlanner planner,
            int maxIterations,
            Duration timeBudget
    ) {
        this(tools, planner, maxIterations, timeBudget, Clock.systemUTC());
    }

    BoundedTriageAgent(
            Collection<TriageTool> tools,
            TriagePlanner planner,
            int maxIterations,
            Duration timeBudget,
            Clock clock
    ) {
        if (maxIterations < 1 || timeBudget.isNegative() || timeBudget.isZero()) {
            throw new IllegalArgumentException("Agent bounds must be positive");
        }
        this.tools = toolRegistry(tools);
        this.planner = planner;
        this.maxIterations = maxIterations;
        this.timeBudget = timeBudget;
        this.clock = clock;
    }

    public TriageReport investigate(ToolRequest request) {
        Instant started = clock.instant();
        List<ToolObservation> evidence = new ArrayList<>();

        for (int iteration = 0; iteration < maxIterations; iteration++) {
            if (Duration.between(started, clock.instant()).compareTo(timeBudget) >= 0) {
                return planner.report(evidence, "TIME_BUDGET_REACHED");
            }
            TriageStep step = planner.nextStep(List.copyOf(evidence));
            if (step.done()) {
                return planner.report(evidence, "PLANNER_DONE: " + step.reason());
            }
            TriageTool tool = tools.get(step.tool());
            if (tool == null) {
                throw new SecurityException("Planner requested a non-allowlisted tool: " + step.tool());
            }
            try {
                evidence.add(executeWithinBudget(tool, request, started).withReason(step.reason()));
            } catch (TimeBudgetExceededException ignored) {
                return planner.report(evidence, "TIME_BUDGET_REACHED_DURING_TOOL: " + tool.name());
            }
        }
        return planner.report(evidence, "MAX_ITERATIONS_REACHED");
    }

    private ToolObservation executeWithinBudget(TriageTool tool, ToolRequest request, Instant started) {
        Duration elapsed = Duration.between(started, clock.instant());
        Duration remaining = timeBudget.minus(elapsed);
        if (remaining.isNegative() || remaining.isZero()) {
            throw new TimeBudgetExceededException();
        }

        ExecutorService executor = Executors.newSingleThreadExecutor(task -> {
            Thread thread = new Thread(task, "w7d2-triage-tool");
            thread.setDaemon(true);
            return thread;
        });
        Future<ToolObservation> call = executor.submit(() -> tool.execute(request));
        try {
            return call.get(remaining.toNanos(), TimeUnit.NANOSECONDS);
        } catch (TimeoutException exception) {
            call.cancel(true);
            throw new TimeBudgetExceededException();
        } catch (InterruptedException exception) {
            call.cancel(true);
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Triage agent was interrupted", exception);
        } catch (ExecutionException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            if (cause instanceof Error error) {
                throw error;
            }
            throw new IllegalStateException("Triage tool failed", cause);
        } finally {
            executor.shutdownNow();
        }
    }

    private static Map<String, TriageTool> toolRegistry(Collection<TriageTool> tools) {
        Map<String, TriageTool> registry = new LinkedHashMap<>();
        for (TriageTool tool : tools) {
            if (!ALLOWED_TOOLS.contains(tool.name())) {
                throw new SecurityException("Tool is outside the triage boundary: " + tool.name());
            }
            if (registry.put(tool.name(), tool) != null) {
                throw new IllegalArgumentException("Duplicate triage tool: " + tool.name());
            }
        }
        return Map.copyOf(registry);
    }

    private static final class TimeBudgetExceededException extends RuntimeException {
    }
}
