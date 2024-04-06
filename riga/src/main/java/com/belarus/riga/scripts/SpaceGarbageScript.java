package com.belarus.riga.scripts;

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

        if (response.getShip().getPlanet().getGarbage().isEmpty()) {
            return false;
        }

        Integer[][] shipGarbage = parseShipGarbage(response.getShip());
        print2DArray(shipGarbage);

        List<Map.Entry<String, List<List<Integer>>>> sortedPlanetGarbage = sortPlanetGarbage(response.getShip().getPlanet().getGarbage());

        Map<String, List<List<Integer>>> garbageToLoad = loadGarbage(shipGarbage, sortedPlanetGarbage);
        print2DArray(shipGarbage);
        tetrisClient.collectGarbage(garbageToLoad);
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

    private static List<List<Integer>> placeFigure(Integer[][] shipGarbage, List<List<Integer>> figure) {
        for (int angle : new int[]{0, 90, 180, 270}) {
            List<List<Integer>> rotatedFigure = rotateFigure(figure, angle);
            List<List<Integer>> newCoordinates = new ArrayList<>();
            if (findSpaceForFigure(shipGarbage, rotatedFigure, newCoordinates)) {
                return newCoordinates;
            }
        }
        return Collections.emptyList();
    }

    public static Map<String, List<List<Integer>>> loadGarbage(Integer[][] shipGarbage, List<Map.Entry<String, List<List<Integer>>>> sortedPlanetGarbage) {
        Map<String, List<List<Integer>>> bestCombination = new HashMap<>();
        backtrack(shipGarbage, sortedPlanetGarbage, 0, new HashMap<>(), bestCombination);
        return bestCombination;
    }

    private static void backtrack(Integer[][] shipGarbage, List<Map.Entry<String, List<List<Integer>>>> sortedPlanetGarbage, int position,
                                  Map<String, List<List<Integer>>> currentCombination,
                                  Map<String, List<List<Integer>>> bestCombination) {
        // Если текущая комбинация лучше, чем лучшая найденная до этого, сохраните ее
        if (countCombinationCapacity(currentCombination) > countCombinationCapacity(bestCombination)) {
            bestCombination.clear();
            bestCombination.putAll(currentCombination);
        }

        for (int i = position; i < sortedPlanetGarbage.size(); i++) {
            Map.Entry<String, List<List<Integer>>> garbageEntry = sortedPlanetGarbage.get(i);
            String garbageID = garbageEntry.getKey();

            List<List<Integer>> figure = garbageEntry.getValue();
            List<List<Integer>> newCoordinates = placeFigure(shipGarbage, figure);

            // Если фигура поместилась в трюм, добавьте ее в текущую комбинацию и продолжите поиск
            if (!newCoordinates.isEmpty()) {
                currentCombination.put(garbageID, newCoordinates);
                backtrack(shipGarbage, sortedPlanetGarbage, i + 1, currentCombination, bestCombination);
                currentCombination.remove(garbageID);

                // Удалите фигуру из трюма после просмотра всех возможных комбинаций с ней
                for (List<Integer> block : figure) {
                    int x = block.get(0);
                    int y = block.get(1);
                    shipGarbage[y][x] = 0;
                }
            }
        }
    }

    private static int countCombinationCapacity(Map<String, List<List<Integer>>> combination) {
        int total = 0;
        for (List<List<Integer>> figure : combination.values()) {
            total += figure.size();
        }
        return total;
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

    public static List<List<Integer>> rotateFigure(List<List<Integer>> figure, int angle) {
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

    public static List<List<Integer>> normalizeFigure(List<List<Integer>> figure) {
        int minX = figure.stream().min(Comparator.comparingInt(List::getFirst)).get().getFirst();
        int minY = figure.stream().min(Comparator.comparingInt(block -> block.get(1))).get().get(1);
        return figure.stream().map(block -> Arrays.asList(block.getFirst() - minX, block.get(1) - minY)).collect(Collectors.toList());
    }
}