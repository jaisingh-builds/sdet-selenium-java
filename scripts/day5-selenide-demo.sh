#!/usr/bin/env bash
set -euo pipefail

echo "== Week 4 Day 5: Selenide demo =="
echo "1) Compile the framework"
mvn clean test-compile

echo
echo "2) Run the Selenide catalog suite headless"
mvn -Dtest=SelenideCatalogTest -Dheadless=true test

echo
echo "3) Optional failure demo command"
echo "mvn -Dtest=SelenideFailureDemo -Dheadless=true test"
echo "Expected: failure with Selenide evidence under target/selenide-reports"
