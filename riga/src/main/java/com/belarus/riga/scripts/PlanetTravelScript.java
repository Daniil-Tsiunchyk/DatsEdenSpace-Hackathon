package com.belarus.riga.scripts;


import com.belarus.riga.classes.PlayerUniverseResponse;
import com.belarus.riga.client.UniverseClient;

import lombok.Data;

import java.util.*;

public class PlanetTravelScript {

    // Метод для преобразования данных из List<List<Object>> в List<PlanetTravel>
    public static List<PlanetTravel> mapData(List<List<Object>> universe) {
        List<PlanetTravel> result = new ArrayList<>();
        for (List<Object> travel : universe) {
            String departurePlanet = (String) travel.get(0);
            String landingPlanet = (String) travel.get(1);
            int fuel = (int) travel.get(2);
            result.add(new PlanetTravel(departurePlanet, landingPlanet, fuel));
        }
        return result;
    }
    @Data
    public static class PlanetTravel {
        String departurePlanet;
        String landingPlanet;
        int fuel;

        public PlanetTravel(String departurePlanet, String landingPlanet, int fuel) {
            this.departurePlanet = departurePlanet;
            this.landingPlanet = landingPlanet;
            this.fuel = fuel;
        }

        @Override
        public String toString() {
            return departurePlanet + " -> " + landingPlanet + " (fuel: " + fuel + ")";
        }
    }
    public static class PathInfo {
        int totalFuel;
        List<PlanetTravel> path;

        public PathInfo(int totalFuel, List<PlanetTravel> path) {
            this.totalFuel = totalFuel;
            this.path = path;
        }
    }
    public static PathInfo findShortestPath(List<PlanetTravel> travels, String start, String end) {
        Map<String, Map<String, Integer>> graph = buildGraph(travels);

        Map<String, Integer> distances = new HashMap<>();
        Map<String, List<PlanetTravel>> paths = new HashMap<>();
        PriorityQueue<String> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        for (String vertex : graph.keySet()) {
            if (vertex.equals(start)) {
                distances.put(vertex, 0);
            } else {
                distances.put(vertex, Integer.MAX_VALUE);
            }
            priorityQueue.offer(vertex);
        }

        while (!priorityQueue.isEmpty()) {
            String current = priorityQueue.poll();
            if (current.equals(end)) {
                break; // Как только достигнута конечная точка, нет необходимости продолжать
            }
            if (!graph.containsKey(current)) {
                continue; // Пропускаем вершину, если она не имеет исходящих рёбер
            }
            for (String neighbor : graph.get(current).keySet()) {
                int distance = distances.get(current) + graph.get(current).get(neighbor);
                if (distance < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    distances.put(neighbor, distance);
                    List<PlanetTravel> newPath = new ArrayList<>(paths.getOrDefault(current, Collections.emptyList()));
                    newPath.add(new PlanetTravel(current, neighbor, graph.get(current).get(neighbor)));
                    paths.put(neighbor, newPath);
                    priorityQueue.remove(neighbor);
                    priorityQueue.offer(neighbor);
                }
            }
        }
        return new PathInfo(distances.get(end), paths.getOrDefault(end, Collections.emptyList()));
    }
    private static Map<String, Map<String, Integer>> buildGraph(List<PlanetTravel> travels) {
        Map<String, Map<String, Integer>> graph = new HashMap<>();
        for (PlanetTravel travel : travels) {
            graph.putIfAbsent(travel.departurePlanet, new HashMap<>());
            graph.get(travel.departurePlanet).put(travel.landingPlanet, travel.fuel);
        }
        return graph;
    }
    public static void main(String[] args) {
        List<PlanetTravel> travels = new ArrayList<>();
        PlayerUniverseResponse response = new PlayerUniverseResponse();
        UniverseClient client = new UniverseClient();

        travels.add(new PlanetTravel("Earth", "Mars", 50));
        travels.add(new PlanetTravel("Mars", "Jupiter", 30));
        travels.add(new PlanetTravel("Earth", "Venus", 20));
        travels.add(new PlanetTravel("Venus", "Jupiter", 40));
        travels.add(new PlanetTravel("Mars", "Saturn", 60));
        travels.add(new PlanetTravel("Earth", "Mercury", 25));
        travels.add(new PlanetTravel("Venus", "Saturn", 35));
        travels.add(new PlanetTravel("Jupiter", "Neptune", 80));
        travels.add(new PlanetTravel("Saturn", "Uranus", 70));
        travels.add(new PlanetTravel("Mercury", "Pluto", 45));
        /*try {
            response = client.getPlayerUniverse("660e963e5bc03660e963e5bc06");
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        travels = mapData(response.getUniverse());*/
        String start = "Earth";
        String end = "Pluto";
        PathInfo shortestPathInfo = findShortestPath(travels, start, end);
        System.out.println("Shortest path fuel: " + shortestPathInfo.totalFuel);
        System.out.println("Path: ");
        for (PlanetTravel travel : shortestPathInfo.path) {
            System.out.println(travel);
        }
        List<String> result = shortestPathInfoString(travels, start, end);
    }
    public static List<String> shortestPathInfoString(List<PlanetTravel> travels,String start, String end){
        PathInfo shortestPathInfo = findShortestPath(travels, start, end);
        List<String> resultString = new ArrayList<>();
        for (PlanetTravel travel : shortestPathInfo.path) {
            resultString.add(travel.getLandingPlanet());
        }
        System.out.println("========");
        System.out.println(resultString);
        return resultString;
    }
}