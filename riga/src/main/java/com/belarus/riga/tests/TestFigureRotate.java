package com.belarus.riga.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.belarus.riga.scripts.SpaceGarbageScript.rotateFigure;


public class TestFigureRotate {


    private static void printFigure(List<List<Integer>> figure) {
        int width = figure.stream().max(Comparator.comparingInt(List::getFirst)).get().getFirst() + 1;
        int height = figure.stream().max(Comparator.comparingInt(block -> block.get(1))).get().get(1) + 1;
        int[][] displayArray = new int[height][width];

        figure.forEach(block -> displayArray[block.get(1)][block.getFirst()] = 1);

        for (int[] row : displayArray) {
            for (int cell : row) {
                System.out.print(cell == 1 ? "1 " : "0 ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void testRotation(List<List<Integer>> figure, int angle) {
        System.out.println("Original Figure:");
        printFigure(figure);

        List<List<Integer>> rotatedFigure = rotateFigure(figure, angle);
        System.out.println("Figure after " + angle + " degree rotation:");
        printFigure(rotatedFigure);
    }

    public static void main(String[] args) {
        List<List<Integer>> figure = new ArrayList<>();

        //Example №1
        // figure.add(Arrays.asList(0, 0));
        // figure.add(Arrays.asList(0, 1));
        // figure.add(Arrays.asList(0, 2));
        // figure.add(Arrays.asList(1, 2));

        //Example №2
        figure.add(Arrays.asList(0, 3));
        figure.add(Arrays.asList(0, 2));
        figure.add(Arrays.asList(0, 1));
        figure.add(Arrays.asList(0, 0));
        figure.add(Arrays.asList(1, 3));
        figure.add(Arrays.asList(1, 0));
        figure.add(Arrays.asList(2, 3));
        figure.add(Arrays.asList(2, 2));
        figure.add(Arrays.asList(2, 1));
        figure.add(Arrays.asList(2, 0));
        figure.add(Arrays.asList(3, 1));
        printFigure(figure);

        testRotation(figure, 0);
        testRotation(figure, 90);
        testRotation(figure, 180);
        testRotation(figure, 270);
    }
}
