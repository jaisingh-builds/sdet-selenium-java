Write-Host "=== W6D3: No-Docker builder checks ==="
mvn -q -Dtest=W6D3TestDataBuilderStructureTest test

Write-Host ""
Write-Host "=== W6D3: Gradle builder checks ==="
.\gradlew.bat w6d3BuilderStructureTest

Write-Host ""
if ($env:RUN_DOCKER -eq "true") {
    Write-Host "=== W6D3: Optional Testcontainers/Flyway isolated Postgres ==="
    Write-Host "Docker run requested through RUN_DOCKER=true."
    mvn -q -Dtest=OrdersDataIT test
    .\gradlew.bat w6d3DataStrategyTest
} else {
    Write-Host "=== W6D3: Optional Testcontainers/Flyway isolated Postgres ==="
    Write-Host "Skipped by default for participant machines without Docker."
    Write-Host "Trainer command: `$env:RUN_DOCKER='true'; .\scripts\day3-test-data-demo.ps1"
}

Write-Host ""
Write-Host "Evidence to show:"
Write-Host "- Builder test states only the changed fields."
Write-Host "- Flyway applies V1 schema and V2 reference data when RUN_DOCKER=true."
Write-Host "- @BeforeEach reset keeps counts independent in the container proof."
