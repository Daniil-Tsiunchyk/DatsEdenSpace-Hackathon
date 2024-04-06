package com.belarus.riga.classes;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PlayerUniverseResponse {
    private String name;
    private String roundName; //Не учитывается
    private Integer roundEndIn; //Не учитывается
    private Ship ship;
    private List<List<Object>> universe;

    @Data
    public static class Ship {
        private Integer capacityX;
        private Integer capacityY;
        private Integer fuelUsed;
        private Map<String, List<List<Integer>>> garbage;
        private Planet planet;
    }

    @Data
    public static class Planet {
        private String name;
        private Map<String, List<List<Integer>>> garbage;
    }

    private Integer attempt; //Не учитывается
}
