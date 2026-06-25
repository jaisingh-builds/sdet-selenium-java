package com.ust.sdet.support.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class HttpApiClient implements ApiClient {
    private final URI baseUri;
    private final HttpClient httpClient;

    public HttpApiClient(String baseUrl) {
        this.baseUri = URI.create(baseUrl.replaceAll("/$", ""));
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public HttpResponse<String> get(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(baseUri.resolve(path)).GET().build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
