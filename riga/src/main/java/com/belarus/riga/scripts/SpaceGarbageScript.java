package com.belarus.riga.scripts;

import com.belarus.riga.classes.PlayerUniverseResponse;
import com.belarus.riga.classes.PlayerUniverseResponse.Ship;
import com.belarus.riga.client.UniverseClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpaceGarbageScript {

    public static int[][] parseShipGarbage(Ship ship) {
        int[][] cargoSpace = new int[ship.getCapacityY()][ship.getCapacityX()];

        for (Map.Entry<String, List<List<Integer>>> entry : ship.getGarbage().entrySet()) {
            for (List<Integer> coordinates : entry.getValue()) {
                int x = coordinates.get(0);
                int y = coordinates.get(1);
                cargoSpace[y][x] = 1;
            }
        }

        return cargoSpace;
    }

    public static List<Map.Entry<String, List<List<Integer>>>> sortPlanetGarbage(Map<String, List<List<Integer>>> garbage) {
        List<Map.Entry<String, List<List<Integer>>>> sortedGarbage = new ArrayList<>(garbage.entrySet());
        sortedGarbage.sort((o1, o2) -> Integer.compare(o2.getValue().size(), o1.getValue().size()));
        return sortedGarbage;
    }

    // Это основной метод, который нам нужно будет дополнить для выполнения шагов 3 и 4
    public static void manageGarbage() throws Exception {
        UniverseClient universeClient = new UniverseClient();
        PlayerUniverseResponse response = universeClient.getPlayerUniverse();

        // Шаг 1: Парсим текущий garbage
        int[][] shipGarbage = parseShipGarbage(response.getShip());
        printCargoSpace(shipGarbage);

        // Шаг 2: Сортируем garbage с планеты
        List<Map.Entry<String, List<List<Integer>>>> sortedPlanetGarbage = sortPlanetGarbage(response.getShip().getPlanet().getGarbage());
        System.out.println(sortedPlanetGarbage);
        // TODO: Шаги 3 и 4 - Загрузка объектов в garbage и отправка запросов на сервер
    }

    // Вспомогательный метод для вывода массива грузового пространства
    public static void printCargoSpace(int[][] cargoSpace) {
        for (int[] row : cargoSpace) {
            for (int cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) throws Exception {
        manageGarbage();
    }
}
