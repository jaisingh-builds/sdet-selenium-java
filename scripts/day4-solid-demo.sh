#!/usr/bin/env bash
set -euo pipefail

echo "== Week 4 Day 4: compile framework =="
mvn clean test-compile

echo "== Week 4 Day 4: run SOLID structure checks =="
mvn -Dtest=Day4SolidStructureTest test

echo "== Week 4 Day 4 demo complete =="
