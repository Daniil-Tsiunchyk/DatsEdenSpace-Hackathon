package com.belarus.riga.classes;

import lombok.Data;

@Data
public class PlanetFlagInfo {
    private String namePlanet;
    private int isClear;
    public int fuel=0;

    public PlanetFlagInfo(String namePlanet, int isClear) {
        this.namePlanet = namePlanet;
        this.isClear = isClear;
    }
}
