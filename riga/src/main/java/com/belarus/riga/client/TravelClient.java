package com.belarus.riga.client;

import java.io.IOException;

public class TravelClient {
    public static void main(String[] args) {
        HttpClientTemplate client = new HttpClientTemplate();
        try {
            // Пример POST запроса

            String jsonPayload = "{\"key\": \"value\"}";
            String responsePost = client.sendPost("https://datsedenspace.datsteam.dev/player/travel", jsonPayload);
            System.out.println("POST Response: " + responsePost);
        } catch (
                IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
