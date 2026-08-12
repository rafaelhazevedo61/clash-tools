package br.com.clash.cwlexporter.utils;

import java.net.URI;
import java.net.http.HttpRequest;

public class HttpUtil {

    private HttpUtil() {
    }

    public static HttpRequest createRequest(String url, String bearerToken) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + bearerToken)
                .header("Accept", "application/json")
                .GET()
                .build();
    }
}
