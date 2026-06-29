$ErrorActionPreference = "Stop"

Write-Host "== Week 4 Day 5: Selenide demo =="
Write-Host "1) Compile the framework"
mvn clean test-compile

Write-Host ""
Write-Host "2) Run the Selenide catalog suite headless"
mvn -Dtest=SelenideCatalogTest -Dheadless=true test

Write-Host ""
Write-Host "3) Optional failure demo command"
Write-Host "mvn -Dtest=SelenideFailureDemo -Dheadless=true test"
Write-Host "Expected: failure with Selenide evidence under target/selenide-reports"
