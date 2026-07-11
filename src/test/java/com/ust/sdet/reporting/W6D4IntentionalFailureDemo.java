package com.ust.sdet.reporting;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("Framework Hardening")
@Feature("Reporting Insights")
@Owner("SDET Trainer")
class W6D4IntentionalFailureDemo {

    @Test
    @Story("Product defect bucket")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Intentional failed assertion so Allure can show the Product defects category.")
    void productDefectAppearsAsFailed() {
        assertEquals("ORDER_CONFIRMED", "PAYMENT_DECLINED", "Checkout status should be confirmed");
    }

    @Test
    @Story("Flaky bucket")
    @Severity(SeverityLevel.NORMAL)
    @Description("Intentional quarantined flaky example so the Flaky tests category has a visible match.")
    void timeoutAppearsInFlakyBucket() {
        throw new TestAbortedException("quarantined flaky test: timeout waiting for cart badge");
    }

    @Test
    @Story("Test defect bucket")
    @Severity(SeverityLevel.MINOR)
    @Description("Intentional broken automation error so Allure can show the Test defects category.")
    void automationDefectAppearsAsBroken() {
        throw new IllegalStateException("Test setup broke before assertion");
    }
}
