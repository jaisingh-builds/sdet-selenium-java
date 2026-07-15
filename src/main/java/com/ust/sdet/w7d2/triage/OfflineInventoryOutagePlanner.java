package com.ust.sdet.w7d2.triage;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class OfflineInventoryOutagePlanner implements TriagePlanner {
    @Override
    public TriageStep nextStep(List<ToolObservation> evidence) {
        Set<String> usedTools = evidence.stream().map(ToolObservation::tool).collect(Collectors.toSet());
        String combined = evidence.stream().map(ToolObservation::content).reduce("", (left, right) -> left + "\n" + right);
        String normalized = combined.toLowerCase(Locale.ROOT);

        if (!usedTools.contains("read_allure_results")) {
            return TriageStep.call("read_allure_results", "Confirm the failed scenario and its symptom");
        }
        if (!usedTools.contains("read_logs")) {
            return TriageStep.call("read_logs", "Check whether application logs confirm the dependency failure");
        }
        boolean inventory503 = normalized.contains("inventory") && normalized.contains("503");
        if (inventory503 && !usedTools.contains("read_trace")) {
            return TriageStep.call("read_trace", "Correlate the checkout and inventory spans");
        }
        if (inventory503 && usedTools.contains("read_trace") && !usedTools.contains("rerun_scenario")) {
            return TriageStep.call("rerun_scenario", "Test the outage hypothesis with one staging recovery rerun");
        }
        if (usedTools.contains("rerun_scenario")) {
            return TriageStep.done("Enough linked evidence and a controlled recovery rerun are available");
        }
        return TriageStep.done("Available evidence does not justify further tool calls");
    }

    @Override
    public TriageReport report(List<ToolObservation> evidence, String stopReason) {
        String combined = evidence.stream().map(ToolObservation::content).reduce("", (left, right) -> left + "\n" + right);
        String normalized = combined.toLowerCase(Locale.ROOT);
        boolean inventory503 = normalized.contains("inventory") && normalized.contains("503");
        boolean recoveryPassed = combined.contains("PASSED_AFTER_RECOVERY");
        String cause = inventory503
            ? "Inventory dependency returned HTTP 503 during checkout"
            : "Insufficient evidence to identify one root cause";
        String confidence = inventory503 && recoveryPassed ? "high" : inventory503 ? "medium" : "low";
        String fix = inventory503
            ? "Keep inventory failures behind the circuit-breaker fallback and alert on sustained 503 responses"
            : "Collect more evidence; do not change the system yet";

        return new TriageReport(
            "Checkout failed while confirming inventory",
            cause,
            evidence.stream().map(item -> item.evidenceRef() + ": " + oneLine(item.content())).toList(),
            fix,
            confidence,
            evidence.stream().map(item -> item.tool() + " - " + item.reason()).toList(),
            stopReason,
            true,
            false
        );
    }

    private static String oneLine(String content) {
        return content.replaceAll("\\s+", " ").trim();
    }
}
