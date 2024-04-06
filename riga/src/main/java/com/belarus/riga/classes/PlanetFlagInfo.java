package com.belarus.riga.classes;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class PlanetFlagInfo {
    private String namePlanet;
    private boolean isClear;
    public int fuel;

    public PlanetFlagInfo(String namePlanet, boolean isClear) {
        this.namePlanet = namePlanet;
        this.isClear = isClear;
    }
}
