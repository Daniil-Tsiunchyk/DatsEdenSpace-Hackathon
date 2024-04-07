package com.belarus.riga.client;

import static com.belarus.riga.templates.ApiClient.sendPost;

public class TravelClient {
    public void postTravel(String jsonPayload) throws Exception {
        System.out.println("json:" +jsonPayload);
        sendPost("player/travel", jsonPayload);
    }
}
