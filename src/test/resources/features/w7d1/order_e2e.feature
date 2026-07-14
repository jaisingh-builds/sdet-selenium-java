@w7d1
Feature: ShopKart cross-layer order assembly

  @four-layer
  Scenario: A placed order is shown, persisted and contract-valid
    Given a customer with a seeded cart
    When they check out in the UI
    Then the orders API returns the placed order
    And the order row exists in the database
    And the order response matches its schema

  @ai-hardened
  Scenario: A valid coupon produces one deterministic discounted order
    Given a customer with a seeded cart for coupon "UST10"
    When they apply the valid coupon and check out in the UI
    Then the orders API returns the placed order
    And the order row exists in the database
    And the order response matches its schema
