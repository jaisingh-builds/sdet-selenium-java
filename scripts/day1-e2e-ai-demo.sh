#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SELENIUM_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
SHOPKART_ROOT="${SHOPKART_ROOT:-$(cd "$SELENIUM_ROOT/../sdet-retail-app" && pwd)}"
RUNTIME="${W7D1_RUNTIME:-container}"
HEADLESS="${HEADLESS:-true}"

cd "$SELENIUM_ROOT"

echo "=== W7D1: AI draft hardening checks ==="
mvn -q -Dtest=W7D1AiHardeningReviewTest test

if [[ "$RUNTIME" == "container" ]]; then
  echo
  echo "=== W7D1: Build ShopKart frontend for the isolated runtime ==="
  npm run build --prefix "$SHOPKART_ROOT/frontend"
elif [[ "$RUNTIME" != "external" ]]; then
  echo "W7D1_RUNTIME must be container or external" >&2
  exit 2
fi

echo
echo "=== W7D1: UI -> API -> database -> contract ==="
mvn -Dtest=RunW7D1E2ETest \
  -Dw7d1.runtime="$RUNTIME" \
  -Dshopkart.root="$SHOPKART_ROOT" \
  -Dheadless="$HEADLESS" \
  test

echo
echo "Expected: 2 tests, 0 failures, 0 errors, 0 skipped."
echo "Allure evidence: target/allure-results"
echo "ShopKart startup log: target/w7d1-shopkart.log"
