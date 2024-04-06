package com.belarus.riga.client;

import com.belarus.riga.classes.PlayerCollectResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.belarus.riga.templates.ApiClient.sendPost;

public class TetrisClient {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public PlayerCollectResponse collectGarbage(Map<String, List<List<Integer>>> garbageData) throws IOException {
        String jsonPayload = objectMapper.writeValueAsString(Collections.singletonMap("garbage", garbageData));
        String jsonResponse = sendPost("player/collect", jsonPayload);
        return objectMapper.readValue(jsonResponse, PlayerCollectResponse.class);
    }
}
