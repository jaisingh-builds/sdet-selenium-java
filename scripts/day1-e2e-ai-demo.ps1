param(
    [ValidateSet("container", "external")]
    [string]$Runtime = $(if ($env:W7D1_RUNTIME) { $env:W7D1_RUNTIME } else { "container" }),
    [string]$Headless = $(if ($env:HEADLESS) { $env:HEADLESS } else { "true" }),
    [string]$ShopKartRoot
)

$ErrorActionPreference = "Stop"
$SeleniumRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
if (-not $ShopKartRoot) {
    $ShopKartRoot = (Resolve-Path (Join-Path $SeleniumRoot "..\sdet-retail-app")).Path
}
Set-Location $SeleniumRoot

function Invoke-Checked {
    param([scriptblock]$Command)
    & $Command
    if ($LASTEXITCODE -ne 0) {
        throw "Command failed with exit code $LASTEXITCODE"
    }
}

Write-Host "=== W7D1: AI draft hardening checks ==="
Invoke-Checked { mvn -q -Dtest=W7D1AiHardeningReviewTest test }

if ($Runtime -eq "container") {
    Write-Host ""
    Write-Host "=== W7D1: Build ShopKart frontend for the isolated runtime ==="
    Invoke-Checked { npm run build --prefix (Join-Path $ShopKartRoot "frontend") }
}

Write-Host ""
Write-Host "=== W7D1: UI -> API -> database -> contract ==="
Invoke-Checked {
    mvn -Dtest=RunW7D1E2ETest `
        "-Dw7d1.runtime=$Runtime" `
        "-Dshopkart.root=$ShopKartRoot" `
        "-Dheadless=$Headless" `
        test
}

Write-Host ""
Write-Host "Expected: 2 tests, 0 failures, 0 errors, 0 skipped."
Write-Host "Allure evidence: target/allure-results"
Write-Host "ShopKart startup log: target/w7d1-shopkart.log"
