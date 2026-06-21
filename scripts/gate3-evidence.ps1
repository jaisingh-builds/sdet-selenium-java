$ErrorActionPreference = "Stop"

Set-Location (Join-Path $PSScriptRoot "..")

$lockDir = ".\.gate3-evidence.lock"
try {
    New-Item $lockDir -ItemType Directory -ErrorAction Stop | Out-Null
} catch {
    throw "Another Gate 3 evidence run is active."
}

try {
    Remove-Item .\evidence -Recurse -Force -ErrorAction SilentlyContinue
    New-Item .\evidence\negative -ItemType Directory -Force | Out-Null
    New-Item .\evidence\final-clean -ItemType Directory -Force | Out-Null

    Write-Host "Gate 3: capturing the expected negative run"
    & mvn -o clean "-Dtest=RunGate3Test" "-Dheadless=true" `
      "-Dcucumber.filter.tags=@negative" test 2>&1 `
      | Tee-Object .\evidence\negative\console.log
    $negativeExit = $LASTEXITCODE

    if ($negativeExit -eq 0) {
        throw "@negative unexpectedly passed."
    }

    Remove-Item .\evidence\negative\allure-results -Recurse -Force -ErrorAction SilentlyContinue
    Remove-Item .\evidence\negative\SparkReport -Recurse -Force -ErrorAction SilentlyContinue
    Copy-Item .\target\allure-results .\evidence\negative\allure-results -Recurse
    Copy-Item .\test-output\SparkReport .\evidence\negative\SparkReport -Recurse
    & allure generate .\target\allure-results --clean `
      -o .\evidence\negative\allure-report
    if ($LASTEXITCODE -ne 0) { throw "Negative Allure report generation failed." }

    $negativeResults = @(Get-ChildItem .\evidence\negative\allure-results -Filter *-result.json).Count
    $negativeSummary = Get-Content .\evidence\negative\allure-report\widgets\summary.json -Raw
    $negativeSpark = Get-Content .\evidence\negative\SparkReport\Spark.html -Raw
    if ($negativeResults -ne 1 -or $negativeSummary -notmatch '"failed":1.*"total":1' `
      -or $negativeSpark -notmatch 'data:image/png;base64' -or $negativeSpark -notmatch 'failChild: 1') {
        throw "Negative evidence is incomplete or contaminated."
    }

    Write-Host "Gate 3: running the green suite three times"
    foreach ($run in 1..3) {
        & mvn -o clean "-Dtest=RunGate3Test" "-Dheadless=true" `
          "-Dcucumber.filter.tags=@smoke or @regression" test 2>&1 `
          | Tee-Object ".\evidence\final-clean\stability-run-$run.log"

        if ($LASTEXITCODE -ne 0) {
            throw "Green stability run $run failed."
        }
    }

    Remove-Item .\evidence\final-clean\allure-results -Recurse -Force -ErrorAction SilentlyContinue
    Remove-Item .\evidence\final-clean\SparkReport -Recurse -Force -ErrorAction SilentlyContinue
    Copy-Item .\target\allure-results .\evidence\final-clean\allure-results -Recurse
    Copy-Item .\test-output\SparkReport .\evidence\final-clean\SparkReport -Recurse
    & allure generate .\target\allure-results --clean `
      -o .\evidence\final-clean\allure-report
    if ($LASTEXITCODE -ne 0) { throw "Final Allure report generation failed." }

    $greenResults = @(Get-ChildItem .\evidence\final-clean\allure-results -Filter *-result.json).Count
    $greenSummary = Get-Content .\evidence\final-clean\allure-report\widgets\summary.json -Raw
    $greenSpark = Get-Content .\evidence\final-clean\SparkReport\Spark.html -Raw
    if ($greenResults -ne 6 -or $greenSummary -notmatch '"failed":0.*"passed":6.*"total":6' `
      -or $greenSpark -notmatch 'passChild: 6') {
        throw "Final-clean evidence is incomplete or contaminated."
    }

    Write-Host "Gate 3 evidence is ready under evidence/."
} finally {
    Remove-Item $lockDir -Recurse -Force -ErrorAction SilentlyContinue
}
