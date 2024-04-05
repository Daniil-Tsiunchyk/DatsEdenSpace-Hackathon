package com.belarus.riga.templates;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class ApiClient {

    private static final String BASE_URL = "https://datsedenspace.datsteam.dev/";
    private static final String X_AUTH_TOKEN = "660e963e5bc03660e963e5bc06";
    private static final String CONTENT_TYPE = "Content-type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String UNEXPECTED_STATUS = "Unexpected response status: ";

    public static String sendGet(String endpoint) throws IOException {
        HttpGet request = new HttpGet(BASE_URL + endpoint);
        request.setHeader("X-Auth-Token", X_AUTH_TOKEN);

        return executeRequest(request);
    }

    public static String sendPost(String endpoint, String jsonPayload) throws IOException {
        HttpPost request = new HttpPost(BASE_URL + endpoint);
        request.setHeader("X-Auth-Token", X_AUTH_TOKEN);
        request.setHeader(CONTENT_TYPE, APPLICATION_JSON);
        request.setEntity(new StringEntity(jsonPayload));

        return executeRequest(request);
    }

    private static String executeRequest(HttpUriRequest request) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(request)) {
            return handleResponse(response);
        }
    }

    private static String handleResponse(CloseableHttpResponse response) throws IOException {
        int status = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            throw new IOException("Response contains no content");
        }

        String result = EntityUtils.toString(entity);
        EntityUtils.consume(entity);

        if (status >= 200 && status < 300) {
            return result;
        } else {
            throw new IOException(UNEXPECTED_STATUS + status + " - " + result);
        }
    }
}