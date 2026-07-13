# XPath in Practice - Demo Pack

This pack supports the one-hour `XPath_In_Practice_1hr.pptx` session with a deterministic local travel site. It does not call a public travel site and needs no extra application server.

## What Is Included

| Area | Files |
| --- | --- |
| Local TripStack site | `src/test/resources/xpath-demo/` |
| XPath templates | `src/test/java/com/ust/sdet/xpath/ui/locators/Xp.java` |
| Reusable widgets | `ui/components/Autosuggest.java`, `Calendar.java`, `AccountPanel.java` |
| Page objects | `ui/pages/SearchPage.java`, `ResultsPage.java`, `SeatMapPage.java` |
| Runtime secrets | `data/secret/Secrets.java`, `local-secret-template.properties` |
| Runnable tests | `XPathInPracticeTest.java`, `XPathSecretSafetyTest.java` |
| Trainer automation | `scripts/xpath-in-practice-demo.sh`, `.ps1` |

## Prerequisites

- JDK 17 or newer
- Maven 3.9+
- Google Chrome

No Node.js, Python, Docker, database, retail app, or internet connection is required after Maven dependencies are available.

## Quick Start

macOS/Linux, headless:

```bash
./scripts/xpath-in-practice-demo.sh
```

macOS/Linux, headed:

```bash
HEADLESS=false ./scripts/xpath-in-practice-demo.sh
```

Windows PowerShell, headless:

```powershell
.\scripts\xpath-in-practice-demo.ps1
```

Windows PowerShell, headed:

```powershell
.\scripts\xpath-in-practice-demo.ps1 -Headed
```

## Expected Result

- `XPathInPracticeTest`: 4 passed
- `XPathSecretSafetyTest`: 1 passed
- final script check: no runtime secret found under `target/`

The secret test uses a synthetic runtime value. It sends only `[MASKED]` to Allure and disables screenshots and page-source capture for that test.

## Trainer Files

- `trainer-runbook.md`: minute-by-minute delivery flow
- `participant-exercises.md`: three deck-aligned exercises
- `answer-key.md`: working XPath and Selenide solutions
