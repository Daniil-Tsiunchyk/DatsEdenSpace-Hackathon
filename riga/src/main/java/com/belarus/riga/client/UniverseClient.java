package com.belarus.riga.client;

import com.belarus.riga.classes.PlayerUniverseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.belarus.riga.templates.ApiClient.sendGet;

public class UniverseClient {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public PlayerUniverseResponse getPlayerUniverse() throws Exception {
        String jsonResponse = sendGet("player/universe");
        return objectMapper.readValue(jsonResponse, PlayerUniverseResponse.class);
    }
}
