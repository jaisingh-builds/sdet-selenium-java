# SDET Selenium Java

Java 21, Maven, Selenium 4 and JUnit 5 starter framework for the UST Global SDET Retail Automation Lab.

## W3D1 Outcome

- Standard Maven test structure
- Pinned Selenium, JUnit BOM and Surefire versions
- `tests`, `pages` and `support` package discipline
- Selenium Manager with no committed driver binaries
- Fresh Chrome session per test
- Catalog smoke test with headed and headless execution

## Prerequisites

- JDK 21
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
