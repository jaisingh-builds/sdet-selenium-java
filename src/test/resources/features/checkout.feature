Feature: Catalog checkout
  As a shopper
  I want to find products, add them to my cart, and complete an order
  So that the retail purchase journey is covered in shared language

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

  @regression
  Scenario Outline: Buy "<product>" end to end
    When I search for "<product>"
    And I add the first result to the cart
    Then the cart badge shows <count>
    When I open the cart
    Then the cart has <count> line item
    When I place the order
    Then the order is confirmed

    Examples:
      | product    | count |
      | headphones | 1     |
      | shoes      | 1     |
      | lamp       | 1     |

  @exercise
  Scenario: A fresh cart is empty
    When I open the cart
    Then the cart has 0 line items

  @report-demo
  Scenario: Capture evidence when an assertion fails
    When I search for "headphones"
    And I add the first result to the cart
    Then the cart badge shows 9
