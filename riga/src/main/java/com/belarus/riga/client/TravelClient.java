package com.belarus.riga.client;

import com.belarus.riga.classes.PlayerUniverseResponse;

import java.io.IOException;

import static com.belarus.riga.templates.ApiClient.sendGet;
import static com.belarus.riga.templates.ApiClient.sendPost;

public class TravelClient {
    public void postTravel(String jsonPayload) throws Exception {
        sendPost("player/travel", jsonPayload);
    }
}
