package com.belarus.riga.classes;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
        private int capacityX;
        private int capacityY;
        private int fuelUsed;
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
