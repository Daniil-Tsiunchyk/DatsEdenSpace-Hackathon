package com.belarus.riga.client;

import com.belarus.riga.classes.PlayerUniverseResponse;

public class UniversityClientStarter {
    public static void main(String[] args) {
        UniverseClient client = new UniverseClient();
        try {
            PlayerUniverseResponse response = client.getPlayerUniverse("660e963e5bc03660e963e5bc06");
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
