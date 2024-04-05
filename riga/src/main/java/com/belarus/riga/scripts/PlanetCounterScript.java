package com.belarus.riga.scripts;

import com.belarus.riga.classes.PlayerUniverseResponse;
import com.belarus.riga.client.UniverseClient;

import java.util.TreeSet;
import java.util.stream.Collectors;

public class PlanetCounterScript {

    public static void main(String[] args) {
        UniverseClient client = new UniverseClient();
        try {
            TreeSet<String> uniquePlanets = fetchUniquePlanets(client);
            printUniquePlanets(uniquePlanets);
        } catch (Exception e) {
            System.err.println("An error occurred while fetching and processing universe data: " + e.getMessage());
        }
    }

    private static TreeSet<String> fetchUniquePlanets(UniverseClient client) throws Exception {
        PlayerUniverseResponse response = client.getPlayerUniverse();
        return response.getUniverse().stream()
                .flatMap(planetInfo -> planetInfo.stream().limit(2))
                .map(Object::toString)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private static void printUniquePlanets(TreeSet<String> uniquePlanets) {
        System.out.println("Total unique planets: " + uniquePlanets.size());
        System.out.println("Sorted and numbered list of unique planets:");
        int count = 1;
        for (String planet : uniquePlanets) {
            System.out.println(count++ + ". " + planet);
        }
    }
}
