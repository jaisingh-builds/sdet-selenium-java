package com.ust.sdet.tests;

import com.ust.sdet.utils.TestConfig;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HealthCheckTest {
    @Test
    public void retailApiHealthCheckIsAvailable() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TestConfig.baseUrl() + "/api/health"))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertTrue(response.body().contains("sdet-retail-app"));
    }
}
