param(
    [string]$BaseUrl = "http://localhost:5173",
    [string]$Headless = "true",
    [switch]$Browser
)

$ErrorActionPreference = "Stop"

Write-Host "=== W6D2: Maven baseline ==="
mvn -q clean -Dtest=W6D1RefactoringStructureTest test

Write-Host ""
Write-Host "=== W6D2: Gradle compile and dependency graph ==="
.\gradlew.bat --version
.\gradlew.bat clean testClasses
.\gradlew.bat build
.\gradlew.bat dependencies --configuration testRuntimeClasspath | Out-File -Encoding utf8 build\w6d2-test-runtime-dependencies.txt
Write-Host "Dependency graph saved to build\w6d2-test-runtime-dependencies.txt"

Write-Host ""
Write-Host "=== W6D2: Gradle safe structure test ==="
.\gradlew.bat w6d1StructureTest "-Pheadless=$Headless" "-PbaseUrl=$BaseUrl"

Write-Host ""
Write-Host "=== W6D2: Gradle parallel fork demo ==="
.\gradlew.bat parallelStructureTest "-Pheadless=$Headless" "-PbaseUrl=$BaseUrl"

if ($Browser) {
    Write-Host ""
    Write-Host "=== W6D2: Optional browser checkout proof ==="
    .\gradlew.bat w6d1CheckoutTest "-Pheadless=$Headless" "-PbaseUrl=$BaseUrl"
}

Write-Host ""
Write-Host "Optional build scan:"
Write-Host ".\gradlew.bat w6d1StructureTest --scan"
