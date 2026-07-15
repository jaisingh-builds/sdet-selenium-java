package com.ust.sdet.w7d2.triage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileArtifactTool implements TriageTool {
    private final String name;
    private final String description;
    private final Path artifact;

    public FileArtifactTool(String name, String description, Path artifact) {
        this.name = name;
        this.description = description;
        this.artifact = artifact;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public ToolObservation execute(ToolRequest request) {
        try {
            String content = Files.readString(artifact, StandardCharsets.UTF_8);
            if (!content.contains(request.runId())) {
                throw new SecurityException("Artifact does not belong to requested run " + request.runId());
            }
            return new ToolObservation(name, artifact.getFileName().toString(), ArtifactRedactor.redact(content));
        } catch (IOException error) {
            throw new IllegalStateException("Could not read artifact " + artifact, error);
        }
    }
}
