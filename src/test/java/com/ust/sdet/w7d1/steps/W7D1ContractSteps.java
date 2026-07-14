package com.ust.sdet.w7d1.steps;

import com.ust.sdet.w7d1.E2EContext;
import io.cucumber.java.en.And;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

public class W7D1ContractSteps {
    private final E2EContext context;

    public W7D1ContractSteps(E2EContext context) {
        this.context = context;
    }

    @And("the order response matches its schema")
    public void orderResponseMatchesSchema() {
        assertThat(context.orderFetchCount)
            .as("API response must be fetched once and reused by the contract layer")
            .isEqualTo(1);
        context.orderResponse.then().assertThat()
            .body(matchesJsonSchemaInClasspath("schemas/w7d1/order.json"));
    }
}
