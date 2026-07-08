#!/usr/bin/env bash
set -euo pipefail

BASE_URL_VALUE="${BASE_URL:-http://localhost:5173}"
HEADLESS_VALUE="${HEADLESS:-true}"

echo "=== W6D2: Maven baseline ==="
/usr/bin/time -p mvn -q clean -Dtest=W6D1RefactoringStructureTest test

echo
echo "=== W6D2: Gradle compile and dependency graph ==="
./gradlew --version
./gradlew clean testClasses
./gradlew build
./gradlew dependencies --configuration testRuntimeClasspath > build/w6d2-test-runtime-dependencies.txt
echo "Dependency graph saved to build/w6d2-test-runtime-dependencies.txt"

echo
echo "=== W6D2: Gradle safe structure test ==="
/usr/bin/time -p ./gradlew w6d1StructureTest -Pheadless="${HEADLESS_VALUE}" -PbaseUrl="${BASE_URL_VALUE}"

echo
echo "=== W6D2: Gradle parallel fork demo ==="
./gradlew parallelStructureTest -Pheadless="${HEADLESS_VALUE}" -PbaseUrl="${BASE_URL_VALUE}"

echo
echo "Optional browser proof after app is running:"
echo "./gradlew w6d1CheckoutTest -Pheadless=${HEADLESS_VALUE} -PbaseUrl=${BASE_URL_VALUE}"
echo
echo "Optional build scan:"
echo "./gradlew w6d1StructureTest --scan"
