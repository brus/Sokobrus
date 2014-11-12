package com.brus.sokobrus.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by brus on 11/9/2014.
 */
public class PathFinder {
    public static List<MazeFieldView> findPath(Maze maze, MazeFieldView start, MazeFieldView end) {
        Map<MazeFieldView, List<MazeFieldView>> closedNodes = new HashMap<MazeFieldView, List<MazeFieldView>>();
        Map<MazeFieldView, List<MazeFieldView>> openNodes = new HashMap<MazeFieldView, List<MazeFieldView>>();
        openNodes.put(start, createNewPath(new ArrayList<MazeFieldView>(), start));
        List<List<MazeFieldView>> foundPaths = new ArrayList<List<MazeFieldView>>();

        checkMaze(closedNodes, openNodes, foundPaths, maze, end);

        return getShortestPath(foundPaths);
    }

    private static List<MazeFieldView> getShortestPath(List<List<MazeFieldView>> foundPaths) {
        List<MazeFieldView> shortestPath = null;
        for (List<MazeFieldView> path : foundPaths) {
            if (shortestPath == null || path.size() < shortestPath.size()) {
                shortestPath = path;
            }
        }
        return shortestPath;
    }

    private static void checkMaze(Map<MazeFieldView, List<MazeFieldView>> closedNodes, Map<MazeFieldView, List<MazeFieldView>> openNodes, List<List<MazeFieldView>> foundPaths, Maze maze, MazeFieldView end) {
        if (openNodes.keySet().iterator().hasNext()) {
            MazeFieldView field = openNodes.keySet().iterator().next();
            List<MazeFieldView> path = openNodes.get(field);
            openNodes.remove(field);
            closedNodes.put(field, path);

            checkDirection(closedNodes, openNodes, foundPaths, maze, field.getRow() + 1, field.getColumn(), path, end);
            checkDirection(closedNodes, openNodes, foundPaths, maze, field.getRow(), field.getColumn() - 1, path, end);
            checkDirection(closedNodes, openNodes, foundPaths, maze, field.getRow() - 1, field.getColumn(), path, end);
            checkDirection(closedNodes, openNodes, foundPaths, maze, field.getRow(), field.getColumn() + 1, path, end);

            checkMaze(closedNodes, openNodes, foundPaths, maze, end);
        }
    }

    private static void checkDirection(Map<MazeFieldView, List<MazeFieldView>> closedNodes, Map<MazeFieldView, List<MazeFieldView>> openNodes, List<List<MazeFieldView>> foundPaths, Maze maze, int row, int column, List<MazeFieldView> path, MazeFieldView end) {
        if (maze.getMazeFields().get(row).containsKey(column)) {
            MazeFieldView field = maze.getMazeFields().get(row).get(column);
            if (field == end) {
                foundPaths.add(createNewPath(path, field));
                return;
            }
            if (!closedNodes.containsKey(field) && field.isFloor() && !field.isBox()) {
                openNodes.put(field, createNewPath(path, field));
            }
        }
    }

    private static List<MazeFieldView> createNewPath(List<MazeFieldView> path, MazeFieldView field) {
        List<MazeFieldView> newPath = new ArrayList<MazeFieldView>(path);
        newPath.add(field);
        return newPath;
    }
}
