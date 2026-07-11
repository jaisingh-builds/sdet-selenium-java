$ReportRuns = if ($env:REPORT_RUNS) { [int]$env:REPORT_RUNS } else { 3 }
$BuildLabel = if ($env:BUILD_LABEL) { $env:BUILD_LABEL } else { "w6d4-local" }
$ReportFailureDemo = $env:REPORT_FAILURE_DEMO -eq "true"
$ResultsDir = "target\allure-results"
$ReportDir = "target\allure-report"
$HistoryCache = "target\allure-history-cache"

function Invoke-ReportingTest {
    Remove-Item -Recurse -Force $ResultsDir -ErrorAction SilentlyContinue
    New-Item -ItemType Directory -Force $ResultsDir | Out-Null

    mvn -q -Dtest=W6D4ReportingInsightsTest "-Dbuild.label=$BuildLabel" test
    if ($ReportFailureDemo) {
        Write-Host "Adding intentional failures so Allure categories are visible."
        mvn -q -Dtest=W6D4IntentionalFailureDemo "-Dbuild.label=$BuildLabel" test *> "target\w6d4-intentional-failures.log"
        Write-Host "Expected failure details saved to target\w6d4-intentional-failures.log"
    }

    Copy-Item "src\test\resources\allure\categories.json" "$ResultsDir\categories.json" -Force

    if (Test-Path $HistoryCache) {
        New-Item -ItemType Directory -Force "$ResultsDir\history" | Out-Null
        Copy-Item "$HistoryCache\*" "$ResultsDir\history" -Recurse -Force
    }

    @"
{
  "name": "Local Trainer Run",
  "type": "local",
  "buildName": "$BuildLabel",
  "reportName": "Week 6 Day 4 Reporting Insights"
}
"@ | Out-File -Encoding utf8 "$ResultsDir\executor.json"
}

Write-Host "=== W6D4: Reporting metadata checks ==="
.\gradlew.bat w6d4ReportingInsightsTest

if (-not (Get-Command allure -ErrorAction SilentlyContinue)) {
    Write-Host ""
    Write-Host "Allure CLI is not installed. Results will be generated but HTML report creation is skipped."
    Write-Host "Install Allure CLI to run: allure generate target/allure-results -o target/allure-report --clean"
    Invoke-ReportingTest
    exit 0
}

Write-Host ""
Write-Host "=== W6D4: Generate Allure report with carried history ==="
Remove-Item -Recurse -Force $ReportDir, $HistoryCache -ErrorAction SilentlyContinue

foreach ($run in 1..$ReportRuns) {
    Write-Host "--- Report run $run/$ReportRuns ---"
    Invoke-ReportingTest
    allure generate $ResultsDir -o $ReportDir --clean
    Remove-Item -Recurse -Force $HistoryCache -ErrorAction SilentlyContinue
    if (Test-Path "$ReportDir\history") {
        New-Item -ItemType Directory -Force $HistoryCache | Out-Null
        Copy-Item "$ReportDir\history\*" $HistoryCache -Recurse -Force
    }
}

Write-Host ""
Write-Host "Report generated at $ReportDir\index.html"
Write-Host "Open locally: allure open $ReportDir"
Write-Host "Optional browser/Cucumber report after app is running:"
Write-Host "mvn -q -Dtest=RunCucumberTest -Dheadless=true test; allure serve target/allure-results"
Write-Host "Optional category demo with product/test red buckets and a quarantined flaky signal:"
Write-Host "`$env:REPORT_FAILURE_DEMO='true'; .\scripts\day4-reporting-insights.ps1"
