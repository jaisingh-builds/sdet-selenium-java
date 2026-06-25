package com.ust.sdet.support.api;

import java.io.IOException;
import java.net.http.HttpResponse;

public interface ApiClient {
    HttpResponse<String> get(String path) throws IOException, InterruptedException;
}
