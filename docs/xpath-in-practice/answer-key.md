# XPath in Practice - Answer Key

## Exercise 1

```xpath
//*[normalize-space()='GreenLine Travels']
  /ancestor::*[contains(@class,'bus-card')]
  //button[normalize-space()='Select Seats']
```

Selenide:

```java
$x(Xp.busSelectSeatsButton("GreenLine Travels"))
    .shouldBe(visible)
    .click();
```

## Exercise 2

Search button with stable ID:

```xpath
//*[@id='search-flights']
```

Flight card by meaningful class fragment and named flight:

```xpath
//*[normalize-space()='6E-2043']
  /ancestor::*[contains(@class,'flight-card')]
```

Book button inside the named flight card:

```xpath
//*[normalize-space()='AI-2817']
  /ancestor::*[contains(@class,'flight-card')]
  //button[normalize-space()='Book']
```

Passenger input from its stable label:

```xpath
//label[normalize-space()='Full name']/following::input[1]
```

The dynamic passenger ID is intentionally not used.

## Exercise 3

Resolution order in `Secrets.find`:

1. environment variable `TRIPSTACK_DEMO_PASSWORD`
2. injected JVM property `tripstack.demo.password`
3. ignored `secrets.local.properties`

Safe reporting:

```java
Allure.parameter("Password", Secrets.mask(secret), Parameter.Mode.MASKED);
```

The code reports `[MASKED]`, not the actual secret. `XPathDemoConfig.applySecretSafe()` also disables screenshots and page-source capture for the secret test.

## Seat XPath Variants

Specific available seat:

```xpath
//*[@data-seat='L12'
  and contains(@class,'available')
  and not(contains(@class,'booked'))]
```

First available sleeper:

```xpath
(//*[contains(@class,'seat')
  and contains(@class,'sleeper')
  and contains(@class,'available')
  and not(contains(@class,'booked'))])[1]
```

First available non-ladies seat:

```xpath
(//*[contains(@class,'seat')
  and contains(@class,'available')
  and not(contains(@class,'booked'))
  and not(contains(@class,'ladies'))])[1]
```
