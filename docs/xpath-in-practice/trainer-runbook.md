# Trainer Runbook - XPath in Practice

## Before Class

From `repos/sdet-selenium-java`:

```bash
mvn -q -DskipTests test-compile
mvn -Dtest=XPathInPracticeTest -Dheadless=true test
```

Expected: 4 tests passed. For the live browser, use:

```bash
HEADLESS=false ./scripts/xpath-in-practice-demo.sh
```

On Windows PowerShell:

```powershell
.\scripts\xpath-in-practice-demo.ps1 -Headed
```

## 0-10 Minutes - Why XPath

1. Open `tripstack.html` through `SearchPage.open()`.
2. Inspect `input[name='from']`: CSS/name is the first choice when it is stable.
3. Type `Del` and inspect the generated-looking suggestion class.
4. Explain why exact generated class chains and list indexes are brittle.
5. Open `Xp.java` and introduce attribute, text, partial match, and axes.

## 10-30 Minutes - Four Live Widgets

### Autosuggest

Run:

```bash
mvn -Dtest=XPathInPracticeTest#searchesWithAutosuggestAndCalendar -Dheadless=false test
```

Show `Xp.suggestion("Delhi")` and `Autosuggest.select`.

### Calendar

Pause on the June calendar. Show the capped loop moving to August, then the exact `aria-label` match for `Fri Aug 21 2026`.

### Flight Card

Run:

```bash
mvn -Dtest=XPathInPracticeTest#anchorsPriceAndBookActionToNamedFlight -Dheadless=false test
```

Contrast `(//button[.='Book'])[1]` with flight number -> ancestor card -> descendant price/button.

### Bus Seat Map

Run:

```bash
mvn -Dtest=XPathInPracticeTest#selectsAnAvailableSeatForNamedOperator -Dheadless=false test
```

Inspect L12, booked L13, and ladies U01. Ask which predicates protect the action.

## 30-45 Minutes - Participant Exercises

Use `participant-exercises.md`. Ask participants to first prove uniqueness in DevTools with `$x("...").length`, then place the locator in `Xp.java`, not in the test.

## 45-55 Minutes - Structure and Secrets

Show this dependency direction:

```text
test -> page -> component -> Xp
                     |
                     +-> Secrets for runtime-only values
```

Run the secret demo and artifact check:

```bash
TRIPSTACK_DEMO_PASSWORD='classroom-only' \
  mvn -Dtest=XPathSecretSafetyTest -Dheadless=false test
```

Then inspect the newest Allure result. It must contain `"value":"[MASKED]"` and `"mode":"masked"`, never the supplied value.

## 55-60 Minutes - Wrap

Ask participants to state the rule used in each widget:

1. Prefer a stable attribute.
2. Anchor to exact visible text with `normalize-space()`.
3. Use `contains()` for meaningful dynamic fragments.
4. Traverse from a stable anchor to the target.
5. Validate uniqueness before coding.
6. Keep locators and secrets out of step/test files.

## Planned Failure Demo

In `Xp.flightBookButton`, temporarily replace the card-anchored XPath with:

```java
return "(//button[normalize-space()='Book'])[1]";
```

Change the target flight in the test to `AI-2817`. The click selects `6E-2043`, so the final status assertion fails. Restore the original locator after explaining the evidence.
