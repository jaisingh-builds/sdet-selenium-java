package com.ust.sdet.w7d2.triage;

public interface TriageTool {
    String name();

    String description();

    default String inputSchema() {
        return """
            {"type":"object","properties":{
              "runId":{"type":"string"},
              "scenario":{"type":"string"},
              "environment":{"type":"string"}
            },"required":["runId"]}
            """;
    }

    ToolObservation execute(ToolRequest request);
}
