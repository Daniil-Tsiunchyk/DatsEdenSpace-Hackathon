package com.belarus.riga.client;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpClientTemplate {

    private static final String API_URL = "http://api.datsteam/";
    private static final String API_KEY = "your_api_key_here";

    private final CloseableHttpClient httpClient;

    public HttpClientTemplate() {
        this.httpClient = HttpClients.createDefault();
    }

    public String sendGet(String endpoint) throws IOException {
        HttpGet request = new HttpGet(API_URL + endpoint);

        request.setHeader("Authorization", "Bearer " + API_KEY);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            return handleResponse(response);
        }
    }

    public String sendPost(String endpoint, String json) throws IOException {
        HttpPost request = new HttpPost(API_URL + endpoint);

        request.setHeader("Authorization", "Bearer " + API_KEY);
        request.setHeader("Content-type", "application/json");

        request.setEntity(new StringEntity(json));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            return handleResponse(response);
        }
    }

    private String handleResponse(HttpResponse response) throws IOException {
        int status = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();
        String result = EntityUtils.toString(entity);
        EntityUtils.consume(entity);

        if (status >= 200 && status < 300) {
            return result;
        } else {
            throw new IOException("Unexpected response status: " + status);
        }
    }

    public void close() throws IOException {
        httpClient.close();
    }
}
