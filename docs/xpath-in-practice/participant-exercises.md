# Participant Exercises

Work in `src/test/java/com/ust/sdet/xpath/`. Do not place raw XPath inside `XPathInPracticeTest`.

## Exercise 1 - Named Bus Operator

Starting from the visible operator text `GreenLine Travels`, locate only that card's `Select Seats` button.

Acceptance checks:

- `$x("your XPath").length` is `1` in DevTools.
- The XPath still works if another bus card is inserted before it.
- The test opens the GreenLine seat map.

## Exercise 2 - Repair Four Brittle Locators

Replace each anti-pattern with a stable locator:

```xpath
/html/body/main/section[2]/div[2]/article[1]/button
//*[contains(@class,'flight-card')][1]//button
(//button[normalize-space()='Book'])[1]
//*[@id='passenger_83921']
```

Use a stable attribute, label, text anchor, partial match, or axis. Explain why each replacement is less coupled to page layout.

## Exercise 3 - Secret-Safe Sign-In

Run `XPathSecretSafetyTest` with a runtime value. Confirm all four controls:

- no hard-coded password in Java
- password input has `type="password"`
- Allure parameter is `[MASKED]`
- runtime value is absent from `target/`

macOS/Linux:

```bash
TRIPSTACK_DEMO_PASSWORD='your-training-value' \
  mvn -Dtest=XPathSecretSafetyTest -Dheadless=true test
```

Windows PowerShell:

```powershell
$env:TRIPSTACK_DEMO_PASSWORD = 'your-training-value'
mvn "-Dtest=XPathSecretSafetyTest" "-Dheadless=true" test
Remove-Item Env:TRIPSTACK_DEMO_PASSWORD
```
