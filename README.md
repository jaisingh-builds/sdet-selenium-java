# SDET Selenium Java

Selenium Java starter framework for the UST Global SDET training retail app.

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
