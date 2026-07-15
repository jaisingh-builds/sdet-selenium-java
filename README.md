# SDET Selenium Java

Java 17, Maven, Selenium 4, Selenide and JUnit 5 starter framework for the UST Global SDET Retail Automation Lab.

## Special Session - XPath in Practice

The one-hour XPath session uses a deterministic local TripStack travel page, reusable XPath templates, page/component objects, and a runtime-secret safety demo. No retail app or external travel website is required.

```bash
./scripts/xpath-in-practice-demo.sh
```

Windows PowerShell:

```powershell
.\scripts\xpath-in-practice-demo.ps1
```

Trainer notes and exercises are under `docs/xpath-in-practice/`.

## W3D1 Outcome

- Standard Maven test structure
- Pinned Selenium, JUnit BOM and Surefire versions
- `tests`, `pages` and `support` package discipline
- Selenium Manager with no committed driver binaries
- Fresh Chrome session per test
- Catalog smoke test with headed and headless execution

## Prerequisites

- JDK 17 or newer
- Maven 3.9+
- Google Chrome
- Retail frontend running at `http://localhost:5173`

Start the frontend:

```bash
cd ../sdet-retail-app/frontend
npm run dev
```

## Commands

Compile without launching a browser:

```bash
mvn clean test-compile
```

Run the smoke test headed:

```bash
mvn clean test
```

Run only `SmokeTest`:

```bash
mvn clean -Dtest=SmokeTest test
```

Run the Week 3 Day 2 WebDriver fundamentals flow:

```bash
mvn clean -Dtest=CatalogFlowTest test
```

Run the Week 3 Day 3 Page Object Model flow:

```bash
mvn clean -Dtest=CatalogPomTest test
```

Run the Week 3 Day 4 Cucumber smoke scenario:

```bash
mvn clean -Dtest=RunCucumberTest test
```

Run the Week 3 Day 5 reporting smoke scenario:

```bash
mvn clean -Dtest=RunCucumberTest -Dheadless=true test
```

The same run writes Allure results, including environment metadata, to `target/allure-results/` and an Extent Spark report to `test-output/SparkReport/Spark.html`.

Run the intentional reporting failure to demonstrate screenshot evidence:

```bash
mvn clean -Dtest=RunCucumberTest -Dheadless=true -Dcucumber.filter.tags="@report-demo" test
```

`BUILD FAILURE` is expected for `@report-demo`; return to the default `@smoke` run afterward.

Run Cucumber regression examples:

```bash
mvn clean -Dtest=RunCucumberTest test -Dcucumber.filter.tags="@regression"
```

Run Cucumber exercise scenarios:

```bash
mvn clean -Dtest=RunCucumberTest test -Dcucumber.filter.tags="@exercise"
```

## Week 3 Day 6 - Gate 3 Assessment

Run the Gate 3 smoke scenarios:

```bash
mvn clean -Dtest=RunGate3Test -Dheadless=true -Dcucumber.filter.tags="@smoke" test
```

Run all green Gate 3 scenarios:

```bash
mvn clean -Dtest=RunGate3Test -Dheadless=true -Dcucumber.filter.tags="@smoke or @regression" test
```

Run the expected failure used for screenshot evidence:

```bash
mvn clean -Dtest=RunGate3Test -Dheadless=true -Dcucumber.filter.tags="@negative" test
```

Generate both preserved assessment evidence packs on macOS/Linux:

```bash
./scripts/gate3-evidence.sh
```

On Windows PowerShell:

```powershell
.\scripts\gate3-evidence.ps1
```

Run through Selenium Grid:

```bash
GRID_APP_URL=http://192.168.65.254:5174
mvn clean -Dtest=RunCucumberTest test \
  -Dheadless=true \
  -DbaseUrl=$GRID_APP_URL \
  -Dselenium.grid.url=http://localhost:4444/wd/hub
```

For Grid, the browser runs inside a container. Do not use `localhost:5173` as `baseUrl`; use the Docker host gateway URL provided by the trainer. On this Mac, `http://192.168.65.254:5174` was tested.

## Week 4 Day 4 - SOLID & Reusable Utilities

Day 4 refactors the Selenium framework so shared concerns have one home:

- `Browser` enum and registry-based `DriverFactory`
- `Waits` utility for explicit waits
- `Config` as the runtime settings source
- role interfaces: `Navigable`, `Searchable`, `CheckoutCapable`
- `ApiClient` abstraction with `HttpApiClient`
- `Day4SolidStructureTest` to verify the refactor without launching a browser

Run the no-browser structure checks:

```bash
mvn clean -Dtest=Day4SolidStructureTest test
```

Run the full Day 4 demo script on macOS/Linux:

```bash
./scripts/day4-solid-demo.sh
```

Run it on Windows PowerShell:

```powershell
.\scripts\day4-solid-demo.ps1
```

Optional browser regression after the retail frontend is running:

```bash
mvn clean -Dtest=CatalogPomTest -Dheadless=true test
```

## Week 4 Day 5 - Selenide Introduction

