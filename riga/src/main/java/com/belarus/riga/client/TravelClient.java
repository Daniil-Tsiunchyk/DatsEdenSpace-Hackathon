package com.belarus.riga.client;

import java.io.IOException;

import static com.belarus.riga.templates.ApiClient.sendPost;

public class TravelClient {

    public static void main(String[] args) throws IOException {
        // Пример POST запроса
        String jsonPayload = "{\"key\": \"value\"}";
        // Используйте ваш API_KEY тут
        String responsePost = sendPost("player/travel", jsonPayload);
        System.out.println("POST Response: " + responsePost);
    }
}
