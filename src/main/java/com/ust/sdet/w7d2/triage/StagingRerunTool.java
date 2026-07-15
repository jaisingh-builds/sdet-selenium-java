package com.ust.sdet.w7d2.triage;

import java.util.Set;

public class StagingRerunTool implements TriageTool {
    private final Set<String> allowedScenarios;
    private final ScenarioRerunner rerunner;

    public StagingRerunTool(Set<String> allowedScenarios, ScenarioRerunner rerunner) {
        this.allowedScenarios = Set.copyOf(allowedScenarios);
        this.rerunner = rerunner;
    }

    @Override
    public String name() {
        return "rerun_scenario";
    }

    @Override
    public String description() {
        return "Re-run one allowlisted scenario in staging only";
    }

    @Override
    public String inputSchema() {
        return """
            {"type":"object","properties":{
              "scenario":{"type":"string"},
              "environment":{"const":"staging"}
            },"required":["scenario","environment"]}
            """;
    }

    @Override
    public ToolObservation execute(ToolRequest request) {
        if (!"staging".equalsIgnoreCase(request.environment())) {
            throw new SecurityException("rerun_scenario is restricted to staging");
        }
        if (!allowedScenarios.contains(request.scenario())) {
            throw new SecurityException("Scenario is not allowlisted: " + request.scenario());
        }
        return new ToolObservation(name(), "staging-rerun:" + request.scenario(), rerunner.rerun(request.scenario()));
    }
}
