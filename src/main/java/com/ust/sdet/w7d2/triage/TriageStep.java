package com.ust.sdet.w7d2.triage;

public record TriageStep(String tool, boolean done, String reason) {
    public static TriageStep call(String tool, String reason) {
        return new TriageStep(tool, false, reason);
    }

    public static TriageStep done(String reason) {
        return new TriageStep(null, true, reason);
    }
}
