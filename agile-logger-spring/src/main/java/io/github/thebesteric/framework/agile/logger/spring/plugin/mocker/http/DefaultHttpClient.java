package io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.http;

import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.HttpClient;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * DefaultHttpClient
 *
 * @author Eric Joe
 * @version 1.0
 */
public class DefaultHttpClient implements HttpClient {
    @Override
    public ResponseEntry doGet(String url) throws Exception {
        java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return new ResponseEntry(response.statusCode(), response.body());
    }
}
