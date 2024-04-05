package com.belarus.riga.legacy;

import com.belarus.riga.classes.PlayerUniverseResponse;
import com.belarus.riga.client.UniverseClient;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.ext.JGraphXAdapter;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;

import javax.swing.*;
import java.util.List;

public class GraphVisualizer {

    public static void buildAndShowGraph(PlayerUniverseResponse response) {
        JGraphXAdapter<String, DefaultWeightedEdge> graphAdapter = getAdapter(response);
        mxGraphComponent graphComponent = new mxGraphComponent(graphAdapter);

        graphComponent.zoomTo(0.25, true); // Масштаб на 25% от исходного размера
        graphComponent.zoomAndCenter();
        mxCircleLayout layout = new mxCircleLayout(graphAdapter);

        layout.execute(graphAdapter.getDefaultParent());

        JFrame frame = new JFrame("Universe Graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(graphComponent);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    private static JGraphXAdapter<String, DefaultWeightedEdge> getAdapter(PlayerUniverseResponse response) {
        Graph<String, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        if (response.getUniverse() != null) {
            for (List<Object> link : response.getUniverse()) {
                String origin = (String) link.get(0);
                String destination = (String) link.get(1);
                int weight = (Integer) link.get(2);

                graph.addVertex(origin);
                graph.addVertex(destination);
                DefaultWeightedEdge edge = graph.addEdge(origin, destination);
                if (edge != null) {
                    graph.setEdgeWeight(edge, weight);
                }
            }
        }

        return new JGraphXAdapter<>(graph);
    }

    public static void main(String[] args) {
        PlayerUniverseResponse response = new PlayerUniverseResponse();
        UniverseClient client = new UniverseClient();
        try {
            response = client.getPlayerUniverse("660e963e5bc03660e963e5bc06");
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        buildAndShowGraph(response);
    }
}