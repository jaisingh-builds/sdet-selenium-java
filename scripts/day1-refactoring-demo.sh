#!/usr/bin/env bash
set -euo pipefail

echo "=== W6D1: no-browser structure checks ==="
mvn clean -Dtest=W6D1RefactoringStructureTest test

echo
echo "=== W6D1: optional browser checkout flow ==="
echo "Start the retail app first, then either run this command:"
echo "mvn -Dtest=W6D1RefactoredCheckoutFlowTest -Dheadless=true -DbaseUrl=\${BASE_URL:-http://localhost:5173} test"

if [[ "${RUN_BROWSER:-false}" == "true" ]]; then
  mvn -Dtest=W6D1RefactoredCheckoutFlowTest \
    -Dheadless="${HEADLESS:-true}" \
    -DbaseUrl="${BASE_URL:-http://localhost:5173}" \
    test
fi
