param(
    [switch]$Browser,
    [string]$BaseUrl = "http://localhost:5173",
    [string]$Headless = "true"
)

$ErrorActionPreference = "Stop"

Write-Host "=== W6D1: no-browser structure checks ==="
mvn clean -Dtest=W6D1RefactoringStructureTest test

Write-Host ""
Write-Host "=== W6D1: optional browser checkout flow ==="
Write-Host "Start the retail app first, then either run:"
Write-Host "mvn -Dtest=W6D1RefactoredCheckoutFlowTest -Dheadless=$Headless -DbaseUrl=$BaseUrl test"

if ($Browser) {
    mvn "-Dtest=W6D1RefactoredCheckoutFlowTest" "-Dheadless=$Headless" "-DbaseUrl=$BaseUrl" test
}
