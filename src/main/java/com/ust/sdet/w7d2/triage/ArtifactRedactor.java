package com.ust.sdet.w7d2.triage;

import java.util.regex.Pattern;

public final class ArtifactRedactor {
    private static final Pattern BEARER_TOKEN = Pattern.compile("(?i)Bearer\\s+[A-Za-z0-9._-]+");
    private static final Pattern EMAIL = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
    private static final Pattern CUSTOMER_ID = Pattern.compile(
        "(?i)(customer[_-]?id[\\\"'=:\\s]+)[A-Za-z0-9_-]+"
    );
    private static final Pattern SENSITIVE_JSON_FIELD = Pattern.compile(
        "(?i)(\"(?:password|api[_-]?key|client[_-]?secret|access[_-]?token|refresh[_-]?token|credential)\"\\s*:\\s*\")[^\"]*(\")"
    );
    private static final Pattern SENSITIVE_ASSIGNMENT = Pattern.compile(
        "(?i)((?:password|api[_-]?key|client[_-]?secret|access[_-]?token|refresh[_-]?token|credential)\\s*=\\s*)[^\\s,;]+"
    );

    private ArtifactRedactor() {
    }

    public static String redact(String source) {
        String withoutTokens = BEARER_TOKEN.matcher(source).replaceAll("Bearer <redacted-token>");
        String withoutJsonSecrets = SENSITIVE_JSON_FIELD.matcher(withoutTokens)
            .replaceAll("$1<redacted-secret>$2");
        String withoutAssignedSecrets = SENSITIVE_ASSIGNMENT.matcher(withoutJsonSecrets)
            .replaceAll("$1<redacted-secret>");
        String withoutEmails = EMAIL.matcher(withoutAssignedSecrets).replaceAll("<redacted-email>");
        return CUSTOMER_ID.matcher(withoutEmails).replaceAll("$1<redacted-id>");
    }
}
