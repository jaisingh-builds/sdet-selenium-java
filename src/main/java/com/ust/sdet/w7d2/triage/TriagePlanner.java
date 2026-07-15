package com.ust.sdet.w7d2.triage;

import java.util.List;

public interface TriagePlanner {
    TriageStep nextStep(List<ToolObservation> evidence);

    TriageReport report(List<ToolObservation> evidence, String stopReason);
}
