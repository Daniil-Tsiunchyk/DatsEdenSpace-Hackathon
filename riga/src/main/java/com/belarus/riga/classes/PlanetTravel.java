package com.belarus.riga.classes;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlanetTravel {
    private String departurePlanet;
    private String landingPlanet;
    private int fuel;

    @Override
    public String toString() {
        return departurePlanet + " -> " + landingPlanet + " (fuel: " + fuel + ")";
    }
}
