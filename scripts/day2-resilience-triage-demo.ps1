$ErrorActionPreference = "Stop"
$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
Set-Location $RepoRoot

function Invoke-Checked {
    param([scriptblock]$Command)
    & $Command
    if ($LASTEXITCODE -ne 0) {
        throw "Command failed with exit code $LASTEXITCODE"
    }
}

Write-Host "=== W7D2 M1-M3: baseline, outage, degradation and recovery ==="
Invoke-Checked { mvn -q -Dtest=W7D2ResilienceTest test }

Write-Host ""
Write-Host "=== W7D2 M4-M5: bounded artifact triage and human gate ==="
Invoke-Checked { mvn -q -Dtest=W7D2AgenticTriageTest test }

Write-Host ""
Write-Host "=== Resilience evidence ==="
Get-Content target/w7d2-resilience-evidence.md

Write-Host ""
Write-Host "=== Agentic triage report ==="
Get-Content target/w7d2-triage-report.md

Write-Host ""
Write-Host "Expected: 12 tests passed, no fix applied, human approval required."
Write-Host "Gradle equivalent: .\gradlew.bat w7d2Day"
