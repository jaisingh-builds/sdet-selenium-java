package com.ust.sdet.w7d2.triage;

public record ToolObservation(String tool, String evidenceRef, String content, String reason) {
    public ToolObservation(String tool, String evidenceRef, String content) {
        this(tool, evidenceRef, content, "");
    }

    public ToolObservation withReason(String planningReason) {
        return new ToolObservation(tool, evidenceRef, content, planningReason);
    }
}
