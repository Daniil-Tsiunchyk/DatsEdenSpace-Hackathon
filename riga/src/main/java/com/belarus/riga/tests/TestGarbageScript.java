package com.belarus.riga.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.belarus.riga.scripts.SpaceGarbageScript.*;

public class TestGarbageScript {
    public static void main(String[] args) {
        Integer[][] shipGarbage = new Integer[11][8];
        initializeCargoSpace(shipGarbage);
        System.out.println("Initial shipGarbage:");
        print2DArray(shipGarbage);

        List<Map.Entry<String, List<List<Integer>>>> sortedPlanetGarbage = new ArrayList<>();
        sortedPlanetGarbage.add(Map.entry("6fSWkmU", List.of(List.of(0, 3), List.of(0, 2), List.of(0, 1), List.of(0, 0), List.of(1, 3), List.of(1, 0), List.of(2, 3), List.of(2, 2), List.of(2, 1), List.of(2, 0), List.of(3, 1))));
        sortedPlanetGarbage.add(Map.entry("6tjTLHP", List.of(List.of(0, 3), List.of(0, 2), List.of(0, 1), List.of(0, 0), List.of(1, 3), List.of(2, 3), List.of(2, 2), List.of(2, 1), List.of(2, 0), List.of(3, 2), List.of(3, 0))));

        Map<String, List<List<Integer>>> loadedGarbage = loadGarbage(shipGarbage, sortedPlanetGarbage);
        for (String id : loadedGarbage.keySet()) {
            System.out.println("Loaded garbage ID: " + id + " with new coordinates: " + loadedGarbage.get(id));
        }

        System.out.println("Initialized shipGarbage:");
        print2DArray(shipGarbage);
    }
}
