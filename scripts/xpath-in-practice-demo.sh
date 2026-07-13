#!/usr/bin/env bash
set -euo pipefail

REPO_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
HEADLESS="${HEADLESS:-true}"
DEMO_SECRET="${TRIPSTACK_DEMO_PASSWORD:-XPath-Classroom-Only-7x!}"

cd "$REPO_DIR"

echo "== XPath in Practice: deterministic TripStack demo =="
echo "1) Compile all demo files"
mvn -q -DskipTests test-compile

echo "2) Run four XPath widget and locator tests"
mvn -Dtest=XPathInPracticeTest -Dheadless="$HEADLESS" test

echo "3) Run the runtime-secret demo"
TRIPSTACK_DEMO_PASSWORD="$DEMO_SECRET" \
  mvn -Dtest=XPathSecretSafetyTest -Dheadless="$HEADLESS" test

echo "4) Confirm the secret is absent from generated artifacts"
if grep -RFl -- "$DEMO_SECRET" target >/dev/null 2>&1; then
  echo "ERROR: the runtime secret was found under target/"
  exit 1
fi

echo "PASS: 5 tests passed and no runtime secret was found in target/."
