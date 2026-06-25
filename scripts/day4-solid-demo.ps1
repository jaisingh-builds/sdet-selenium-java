$ErrorActionPreference = "Stop"

Write-Host "== Week 4 Day 4: compile framework =="
mvn clean test-compile

Write-Host "== Week 4 Day 4: run SOLID structure checks =="
mvn -Dtest=Day4SolidStructureTest test

Write-Host "== Week 4 Day 4 demo complete =="
