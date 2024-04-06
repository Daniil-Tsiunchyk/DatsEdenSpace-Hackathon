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
    private static final double CAPACITY_THRESHOLD = 0.35;
    private static final UniverseClient universeClient = new UniverseClient();
    private static final TravelClient travelClient = new TravelClient();

    public static void main(String[] args) {
        List<PlanetTravel> travels;
        List<PlanetFlagInfo> planetFlagInfoList;

        PlayerUniverseResponse response = getPlayerUniverse();
        travels = PlanetTravelScript.mapData(response.getUniverse());
        planetFlagInfoList = PlanetTravelScript.convertToFlagInfoList(travels);
        while (true) {
            System.out.println("-------------------------------------");
            try {
                response = universeClient.getPlayerUniverse();
                System.out.println(response);
                Thread.sleep(300);
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (response.getShip().getPlanet().getGarbage().isEmpty()) {
                markPlanetAsClean(planetFlagInfoList, response.getShip().getPlanet().getName());
            }

            travels = PlanetTravelScript.mapData(response.getUniverse());
            List<PlanetFlagInfo> sortedClosestPlanet = PlanetTravelScript.findClosestPlanet(planetFlagInfoList, travels, response.getShip().getPlanet().getName());
            if (sortedClosestPlanet.isEmpty()) {
                System.out.println("Все планеты очищены");
                break;
            }

            Integer[][] shipGarbage = parseShipGarbage(response.getShip());
            int capacity = countCapacity(shipGarbage);
            System.out.println(capacity);
            System.out.println(response.getShip().getCapacityY() * response.getShip().getCapacityX() * CAPACITY_THRESHOLD);
            String jsonPayload;
            if (capacity <= (response.getShip().getCapacityY() * response.getShip().getCapacityX() * CAPACITY_THRESHOLD)) {

                jsonPayload = shortestPathInfoString(travels, response.getShip().getPlanet().getName(), sortedClosestPlanet.getFirst().getNamePlanet());

                //todo Тетрис
                try {
                    if (!response.getShip().getPlanet().getGarbage().isEmpty()) {
                        manageGarbage();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //todo Тетрис
            } else {
                jsonPayload = shortestPathInfoString(travels, response.getShip().getPlanet().getName(), DEFAULT_PLANET);
            }
            postTravel(jsonPayload);
            System.out.println("-------------------------------------");
        }
    }

    private static PlayerUniverseResponse getPlayerUniverse() {
        try {
            PlayerUniverseResponse response = MainScript.universeClient.getPlayerUniverse();
            System.out.println(response);
            Thread.sleep(250);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get player universe", e);
        }
    }

    private static void markPlanetAsClean(List<PlanetFlagInfo> planetFlagInfoList, String planetName) {
        for (PlanetFlagInfo planetFlagInfo : planetFlagInfoList) {
            if (planetFlagInfo.getNamePlanet().equals(planetName)) {
                planetFlagInfo.setClear(true);
                System.out.println("Планета " + planetName + " очищена");
                break;
            }
        }
    }

    private static void postTravel(String jsonPayload) {
        try {
            MainScript.travelClient.postTravel(jsonPayload);
            Thread.sleep(250);
        } catch (Exception e) {
            throw new RuntimeException("Failed to post travel", e);
        }
    }
}
