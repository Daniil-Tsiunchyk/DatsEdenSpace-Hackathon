package com.belarus.riga.scripts;

import com.belarus.riga.classes.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.*;
import java.util.stream.Collectors;

public class PlanetTravelScript {


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
            graph.putIfAbsent(travel.getDeparturePlanet(), new HashMap<>());
            graph.get(travel.getDeparturePlanet()).put(travel.getLandingPlanet(), travel.getFuel());
        }
        return graph;
    }

    public static String shortestPathInfoString(List<PlanetTravel> travels, String start, String end) {
        if (start.equals(end)) {
            throw new RuntimeException("Мы летим с" + start + " на " + end);
        }
        PathInfo shortestPathInfo = findShortestPath(travels, start, end);
        System.out.println("Shortest path fuel: " + shortestPathInfo.getTotalFuel());
        System.out.println("Path: ");
        for (PlanetTravel travel : shortestPathInfo.getPath()) {
            System.out.println(travel);
        }

        return mapData(shortestPathInfo);
    }

    public static String mapData(PathInfo shortestPathInfo) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> planetsRequest = shortestPathInfo.getPath()
                .stream()
                .map(PlanetTravel::getLandingPlanet)
                .collect(Collectors.toList());

        PlanetListRequest planetList = new PlanetListRequest(planetsRequest);

        try {
            return objectMapper.writeValueAsString(planetList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<PlanetFlagInfo> convertToFlagInfoList(List<PlanetTravel> planetTravels) {
        Map<String, Integer> planetFlags = getPlanetFlags(planetTravels);
        planetFlags.remove("Earth");
        planetFlags.remove("Eden");

        List<PlanetFlagInfo> flagInfoList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : planetFlags.entrySet()) {
            flagInfoList.add(new PlanetFlagInfo(entry.getKey(), 0));
        }

        return flagInfoList;
    }

    private static Map<String, Integer> getPlanetFlags(List<PlanetTravel> planetTravels) {
        Map<String, Integer> planetFlags = new HashMap<>();

        for (PlanetTravel travel : planetTravels) {
            String departurePlanet = travel.getDeparturePlanet();
            String landingPlanet = travel.getLandingPlanet();
            if (!planetFlags.containsKey(departurePlanet)) {
                planetFlags.put(departurePlanet, 0);
            }
            if (!planetFlags.containsKey(landingPlanet)) {
                planetFlags.put(landingPlanet, 0);
            }
        }
        return planetFlags;
    }

    public static List<PlanetFlagInfo> findClosestPlanet(List<PlanetFlagInfo> planetFlagInfoList, List<PlanetTravel> travels,
                                                         String namePlanet) {
        List<PlanetFlagInfo> closestPlanetsList = new ArrayList<>();
        for (PlanetFlagInfo planet :
                planetFlagInfoList) {
            if (planet.getIsClear()!=3 && !planet.getNamePlanet().equals(namePlanet)) {
                planet.setFuel(findShortestPath(travels, namePlanet, planet.getNamePlanet()).getTotalFuel());
                closestPlanetsList.add(planet);
            }
        }
        return closestPlanetsList.stream()
                .sorted(Comparator.comparingInt(PlanetFlagInfo::getFuel))
                .collect(Collectors.toList());
    }

}