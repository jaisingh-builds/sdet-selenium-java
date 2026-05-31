# SDET Selenium Java

Selenium Java starter framework for the UST Global SDET training retail app.

The application will use a ReactJS frontend over a POS/retail API and OMS microservice slice. Selenium tests should focus on the same user-facing flows students first automate in Playwright: login, products, cart, checkout, orders, profile, and admin.

## Setup

```bash
mvn test
```

Override the app URL:

```bash
mvn test -DbaseUrl=http://localhost:4000
```

## Structure

- `pages/`: Page Object Model classes.
- `tests/`: TestNG tests.
- `utils/`: Driver and configuration helpers.

## Training Alignment

- Week 3: Selenium Java comparison against the same ReactJS UI domain used in Week 1.
- Week 5: Debugging UI failures and timing issues.
- Week 7: Capstone UI coverage alongside API, DB, contract, and resilience evidence.
