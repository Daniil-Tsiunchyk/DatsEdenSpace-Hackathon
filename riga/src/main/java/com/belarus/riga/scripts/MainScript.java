package com.belarus.riga.scripts;

import com.belarus.riga.classes.PlanetFlagInfo;
import com.belarus.riga.classes.PlanetTravel;
import com.belarus.riga.classes.PlayerUniverseResponse;
import com.belarus.riga.client.TravelClient;
import com.belarus.riga.client.UniverseClient;

import java.util.List;

import static com.belarus.riga.scripts.PlanetTravelScript.*;
import static com.belarus.riga.scripts.SpaceGarbageScript.*;


public class MainScript {
    private static final String DEFAULT_PLANET = "Eden";
    private static final double CAPACITY_THRESHOLD = 0.5;
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
                    if (errorCount >= 3) {

                        errorCount = 0;
                        jsonPayload = shortestPathInfoString(travels, response.getShip().getPlanet().getName(), DEFAULT_PLANET);
                        postTravel(jsonPayload);
                        continue;
                    }

                    travels = PlanetTravelScript.mapData(response.getUniverse());
                    System.out.println("size "+planetFlagInfoList.size());
                    List<PlanetFlagInfo> sortedClosestPlanet = PlanetTravelScript.findClosestPlanet(planetFlagInfoList, travels, response.getShip().getPlanet().getName());
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

                        jsonPayload = shortestPathInfoString(travels, response.getShip().getPlanet().getName(),getClosestPlanet(sortedClosestPlanet).getNamePlanet());


                        //todo Тетрис
                        try {
                            System.out.println("Мы делаем тетрис");
                            if (!manageGarbage()) {
                                errorCount++;
                                markPlanet(planetFlagInfoList, response.getShip().getPlanet().getName(), 1);
                            } else {
                                errorCount = 0;
                                System.out.println("planet is clear");
                                if (response.getShip().getPlanet().getGarbage().isEmpty()) {
                                    markPlanet(planetFlagInfoList, response.getShip().getPlanet().getName(), 3);
                                }
                                else{
                                    markPlanet(planetFlagInfoList, response.getShip().getPlanet().getName(), 0);
                                }
                            }


                        } catch (Exception e) {
                            errorCount++;
                            markPlanet(planetFlagInfoList, response.getShip().getPlanet().getName(), 1);
                            e.printStackTrace();
                        }
                        //todo Тетрис
                    } else {
                        jsonPayload = shortestPathInfoString(travels, response.getShip().getPlanet().getName(), DEFAULT_PLANET);
                    }
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
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
            if (planet.getIsClear() == 0 ) {
                return planet;
            }
        }
        for (PlanetFlagInfo planet : sortedClosestPlanet) {
            if (planet.getIsClear() == 1 ) {
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
