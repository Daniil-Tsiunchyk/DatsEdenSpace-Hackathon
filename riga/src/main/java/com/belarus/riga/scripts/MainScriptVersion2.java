package com.belarus.riga.scripts;

import com.belarus.riga.classes.*;
import com.belarus.riga.client.TravelClient;
import com.belarus.riga.client.UniverseClient;

import java.util.List;
import java.util.Objects;

import static com.belarus.riga.scripts.PlanetTravelScript.findShortestPath;
import static com.belarus.riga.scripts.PlanetTravelScript.shortestPathInfoString;
import static com.belarus.riga.scripts.SpaceGarbageScript.*;

public class MainScriptVersion2 {
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
        String jsonPayload;
        if( !response.getShip().getPlanet().getName().equals(DEFAULT_PLANET)){
            jsonPayload = shortestPathInfoString(travels, response.getShip().getPlanet().getName(), DEFAULT_PLANET);
            try {
                Thread.sleep(200);
                postTravel(jsonPayload);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


       // int errorCount = 0;
        int i=1;

        for (PlanetFlagInfo currentPlanet:
             planetFlagInfoList) {
            System.out.println("==============================================================="+currentPlanet.getNamePlanet()+" " +i+" из "+planetFlagInfoList.size()+"==============================================================");
            while (true) {

                System.out.println("-------------------------------------");
                try {
                    Thread.sleep(100);
                    response = universeClient.getPlayerUniverse();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                travels = PlanetTravelScript.mapData(response.getUniverse());
                //List<PlanetFlagInfo> sortedClosestPlanet = PlanetTravelScript.findClosestPlanet(planetFlagInfoList, travels, response.getShip().getPlanet().getName());




                //Going to current Planet
                jsonPayload = shortestPathInfoString(travels, response.getShip().getPlanet().getName(), currentPlanet.getNamePlanet());
                try {
                    Thread.sleep(100);
                    postTravel(jsonPayload);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }



                boolean garbage = false;
                  boolean isCurrentPlanetClear = false;
                    try {
                        garbage = manageGarbage();
                    } catch (Exception e) {
                        PathInfo shortestPathInfo = findShortestPath(travels, response.getShip().getPlanet().getName(), currentPlanet.getNamePlanet());
                        for (PlanetTravel planetPath:
                             shortestPathInfo.getPath()) {
                            markPlanet(planetFlagInfoList,planetPath.getLandingPlanet(),1);

                        }
                        markPlanet(planetFlagInfoList,shortestPathInfo.getPath().getFirst().getDeparturePlanet(), 1);

                        isCurrentPlanetClear=true;

                        //markPlanet(planetFlagInfoList, currentPlanet.getNamePlanet(), 3);
                        e.printStackTrace();
                    }
                try {
                    Thread.sleep(200);

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                PlayerUniverseResponse anotherResponse = getPlayerUniverse();

                        if (garbage) {
                            if (anotherResponse.getShip().getPlanet().getGarbage().isEmpty()) {
                                System.out.println("planet is clear");
                                isCurrentPlanetClear=true;
                                markPlanet(planetFlagInfoList, anotherResponse.getShip().getPlanet().getName(), 3);
                            } else {
                                System.out.println(anotherResponse.getShip().getPlanet().getGarbage());
                                System.out.println("planet is not clear");
                                markPlanet(planetFlagInfoList, anotherResponse.getShip().getPlanet().getName(), 0);
                            }

                        } else {

                            System.out.println(anotherResponse.getShip().getPlanet().getGarbage());
                            System.out.println("WARNING! ERROR! Planet NOT clear");
                            isCurrentPlanetClear=true;
                            markPlanet(planetFlagInfoList, anotherResponse.getShip().getPlanet().getName(), 1);

                        }


                    Integer[][] shipGarbage = parseShipGarbage(anotherResponse.getShip());
                    int capacity = countCapacity(shipGarbage);
                    System.out.println("Capaciry: "+capacity +" из 88");
                    /*if (isCurrentPlanetClear && capacity <= (response.getShip().getCapacityY() * response.getShip().getCapacityX() * CAPACITY_THRESHOLD)) {
                        System.out.println("Мы летим на след планету!");
                        break;
                    }*/


                    //Going to Eden
                    try {
                        Thread.sleep(100);
                        jsonPayload = shortestPathInfoString(travels, currentPlanet.getNamePlanet(), DEFAULT_PLANET);
                        postTravel(jsonPayload);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }


                if(isCurrentPlanetClear){
                    break;
                }




                System.out.println("-------------------------------------");
            }
            System.out.println("==============================="+currentPlanet.getNamePlanet()+"===============================");
            i++;

        }



    }

    private static PlayerUniverseResponse getPlayerUniverse() {
        try {
            return MainScriptVersion2.universeClient.getPlayerUniverse();
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
            MainScriptVersion2.travelClient.postTravel(jsonPayload);
        } catch (Exception e) {
            throw new RuntimeException("Failed to post travel", e);
        }
    }
}
