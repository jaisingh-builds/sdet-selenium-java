param(
    [switch]$Headed
)

$ErrorActionPreference = "Stop"
$repoDir = Split-Path -Parent $PSScriptRoot
$headless = if ($Headed) { "false" } else { "true" }
$previousSecret = $env:TRIPSTACK_DEMO_PASSWORD
$demoSecret = if ($previousSecret) { $previousSecret } else { "XPath-Classroom-Only-7x!" }

Push-Location $repoDir
try {
    Write-Host "== XPath in Practice: deterministic TripStack demo =="
    Write-Host "1) Compile all demo files"
    mvn -q -DskipTests test-compile
    if ($LASTEXITCODE -ne 0) { throw "Compilation failed" }

    Write-Host "2) Run four XPath widget and locator tests"
    mvn "-Dtest=XPathInPracticeTest" "-Dheadless=$headless" test
    if ($LASTEXITCODE -ne 0) { throw "XPath browser tests failed" }

    Write-Host "3) Run the runtime-secret demo"
    $env:TRIPSTACK_DEMO_PASSWORD = $demoSecret
    mvn "-Dtest=XPathSecretSafetyTest" "-Dheadless=$headless" test
    if ($LASTEXITCODE -ne 0) { throw "Secret-safety test failed" }

    Write-Host "4) Confirm the secret is absent from generated artifacts"
    $leak = Get-ChildItem target -Recurse -File -ErrorAction SilentlyContinue |
        Select-String -SimpleMatch $demoSecret -List -ErrorAction SilentlyContinue
    if ($leak) { throw "The runtime secret was found under target/" }

    Write-Host "PASS: 5 tests passed and no runtime secret was found in target/."
}
finally {
    if ($null -eq $previousSecret) {
        Remove-Item Env:TRIPSTACK_DEMO_PASSWORD -ErrorAction SilentlyContinue
    }
    else {
        $env:TRIPSTACK_DEMO_PASSWORD = $previousSecret
    }
    Pop-Location
}