Day 5 rewrites the catalog search flow with Selenide:

- `$` and `$$` for concise element and collection access
- `should`, `shouldBe`, `shouldHave` for built-in waits
- `Configuration` for browser, headless, baseUrl and timeouts
- `ScreenShooterExtension` for JUnit 5 failure screenshots
- driver-free Selenide page object for catalog search, sort and empty-state checks

`ScreenShooterExtension` is provided by the core `com.codeborne:selenide` jar in version `7.16.2`; do not add a separate `selenide-junit5` dependency.

Run the Selenide catalog suite:

```bash
mvn clean -Dtest=SelenideCatalogTest -Dheadless=true test
```

Run the full Day 5 demo script on macOS/Linux:

```bash
./scripts/day5-selenide-demo.sh
```

Run it on Windows PowerShell:

```powershell
.\scripts\day5-selenide-demo.ps1
```

Intentional failure demo:

```bash
mvn -Dtest=SelenideFailureDemo -Dheadless=true test
```

The failure is expected; use it to show Selenide screenshots and failure details under `target/selenide-reports`.

## Week 6 Day 1 - Refactoring Patterns

Day 1 hardens the Selenium framework without changing user behaviour:

- before sample: `MessyCheckoutBefore` with hard waits, hard-coded URL and driver creation
- after code: `CheckoutJourney`, `WebDriverProvider`, `DefaultWebDriverProvider`
- no-browser structure checks for BasePage reuse, locator ownership, driver inversion and DSL naming
- optional browser checkout regression that proves the refactor still places an order

Run the safe structure checks:

```bash
mvn clean -Dtest=W6D1RefactoringStructureTest test
```

Run the full refactored checkout flow after the retail frontend and backend are running:

```bash
mvn -Dtest=W6D1RefactoredCheckoutFlowTest -Dheadless=true test
```

If your frontend is on a different port:

```bash
mvn -Dtest=W6D1RefactoredCheckoutFlowTest -Dheadless=true -DbaseUrl=http://127.0.0.1:5176 test
```

Run the trainer helper script on macOS/Linux:

```bash
./scripts/day1-refactoring-demo.sh
```

Run the safe check plus optional browser proof on macOS/Linux:

```bash
RUN_BROWSER=true BASE_URL=http://localhost:5173 ./scripts/day1-refactoring-demo.sh
```

Run it on Windows PowerShell:

```powershell
.\scripts\day1-refactoring-demo.ps1
```

Run the safe check plus optional browser proof on Windows PowerShell:

```powershell
.\scripts\day1-refactoring-demo.ps1 -Browser -BaseUrl http://localhost:5173
```

## Week 6 Day 2 - Build Tooling with Gradle

Day 2 adds a Gradle variant beside the Maven build:

- Gradle wrapper pinned in `gradle/wrapper/gradle-wrapper.properties`
- `build.gradle.kts` mapped from the Maven Selenium/JUnit/Cucumber dependencies
- `gradle.properties` speed levers: build cache, parallel mode and configuration cache
- dedicated Gradle tasks: `w6d1StructureTest`, `w6d1CheckoutTest`, `cucumberSmoke`, `parallelStructureTest`
- demo scripts that compare Maven and Gradle command flow

Run the default Gradle build gate:

```bash
./gradlew clean build
```

This default Gradle `test` task runs classroom-safe structure checks only. Browser and Cucumber flows stay as explicit tasks so the build gate is not affected by app availability or intentional failure demos.

Run the Gradle structure check:

```bash
./gradlew clean w6d1StructureTest
```

Run the Day 2 demo script on macOS/Linux:

```bash
./scripts/day2-gradle-demo.sh
```

Run it on Windows PowerShell:

```powershell
.\scripts\day2-gradle-demo.ps1
```

Generate a dependency graph:

```bash
./gradlew dependencies --configuration testRuntimeClasspath
```

Optional build scan:

```bash
./gradlew w6d1StructureTest --scan
```

## Week 6 Day 3 - Test Data Strategy

Day 3 adds a test-data strategy example for the retail order domain:

- `OrderBuilder` with sensible defaults and fluent overrides
- `OrderFactory` to persist built data through a repository
- Flyway migrations for schema and reference seed data
- optional Testcontainers Postgres integration test for isolated database checks
- no-Docker builder checks for participants who cannot run containers locally

Run the no-Docker builder checks:

```bash
mvn -Dtest=W6D3TestDataBuilderStructureTest test
./gradlew w6d3BuilderStructureTest
```

Run the optional isolated Postgres checks when Docker is available:

```bash
mvn -Dtest=OrdersDataIT test
./gradlew w6d3DataStrategyTest
```

Run the full Day 3 demo script on macOS/Linux:

```bash
./scripts/day3-test-data-demo.sh
```

Run the same script with the optional container proof:

```bash
RUN_DOCKER=true ./scripts/day3-test-data-demo.sh
```

Run it on Windows PowerShell:

```powershell
.\scripts\day3-test-data-demo.ps1
```

Windows optional container proof:

