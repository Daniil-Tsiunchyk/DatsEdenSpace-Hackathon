package com.belarus.riga.client;

import com.belarus.riga.classes.PlayerUniverseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

public class UniverseClient {

    private static final String BASE_URL = "https://datsedenspace.datsteam.dev/player/universe";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public PlayerUniverseResponse getPlayerUniverse(String authToken) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(BASE_URL);
            request.setHeader("X-Auth-Token", authToken);

            HttpResponse response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                return objectMapper.readValue(jsonResponse, PlayerUniverseResponse.class);
            } else if (statusCode == 400) {
                String errorResponse = EntityUtils.toString(response.getEntity());
                System.out.println(errorResponse);
            } else {
                throw new RuntimeException("Unexpected response status: " + statusCode);
            }

            return null;
        }
    }
}
