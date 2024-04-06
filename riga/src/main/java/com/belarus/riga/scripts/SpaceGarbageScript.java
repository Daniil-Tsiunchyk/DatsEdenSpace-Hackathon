package com.belarus.riga.scripts;

import com.belarus.riga.classes.PlayerCollectResponse;
import com.belarus.riga.classes.PlayerUniverseResponse;
import com.belarus.riga.classes.PlayerUniverseResponse.Ship;
import com.belarus.riga.client.TetrisClient;
import com.belarus.riga.client.UniverseClient;

import java.util.*;
import java.util.stream.Collectors;

public class SpaceGarbageScript {

    public static Integer[][] parseShipGarbage(Ship ship) {
        Integer[][] cargoSpace = new Integer[ship.getCapacityY()][ship.getCapacityX()];
        initializeCargoSpace(cargoSpace);
        for (Map.Entry<String, List<List<Integer>>> entry : ship.getGarbage().entrySet()) {
            for (List<Integer> coordinates : entry.getValue()) {
                Integer x = coordinates.get(0);
                Integer y = coordinates.get(1);
                cargoSpace[y][x] = 1;
            }
        }

        return cargoSpace;
    }

    public static int countCapacity(Integer[][] array) {
        int count = 0;
        for (Integer[] row : array) {
            for (Integer element : row) {
                if (element == 1) {
                    count++;
                }
            }
        }
        return count;
    }

    public static List<Map.Entry<String, List<List<Integer>>>> sortPlanetGarbage(Map<String, List<List<Integer>>> garbage) {
        List<Map.Entry<String, List<List<Integer>>>> sortedGarbage = new ArrayList<>(garbage.entrySet());
        sortedGarbage.sort((o1, o2) -> Integer.compare(o2.getValue().size(), o1.getValue().size()));
        return sortedGarbage;
    }

    public static boolean manageGarbage() throws Exception {
        UniverseClient universeClient = new UniverseClient();
        TetrisClient tetrisClient = new TetrisClient();
        PlayerUniverseResponse response = universeClient.getPlayerUniverse();

        // Step 1: Parse current garbage
        Integer[][] shipGarbage = parseShipGarbage(response.getShip());
        print2DArray(shipGarbage);

        // Step 2: Sort planet garbage
        List<Map.Entry<String, List<List<Integer>>>> sortedPlanetGarbage = sortPlanetGarbage(response.getShip().getPlanet().getGarbage());
        System.out.println(sortedPlanetGarbage);
        if (sortedPlanetGarbage.isEmpty()) return true;
        // Step 3: Load objects into garbage
        Map<String, List<List<Integer>>> garbageToLoad = loadGarbage(shipGarbage, sortedPlanetGarbage);

        //  if (garbageToLoad.isEmpty()) {
        // If there is no garbage to collect, return false
        //  return false;
        //  }

        // Step 4: Send requests to the server
        PlayerCollectResponse collectResponse = tetrisClient.collectGarbage(garbageToLoad);
        System.out.println("Response from server: " + collectResponse);
        Thread.sleep(300);

        // Return true if garbage was collected
        return true;
    }

    private static boolean tryToFitFigure(Integer[][] shipGarbage, List<List<Integer>> figure, int startX, int startY) {
        for (List<Integer> block : figure) {
            int x = startX + block.get(0);
            int y = startY + block.get(1);
            if (y < 0 || y >= shipGarbage.length || x < 0 || x >= shipGarbage[y].length || shipGarbage[y][x] != 0) {
                return false;
            }
        }
        return true;
    }

