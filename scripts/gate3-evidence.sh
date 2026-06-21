#!/usr/bin/env bash
set -uo pipefail

cd "$(dirname "$0")/.."

lock_dir=".gate3-evidence.lock"
if ! mkdir "$lock_dir" 2>/dev/null; then
  echo "ERROR: another Gate 3 evidence run is active."
  exit 1
fi
trap 'rmdir "$lock_dir" 2>/dev/null || true' EXIT INT TERM

rm -rf evidence
mkdir -p evidence/negative evidence/final-clean

echo "Gate 3: capturing the expected negative run"
mvn -o clean -Dtest=RunGate3Test -Dheadless=true \
  -Dcucumber.filter.tags="@negative" test 2>&1 \
  | tee evidence/negative/console.log
negative_exit=${PIPESTATUS[0]}

if [[ $negative_exit -eq 0 ]]; then
  echo "ERROR: @negative unexpectedly passed."
  exit 1
fi

rm -rf evidence/negative/allure-results evidence/negative/SparkReport
cp -R target/allure-results evidence/negative/allure-results
cp -R test-output/SparkReport evidence/negative/SparkReport
allure generate target/allure-results --clean \
  -o evidence/negative/allure-report || exit 1

negative_results=$(find evidence/negative/allure-results -name '*-result.json' | wc -l | tr -d ' ')
if [[ "$negative_results" != "1" ]] \
  || ! grep -q '"failed":1.*"total":1' evidence/negative/allure-report/widgets/summary.json \
  || ! grep -q 'data:image/png;base64' evidence/negative/SparkReport/Spark.html \
  || ! grep -q 'failChild: 1' evidence/negative/SparkReport/Spark.html; then
  echo "ERROR: negative evidence is incomplete or contaminated."
  exit 1
fi

echo "Gate 3: running the green suite three times"
for run in 1 2 3; do
  mvn -o clean -Dtest=RunGate3Test -Dheadless=true \
    -Dcucumber.filter.tags="@smoke or @regression" test 2>&1 \
    | tee "evidence/final-clean/stability-run-${run}.log"
  green_exit=${PIPESTATUS[0]}

  if [[ $green_exit -ne 0 ]]; then
    echo "ERROR: green stability run ${run} failed."
    exit "$green_exit"
  fi
done

rm -rf evidence/final-clean/allure-results evidence/final-clean/SparkReport
cp -R target/allure-results evidence/final-clean/allure-results
cp -R test-output/SparkReport evidence/final-clean/SparkReport
allure generate target/allure-results --clean \
  -o evidence/final-clean/allure-report || exit 1

green_results=$(find evidence/final-clean/allure-results -name '*-result.json' | wc -l | tr -d ' ')
if [[ "$green_results" != "6" ]] \
  || ! grep -q '"failed":0.*"passed":6.*"total":6' evidence/final-clean/allure-report/widgets/summary.json \
  || ! grep -q 'passChild: 6' evidence/final-clean/SparkReport/Spark.html; then
  echo "ERROR: final-clean evidence is incomplete or contaminated."
  exit 1
fi

echo "Gate 3 evidence is ready under evidence/."
