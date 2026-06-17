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
