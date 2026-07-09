#!/usr/bin/env bash
set -euo pipefail

echo "=== W6D3: No-Docker builder checks ==="
mvn -q -Dtest=W6D3TestDataBuilderStructureTest test

echo
echo "=== W6D3: Gradle builder checks ==="
./gradlew w6d3BuilderStructureTest

echo
if [[ "${RUN_DOCKER:-false}" == "true" ]]; then
  echo "=== W6D3: Optional Testcontainers/Flyway isolated Postgres ==="
  echo "Docker run requested through RUN_DOCKER=true."
  mvn -q -Dtest=OrdersDataIT test
  ./gradlew w6d3DataStrategyTest
else
  echo "=== W6D3: Optional Testcontainers/Flyway isolated Postgres ==="
  echo "Skipped by default for participant machines without Docker."
  echo "Trainer command: RUN_DOCKER=true ./scripts/day3-test-data-demo.sh"
fi

echo
echo "Evidence to show:"
echo "- Builder test states only the changed fields."
echo "- Flyway applies V1 schema and V2 reference data when RUN_DOCKER=true."
echo "- @BeforeEach reset keeps counts independent in the container proof."
