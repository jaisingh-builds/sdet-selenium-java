package com.ust.sdet.w7d2.triage;

import java.util.List;

public record TriageReport(
        String symptom,
        String cause,
        List<String> evidence,
        String candidateFix,
        String confidence,
        List<String> toolTrace,
        String stopReason,
        boolean humanApprovalRequired,
        boolean fixApplied
) {
    public String toMarkdown() {
        return """
            # W7D2 Triage Report

            - Symptom: %s
            - Cause: %s
            - Candidate fix: %s
            - Confidence: %s
            - Stop reason: %s
            - Human approval required: %s
            - Fix applied: %s

            ## Evidence

            %s

            ## Tool Trace

            %s
            """.formatted(
                symptom,
                cause,
                candidateFix,
                confidence,
                stopReason,
                humanApprovalRequired,
                fixApplied,
                bulletList(evidence),
                bulletList(toolTrace)
            );
    }

    private static String bulletList(List<String> values) {
        return values.stream().map(value -> "- " + value).reduce((left, right) -> left + "\n" + right).orElse("- none");
    }
}
