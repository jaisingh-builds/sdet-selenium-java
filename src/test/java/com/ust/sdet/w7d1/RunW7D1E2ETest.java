package com.ust.sdet.w7d1;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/w7d1/order_e2e.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.ust.sdet.w7d1")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@w7d1")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "pretty, io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
)
public class RunW7D1E2ETest {
}
