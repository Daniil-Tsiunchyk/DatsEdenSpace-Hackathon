package com.belarus.riga.classes;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PlayerCollectResponse {

    private Map<String, List<List<Integer>>> garbage;
    private List<String> leaved;
}
