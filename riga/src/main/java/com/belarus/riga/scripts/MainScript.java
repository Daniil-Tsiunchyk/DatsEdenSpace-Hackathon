package com.belarus.riga.scripts;

import com.belarus.riga.classes.PlanetFlagInfo;
import com.belarus.riga.classes.PlanetTravel;
import com.belarus.riga.classes.PlayerUniverseResponse;
import com.belarus.riga.client.TravelClient;
import com.belarus.riga.client.UniverseClient;
import java.util.List;

import static com.belarus.riga.scripts.PlanetTravelScript.*;

public class MainScript {
    public static void main(String[] args) {
        List<PlanetTravel> travels;
        List<PlanetFlagInfo> planetFlagInfoList;
        PlayerUniverseResponse response = new PlayerUniverseResponse();
        TravelClient travelClient = new TravelClient();
        UniverseClient client = new UniverseClient();

        //Запрос на инфу общую
        try {
            response = client.getPlayerUniverse();
            System.out.println(response);
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        travels = PlanetTravelScript.mapData(response.getUniverse());
        planetFlagInfoList = PlanetTravelScript.convertToFlagInfoList(travels);

        while(true){
            try {
                response = client.getPlayerUniverse();
                System.out.println(response);
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(response.getShip().getPlanet().getGarbage().isEmpty()){
                for (PlanetFlagInfo planetFlagInfo : planetFlagInfoList) {
                    if (planetFlagInfo.getNamePlanet().equals(response.getShip().getPlanet().getName())) {
                        planetFlagInfo.setClear(true);
                        break;
                    }
                }
            }
            //todo Заменить Earth на реальное
            travels = PlanetTravelScript.mapData(response.getUniverse());

            List<PlanetFlagInfo> sortedClosestPlanet = PlanetTravelScript.findClosestPlanet(planetFlagInfoList,travels,response.getShip().getPlanet().getName());
            if(sortedClosestPlanet.isEmpty()){
                System.out.println("Все планеты очищены");
                break;
            }
            //todo Проверка на Copasity
            //
            //todo Проверка на Copasity
            if(true){
                String jsonPayload = shortestPathInfoString(travels, response.getShip().getPlanet().getName(), sortedClosestPlanet.getFirst().getNamePlanet());
                //todo Затестить
                try {
                    travelClient.postTravel(jsonPayload);
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                //todo Тетрис
                //
                //todo Тетрис

                //todo Оставить флаг или нет
            }
            else {
                String jsonPayload = shortestPathInfoString(travels, response.getShip().getPlanet().getName(),"Eden" );
                try {
                    travelClient.postTravel(jsonPayload);
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        }

    }
}