    private static boolean findSpaceForFigure(Integer[][] shipGarbage, List<List<Integer>> figure, List<List<Integer>> newCoordinates) {
        for (int y = 0; y < shipGarbage.length; y++) {
            for (int x = 0; x < shipGarbage[y].length; x++) {
                if (tryToFitFigure(shipGarbage, figure, x, y)) {
                    for (List<Integer> block : figure) {
                        int newX = x + block.get(0);
                        int newY = y + block.get(1);
                        shipGarbage[newY][newX] = 1;
                        newCoordinates.add(Arrays.asList(newX, newY));
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean canPlaceFigure(Integer[][] shipGarbage, List<List<Integer>> figure) {
        for (int angle : new int[]{0, 90, 180, 270}) {
            // Create a copy of shipGarbage
            Integer[][] shipGarbageCopy = Arrays.stream(shipGarbage)
                    .map(Integer[]::clone)
                    .toArray(Integer[][]::new);
            // Rotate the figure
            List<List<Integer>> rotatedFigure = rotateFigure(figure, angle);
            List<List<Integer>> dummyCoordinates = new ArrayList<>();
            if (findSpaceForFigure(shipGarbageCopy, rotatedFigure, dummyCoordinates)) {
                return true;
            }
        }
        return false;
    }

    private static List<List<Integer>> placeFigure(Integer[][] shipGarbage, List<List<Integer>> figure) {
        for (int angle : new int[]{0, 90, 180, 270}) {
            List<List<Integer>> rotatedFigure = rotateFigure(figure, angle);
            List<List<Integer>> newCoordinates = new ArrayList<>();
            if (findSpaceForFigure(shipGarbage, rotatedFigure, newCoordinates)) {
                //if (findSpaceForFigure(shipGarbage, figure, newCoordinates)) {
                return newCoordinates;
            }
        }
        return Collections.emptyList();
    }

    public static Map<String, List<List<Integer>>> loadGarbage(Integer[][] shipGarbage, List<Map.Entry<String, List<List<Integer>>>> sortedPlanetGarbage) {
        Map<String, List<List<Integer>>> loadedGarbage = new HashMap<>();
        //  int currentCapacity = countCapacity(shipGarbage);
        //  int totalCapacity = shipGarbage.length * (shipGarbage[0].length);
        //  System.out.println(currentCapacity+" - - - -- " +totalCapacity);
        //  int minimumLoad;

        for (Map.Entry<String, List<List<Integer>>> garbageEntry : sortedPlanetGarbage) {
            String garbageID = garbageEntry.getKey();
            List<List<Integer>> figure = garbageEntry.getValue();

            //      if (currentCapacity == 0) {
            //          minimumLoad = (int) Math.ceil(totalCapacity * 0.3);
            //     } else {
            //         minimumLoad = (int) Math.ceil(totalCapacity * 0.05);
            //     }

            //      int potentialCapacity = currentCapacity + figure.size();

            //     if (canPlaceFigure(shipGarbage, figure) && (potentialCapacity >= minimumLoad || potentialCapacity == totalCapacity)) {
            if (canPlaceFigure(shipGarbage, figure)) {
                List<List<Integer>> newCoordinates = placeFigure(shipGarbage, figure);
                loadedGarbage.put(garbageID, newCoordinates);
                System.out.println("Старые координаты" + figure);
                System.out.println("Новые координаты" + newCoordinates);

                //    currentCapacity = potentialCapacity;
            }
        }

        return loadedGarbage;
    }

    public static void initializeCargoSpace(Integer[][] cargoSpace) {
        for (Integer[] integers : cargoSpace) {
            Arrays.fill(integers, 0);
        }
    }

    public static void print2DArray(Integer[][] array) {
        for (Integer[] row : array) {
            for (Integer item : row) {
                System.out.print(item + " ");
            }
            System.out.println();
        }
    }

    private static List<List<Integer>> rotateFigure(List<List<Integer>> figure, int angle) {
        List<List<Integer>> rotatedFigure = new ArrayList<>();
        for (List<Integer> block : figure) {
            int x = block.get(0);
            int y = block.get(1);
            List<Integer> rotatedBlock = switch (angle) {
                case 90 -> Arrays.asList(y, -x);
                case 180 -> Arrays.asList(-x, -y);
                case 270 -> Arrays.asList(-y, x);
                default -> Arrays.asList(x, y);
            };
            rotatedFigure.add(rotatedBlock);
        }
        return normalizeFigure(rotatedFigure);
    }

    private static List<List<Integer>> normalizeFigure(List<List<Integer>> figure) {
        int minX = figure.stream().min(Comparator.comparingInt(block -> block.get(0))).get().get(0);
        int minY = figure.stream().min(Comparator.comparingInt(block -> block.get(1))).get().get(1);
        return figure.stream().map(block -> Arrays.asList(block.get(0) - minX, block.get(1) - minY)).collect(Collectors.toList());
    }

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