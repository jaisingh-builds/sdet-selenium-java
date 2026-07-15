#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$REPO_ROOT"

echo "=== W7D2 M1-M3: baseline, outage, degradation and recovery ==="
mvn -q -Dtest=W7D2ResilienceTest test

echo
echo "=== W7D2 M4-M5: bounded artifact triage and human gate ==="
mvn -q -Dtest=W7D2AgenticTriageTest test

echo
echo "=== Resilience evidence ==="
cat target/w7d2-resilience-evidence.md

echo
echo "=== Agentic triage report ==="
cat target/w7d2-triage-report.md

echo
echo "Expected: 12 tests passed, no fix applied, human approval required."
echo "Gradle equivalent: ./gradlew w7d2Day"
