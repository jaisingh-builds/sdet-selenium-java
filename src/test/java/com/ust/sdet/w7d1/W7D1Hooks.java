package com.ust.sdet.w7d1;

import com.ust.sdet.support.DriverFactory;
import com.ust.sdet.w7d1.db.OrderDatabase;
import com.ust.sdet.w7d1.runtime.W7D1Runtime;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.nio.charset.StandardCharsets;

public class W7D1Hooks {
    private static W7D1Runtime runtime;

    private final E2EContext context;

    public W7D1Hooks(E2EContext context) {
        this.context = context;
    }

    @BeforeAll
    public static void startRuntime() {
        runtime = W7D1Runtime.start();
        System.setProperty("baseUrl", runtime.baseUrl());
    }

    @Before("@w7d1")
    public void startScenario(Scenario scenario) {
        context.runtime = runtime;
        context.scenario = scenario;
        context.database = new OrderDatabase(runtime);
        context.driver = DriverFactory.createDriver();
    }

    @After(value = "@w7d1", order = 20)
    public void attachEvidence(Scenario scenario) {
        if (scenario.isFailed() && context.driver instanceof TakesScreenshot screenshotDriver) {
            scenario.attach(
                screenshotDriver.getScreenshotAs(OutputType.BYTES),
                "image/png",
                scenario.getName()
            );
        }
        if (context.orderId != null) {
            String evidence = "orderId=" + context.orderId
                + ", API order fetches=" + context.orderFetchCount
                + ", expectedTotalPaise=" + context.order.totalPaise();
            scenario.attach(evidence.getBytes(StandardCharsets.UTF_8), "text/plain", "Cross-layer journey");
        }
    }

    @After(value = "@w7d1", order = 10)
    public void cleanScenarioData() {
        if (context.database != null) {
            context.database.cleanupCart(context.cartId);
        }
    }

    @After(value = "@w7d1", order = 0)
    public void closeBrowser() {
        if (context.driver != null) {
            context.driver.quit();
        }
    }

    @AfterAll
    public static void stopRuntime() {
        if (runtime != null) {
            runtime.close();
        }
    }
}
