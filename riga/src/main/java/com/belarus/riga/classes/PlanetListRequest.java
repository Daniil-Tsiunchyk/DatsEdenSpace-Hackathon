package com.belarus.riga.classes;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PlanetListRequest {

    private List<String> planets;
}
