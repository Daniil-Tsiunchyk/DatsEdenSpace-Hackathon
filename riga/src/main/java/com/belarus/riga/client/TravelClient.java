package com.belarus.riga.client;

import com.belarus.riga.classes.PlayerUniverseResponse;

import java.io.IOException;

import static com.belarus.riga.templates.ApiClient.sendGet;
import static com.belarus.riga.templates.ApiClient.sendPost;

public class TravelClient {

    public static void main(String[] args) throws IOException {
        // Пример POST запроса
        String jsonPayload = "{\"key\": \"value\"}";
        // Используйте ваш API_KEY тут
        String responsePost = sendPost("player/travel", jsonPayload);
        System.out.println("POST Response: " + responsePost);
    }

    public  void postTravel(String jsonPayload) throws Exception {
        String responsePost = sendPost("player/travel", jsonPayload);
        System.out.println("POST Response: " + responsePost);
    }
}