```powershell
$env:RUN_DOCKER='true'; .\scripts\day3-test-data-demo.ps1
```

## Week 6 Day 4 - Reporting Insights

Day 4 turns raw Allure results into a readable report:

- `categories.json` splits product and test defects, with a quarantine/flaky rule for skipped signals
- `environment.properties` and generated `executor.json` add run context
- Allure history is carried forward so trend widgets grow
- `W6D4ReportingInsightsTest` produces no-browser Allure results
- demo scripts generate a local report when the Allure CLI is installed

Run the no-browser reporting checks:

```bash
mvn -Dtest=W6D4ReportingInsightsTest test
./gradlew w6d4ReportingInsightsTest
```

Generate a local report with three history points:

```bash
./scripts/day4-reporting-insights.sh
```

Generate a category demo with intentional product/test red buckets and a quarantined flaky signal:

```bash
REPORT_FAILURE_DEMO=true ./scripts/day4-reporting-insights.sh
```

Windows PowerShell:

```powershell
.\scripts\day4-reporting-insights.ps1
```

Windows category demo with intentional product/test red buckets and a quarantined flaky signal:

```powershell
$env:REPORT_FAILURE_DEMO='true'; .\scripts\day4-reporting-insights.ps1
```

Open the generated report:

```bash
allure open target/allure-report
```

## Week 7 Day 1 - E2E Assembly and AI Authoring

Day 1 assembles one traceable order journey across four layers:

- REST Assured seeds the cart from one `OrderDraft`
- Selenium checks out and captures the order ID from the URL
- REST Assured fetches that exact order once
- JDBC verifies the same order row
- JSON Schema validates the already-fetched response
- the AI review exercise replaces an invented selector, hard wait, latest-order lookup, weak assertion and hard-coded total

Run the complete isolated demo with Docker/Testcontainers:

```bash
./scripts/day1-e2e-ai-demo.sh
```

Windows PowerShell:

```powershell
.\scripts\day1-e2e-ai-demo.ps1
```

Run the AI hardening checks without Docker or a browser:

```bash
mvn -Dtest=W7D1AiHardeningReviewTest test
```

Run against an existing ShopKart and MySQL/PostgreSQL environment:

```bash
export W7D1_RUNTIME=external
export SHOPKART_BASE_URL=http://localhost:8080
export SHOPKART_JDBC_URL='jdbc:mysql://localhost:3306/shopkart'
export SHOPKART_DB_USER=shopkart_user
export SHOPKART_DB_PASSWORD='your-password'
export SHOPKART_ALICE_PASSWORD='your-seeded-alice-password'
./scripts/day1-e2e-ai-demo.sh
```

The successful E2E result must say `2 tests`, `0 failures`, `0 errors` and `0 skipped`.

## Week 7 Day 2 - Resilience and Agentic Triage

Day 2 adds a Java component boundary for inventory-aware checkout and tests the complete resilience arc:

- healthy WireMock baseline
- 503 outage and circuit-breaker short-circuit
- client timeout for latency
- strict rejection of wrong-typed JSON
- one retry for a connection reset
- automatic `OPEN -> HALF_OPEN -> CLOSED` recovery without restart
- bounded `plan -> act -> observe -> adapt` triage over failed-run artifacts
- read-only tools, one staging-only rerun, redaction and a human approval gate

Run the complete trainer demo:

```bash
./scripts/day2-resilience-triage-demo.sh
```

Windows PowerShell:

```powershell
.\scripts\day2-resilience-triage-demo.ps1
```

Maven commands:

```bash
mvn -Dtest=W7D2ResilienceTest test
mvn -Dtest=W7D2AgenticTriageTest test
```

Gradle commands:

```bash
./gradlew w7d2Resilience
./gradlew w7d2AgenticTriage
./gradlew w7d2Day
```

Evidence is written to `target/w7d2-resilience-evidence.md` and `target/w7d2-triage-report.md`.

## General Runtime Options

Run headless:

```bash
mvn clean test -Dheadless=true
```

Override the frontend URL:

```bash
mvn clean test -DbaseUrl=http://localhost:5173 -Dheadless=true
```

The first browser run may take longer while Selenium Manager resolves and caches the matching ChromeDriver.

Check W3D2 stability three times:

```bash
for i in 1 2 3; do mvn -q -Dtest=CatalogFlowTest test -Dheadless=true || break; done
```

Check W3D3 POM stability three times:

```bash
for i in 1 2 3; do mvn -q -Dtest=CatalogPomTest test -Dheadless=true || break; done
```

Check W3D4 Cucumber stability three times:

```bash
for i in 1 2 3; do mvn -q -Dtest=RunCucumberTest test -Dheadless=true || break; done
```

## Structure

```text
src/test/java/com/ust/sdet/
  examples/   First browser-session demonstration
  tests/      JUnit test classes and assertions
  pages/      Page objects introduced on W3D3
  support/    Driver and runtime configuration
src/test/resources/
```

Do not commit `target/`, browser drivers, IDE metadata, logs, `.env`, or secret files.
