package com.ust.sdet.w7d1;

import com.ust.sdet.w7d1.api.SeededCart;
import com.ust.sdet.w7d1.data.OrderDraft;
import com.ust.sdet.w7d1.db.OrderDatabase;
import com.ust.sdet.w7d1.runtime.W7D1Runtime;
import io.restassured.response.Response;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;

public class E2EContext {
    public W7D1Runtime runtime;
    public WebDriver driver;
    public Scenario scenario;
    public OrderDatabase database;
    public OrderDraft order;
    public SeededCart seededCart;
    public Long cartId;
    public Long orderId;
    public Response orderResponse;
    public int orderFetchCount;
}
