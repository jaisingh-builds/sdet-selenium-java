Feature: Gate 3 retail checkout assessment
  As a shopper
  I want to search the catalog, add products, and complete checkout
  So that the team can prove the retail journey works on every build

  Background:
    Given the catalog is open

  @smoke
  Scenario: Buy a single product end to end
    When I search for "headphones"
    And I add the first result to the cart
    Then the cart badge shows 1
    When I open the cart
    Then the cart has 1 line item
    When I place the order
    Then the order is confirmed

  @smoke
  Scenario: Cart badge reflects the item added
    When I search for "shoes"
    And I add the first result to the cart
    Then the cart badge shows 1

  @regression
  Scenario Outline: Buy "<product>" end to end
    When I search for "<product>"
    And I add the first result to the cart
    Then the cart badge shows 1
    When I open the cart
    Then the cart has 1 line item
    When I place the order
    Then the order is confirmed

    Examples:
      | product    |
      | headphones |
      | shoes      |
      | lamp       |

  @regression
  Scenario: A fresh cart is empty
    When I open the cart
    Then the cart has 0 line items

  @negative
  Scenario: Capture evidence when the cart badge assertion fails
    When I search for "headphones"
    And I add the first result to the cart
    Then the cart badge shows 9
