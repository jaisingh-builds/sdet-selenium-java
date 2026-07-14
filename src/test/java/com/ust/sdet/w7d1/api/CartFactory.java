package com.ust.sdet.w7d1.api;

import com.ust.sdet.w7d1.data.OrderDraft;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class CartFactory {
    private final String apiBase;
    private final String customerEmail;
    private final String customerPassword;

    public CartFactory(String apiBase, String customerEmail, String customerPassword) {
        this.apiBase = apiBase;
        this.customerEmail = customerEmail;
        this.customerPassword = customerPassword;
    }

    public SeededCart seedCartWith(OrderDraft order) {
        Response login = given()
            .baseUri(apiBase)
            .contentType(ContentType.JSON)
            .body(Map.of("email", customerEmail, "password", customerPassword))
        .when()
            .post("/auth/login")
        .then()
            .statusCode(200)
            .extract().response();

        String token = login.jsonPath().getString("token");
        long customerId = login.jsonPath().getLong("customerId");
        String displayName = login.jsonPath().getString("customer.displayName");

        long cartId = given()
            .baseUri(apiBase)
            .auth().oauth2(token)
        .when()
            .post("/carts")
        .then()
            .statusCode(201)
            .extract().jsonPath().getLong("cartId");

        Response cart = given()
            .baseUri(apiBase)
            .auth().oauth2(token)
            .contentType(ContentType.JSON)
            .body(Map.of("sku", order.sku(), "qty", order.quantity()))
        .when()
            .post("/carts/{id}/items", cartId)
        .then()
            .statusCode(200)
            .extract().response();

        assertThat(cart.jsonPath().getLong("subtotalPaise")).isEqualTo(order.subtotalPaise());
        assertThat(cart.jsonPath().getLong("totalPaise")).isEqualTo(order.subtotalPaise());
        assertThat(cart.jsonPath().getInt("items[0].qty")).isEqualTo(order.quantity());
        assertThat(cart.jsonPath().getString("items[0].sku")).isEqualTo(order.sku());

        return new SeededCart(cartId, token, customerId, customerEmail, displayName);
    }
}
