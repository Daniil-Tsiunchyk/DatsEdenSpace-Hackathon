package com.belarus.riga;

import com.belarus.riga.client.HttpClientTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class RigaApplication {

    public static void main(String[] args) {
        HttpClientTemplate client = new HttpClientTemplate();
        try {
            // Пример GET запроса
            String responseGet = client.sendGet("endpoint");
            System.out.println("GET Response: " + responseGet);

            // Пример POST запроса
            String jsonPayload = "{\"key\": \"value\"}";
            String responsePost = client.sendPost("endpoint", jsonPayload);
            System.out.println("POST Response: " + responsePost);
        } catch (IOException e) {
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
