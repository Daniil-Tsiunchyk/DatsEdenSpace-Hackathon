package com.belarus.riga.classes;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PathInfo {
    private int totalFuel;
    private List<PlanetTravel> path;
}