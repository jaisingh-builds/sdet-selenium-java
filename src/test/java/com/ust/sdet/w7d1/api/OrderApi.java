package com.ust.sdet.w7d1.api;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderApi {
    private final String apiBase;

    public OrderApi(String apiBase) {
        this.apiBase = apiBase;
    }

    public Response fetch(long orderId, String token) {
        return given()
            .baseUri(apiBase)
            .auth().oauth2(token)
        .when()
            .get("/orders/{id}", orderId)
        .then()
            .statusCode(200)
            .extract().response();
    }
}
