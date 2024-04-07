package com.belarus.riga.scripts;
import com.belarus.riga.classes.PathInfo;
import com.belarus.riga.classes.PlanetFlagInfo;
import com.belarus.riga.classes.PlanetTravel;
import com.belarus.riga.classes.PlayerUniverseResponse;
import com.belarus.riga.client.TravelClient;
import com.belarus.riga.client.UniverseClient;

import java.util.List;
import java.util.Objects;

import static com.belarus.riga.scripts.PlanetTravelScript.*;
import static com.belarus.riga.scripts.SpaceGarbageScript.*;
public class MainScript {
    private static final String DEFAULT_PLANET = "Eden";
    private static final double CAPACITY_THRESHOLD = 0.50;
    private static final UniverseClient universeClient = new UniverseClient();
    private static final TravelClient travelClient = new TravelClient();
    public static void main(String[] args) {
        List<PlanetTravel> travels;
        List<PlanetFlagInfo> planetFlagInfoList;
        PlayerUniverseResponse response = getPlayerUniverse();
        travels = PlanetTravelScript.mapData(response.getUniverse());
        planetFlagInfoList = PlanetTravelScript.convertToFlagInfoList(travels);
        int errorCount = 0;
        String jsonPayload;
        while (true) {
            System.out.println("-------------------------------------");
            try {
                Thread.sleep(250);
                response = universeClient.getPlayerUniverse();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("77777777777");
            System.out.println(response.getShip().getPlanet().getGarbage());
            System.out.println("77777777777");
            travels = PlanetTravelScript.mapData(response.getUniverse());
            System.out.println("size " + planetFlagInfoList.size());
            List<PlanetFlagInfo> sortedClosestPlanet = PlanetTravelScript.findClosestPlanet(planetFlagInfoList, travels, response.getShip().getPlanet().getName());
            System.out.println("-0-0-0-0-0-0-0-0-0-0");
            for (PlanetFlagInfo p:
                    sortedClosestPlanet) {
                System.out.println(p);
            }
            System.out.println("-0-0-0-0-0-0-0-0-0-0");
            if (sortedClosestPlanet.isEmpty()) {
                System.out.println("Все планеты очищены");
                break;
            } else {
                System.out.println("Количество планет осталось: " + sortedClosestPlanet.size());
            }
            Integer[][] shipGarbage = parseShipGarbage(response.getShip());
            int capacity = countCapacity(shipGarbage);
            System.out.println(capacity);
            System.out.println(response.getShip().getCapacityY() * response.getShip().getCapacityX() * CAPACITY_THRESHOLD);
            if (capacity <= (response.getShip().getCapacityY() * response.getShip().getCapacityX() * CAPACITY_THRESHOLD)) {
                jsonPayload = shortestPathInfoString(travels, response.getShip().getPlanet().getName(), Objects.requireNonNull(getClosestPlanet(sortedClosestPlanet)).getNamePlanet());
                try {
                    boolean garbage = manageGarbage();

                    PlayerUniverseResponse anotherResponse = getPlayerUniverse();

                    if (garbage) {
                        if (anotherResponse.getShip().getPlanet().getGarbage().isEmpty()) {
                            errorCount = 0;
                            System.out.println("planet is clear");
                            markPlanet(planetFlagInfoList, anotherResponse.getShip().getPlanet().getName(), 3);
                        } else {
                            System.out.println(anotherResponse.getShip().getPlanet().getGarbage());
                            System.out.println("planet is not clear");
                            markPlanet(planetFlagInfoList, anotherResponse.getShip().getPlanet().getName(), 0);
                        }

                    } else {
                        errorCount++;
                        System.out.println(anotherResponse.getShip().getPlanet().getGarbage());
                        System.out.println("planet is 1 clear");


                            PathInfo shortestPathInfo = findShortestPath(travels, response.getShip().getPlanet().getName(), Objects.requireNonNull(getClosestPlanet(sortedClosestPlanet)).getNamePlanet());
                            for (PlanetTravel planetPath:
                                    shortestPathInfo.getPath()) {
                                markPlanet(planetFlagInfoList,planetPath.getLandingPlanet(),1);

                            }
                            markPlanet(planetFlagInfoList,shortestPathInfo.getPath().getFirst().getDeparturePlanet(), 1);
                        continue;
                    }
                } catch (Exception e) {
                    errorCount++;
                    markPlanet(planetFlagInfoList, response.getShip().getPlanet().getName(), 3);
                    e.printStackTrace();
                }
                if (errorCount >= 3) {

                    errorCount = 0;
                    jsonPayload = shortestPathInfoString(travels, response.getShip().getPlanet().getName(), DEFAULT_PLANET);
                    postTravel(jsonPayload);
                    continue;
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                jsonPayload = shortestPathInfoString(travels, response.getShip().getPlanet().getName(), DEFAULT_PLANET);
            }
            postTravel(jsonPayload);
            System.out.println("-------------------------------------");
        }

    }

    private static PlayerUniverseResponse getPlayerUniverse() {
        try {
            return MainScript.universeClient.getPlayerUniverse();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get player universe", e);
        }
    }

    public static PlanetFlagInfo getClosestPlanet(List<PlanetFlagInfo> sortedClosestPlanet) {
        for (PlanetFlagInfo planet : sortedClosestPlanet) {
            if (planet.getIsClear() == 0) {
                return planet;
            }
        }
        for (PlanetFlagInfo planet : sortedClosestPlanet) {
            if (planet.getIsClear() == 1) {
                return planet;
            }
        }
        return null;
    }

    private static void markPlanet(List<PlanetFlagInfo> planetFlagInfoList, String planetName, int flag) {
        for (PlanetFlagInfo planetFlagInfo : planetFlagInfoList) {
            if (planetFlagInfo.getNamePlanet().equals(planetName)) {
                planetFlagInfo.setIsClear(flag);
                System.out.println("Планета " + planetName + " значение: " + flag);
                break;
            }
        }
    }

    private static void postTravel(String jsonPayload) {
        try {
            MainScript.travelClient.postTravel(jsonPayload);
        } catch (Exception e) {
            throw new RuntimeException("Failed to post travel", e);
        }
    }
}