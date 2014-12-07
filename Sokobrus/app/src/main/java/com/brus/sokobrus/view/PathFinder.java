package com.brus.sokobrus.view;

import com.brus.sokobrus.view.model.Maze;
import com.brus.sokobrus.view.model.MazeField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by brus on 11/9/2014.
 */
public class PathFinder {
    public static List<MazeField> findPath(Maze maze, MazeField start, MazeField end) {
        Map<MazeField, List<MazeField>> closedNodes = new HashMap<MazeField, List<MazeField>>();
        Map<MazeField, List<MazeField>> openNodes = new HashMap<MazeField, List<MazeField>>();
        openNodes.put(start, new ArrayList<MazeField>());
        List<List<MazeField>> foundPaths = new ArrayList<List<MazeField>>();

        checkMaze(closedNodes, openNodes, foundPaths, maze, end);

        return getShortestPath(foundPaths);
    }

    private static List<MazeField> getShortestPath(List<List<MazeField>> foundPaths) {
        List<MazeField> shortestPath = null;
        for (List<MazeField> path : foundPaths) {
            if (shortestPath == null || path.size() < shortestPath.size()) {
                shortestPath = path;
            }
        }
        return shortestPath;
    }

    private static void checkMaze(Map<MazeField, List<MazeField>> closedNodes, Map<MazeField, List<MazeField>> openNodes, List<List<MazeField>> foundPaths, Maze maze, MazeField end) {
        if (openNodes.keySet().iterator().hasNext()) {
            MazeField field = openNodes.keySet().iterator().next();
            List<MazeField> path = openNodes.get(field);
            openNodes.remove(field);
            closedNodes.put(field, path);

            checkDirection(closedNodes, openNodes, foundPaths, maze, field.getRow() + 1, field.getColumn(), path, end);
            checkDirection(closedNodes, openNodes, foundPaths, maze, field.getRow(), field.getColumn() - 1, path, end);
            checkDirection(closedNodes, openNodes, foundPaths, maze, field.getRow() - 1, field.getColumn(), path, end);
            checkDirection(closedNodes, openNodes, foundPaths, maze, field.getRow(), field.getColumn() + 1, path, end);

            checkMaze(closedNodes, openNodes, foundPaths, maze, end);
        }
    }

    private static void checkDirection(Map<MazeField, List<MazeField>> closedNodes, Map<MazeField, List<MazeField>> openNodes, List<List<MazeField>> foundPaths, Maze maze, int row, int column, List<MazeField> path, MazeField end) {
        if (maze.getMazeFields().get(row).containsKey(column)) {
            MazeField field = maze.getMazeFields().get(row).get(column);
            if (field == end) {
                foundPaths.add(createNewPath(path, field));
                return;
            }
            if (!closedNodes.containsKey(field) && field.isFloor() && !field.isBox()) {
                openNodes.put(field, createNewPath(path, field));
            }
        }
    }

    private static List<MazeField> createNewPath(List<MazeField> path, MazeField field) {
        List<MazeField> newPath = new ArrayList<MazeField>(path);
        newPath.add(field);
        return newPath;
    }
}
