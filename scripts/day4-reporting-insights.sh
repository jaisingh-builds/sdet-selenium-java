#!/usr/bin/env bash
set -euo pipefail

REPORT_RUNS="${REPORT_RUNS:-3}"
BUILD_LABEL="${BUILD_LABEL:-w6d4-local}"
REPORT_FAILURE_DEMO="${REPORT_FAILURE_DEMO:-false}"
RESULTS_DIR="target/allure-results"
REPORT_DIR="target/allure-report"
HISTORY_CACHE="target/allure-history-cache"

run_reporting_test() {
  rm -rf "${RESULTS_DIR}"
  mkdir -p "${RESULTS_DIR}"

  mvn -q -Dtest=W6D4ReportingInsightsTest -Dbuild.label="${BUILD_LABEL}" test
  if [[ "${REPORT_FAILURE_DEMO}" == "true" ]]; then
    echo "Adding intentional failures so Allure categories are visible."
    mvn -q -Dtest=W6D4IntentionalFailureDemo -Dbuild.label="${BUILD_LABEL}" test \
      > target/w6d4-intentional-failures.log 2>&1 || true
    echo "Expected failure details saved to target/w6d4-intentional-failures.log"
  fi

  cp src/test/resources/allure/categories.json "${RESULTS_DIR}/categories.json"
  if [[ -d "${HISTORY_CACHE}" ]]; then
    mkdir -p "${RESULTS_DIR}/history"
    cp -R "${HISTORY_CACHE}/." "${RESULTS_DIR}/history/"
  fi

  cat > "${RESULTS_DIR}/executor.json" <<JSON
{
  "name": "Local Trainer Run",
  "type": "local",
  "buildName": "${BUILD_LABEL}",
  "reportName": "Week 6 Day 4 Reporting Insights"
}
JSON
}

echo "=== W6D4: Reporting metadata checks ==="
./gradlew w6d4ReportingInsightsTest

if ! command -v allure >/dev/null 2>&1; then
  echo
  echo "Allure CLI is not installed. Results will be generated but HTML report creation is skipped."
  echo "Install Allure CLI to run: allure generate target/allure-results -o target/allure-report --clean"
  run_reporting_test
  exit 0
fi

echo
echo "=== W6D4: Generate Allure report with carried history ==="
rm -rf "${REPORT_DIR}" "${HISTORY_CACHE}"
for run in $(seq 1 "${REPORT_RUNS}"); do
  echo "--- Report run ${run}/${REPORT_RUNS} ---"
  run_reporting_test
  allure generate "${RESULTS_DIR}" -o "${REPORT_DIR}" --clean
  rm -rf "${HISTORY_CACHE}"
  if [[ -d "${REPORT_DIR}/history" ]]; then
    mkdir -p "${HISTORY_CACHE}"
    cp -R "${REPORT_DIR}/history/." "${HISTORY_CACHE}/"
  fi
done

echo
echo "Report generated at ${REPORT_DIR}/index.html"
echo "Open locally: allure open ${REPORT_DIR}"
echo "Optional browser/Cucumber report after app is running:"
echo "mvn -q -Dtest=RunCucumberTest -Dheadless=true test && allure serve target/allure-results"
echo "Optional category demo with product/test red buckets and a quarantined flaky signal:"
echo "REPORT_FAILURE_DEMO=true ./scripts/day4-reporting-insights.sh"
