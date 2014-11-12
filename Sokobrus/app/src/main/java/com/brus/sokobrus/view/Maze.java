package com.brus.sokobrus.view;

import android.app.Activity;
import android.os.SystemClock;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by brus on 11/8/2014.
 */
public class Maze {

    private Map<Integer, Map<Integer, MazeFieldView>> mazeFields;
    private int maxColumnNumber;
    private int maxRowNumber;
    private int fieldSize;
    MazeFieldView currentWorkerPosition;

    public void Load(Activity activity, RelativeLayout frame, String level) {
        mazeFields = new HashMap<Integer, Map<Integer, MazeFieldView>>();
        maxColumnNumber = 0;
        maxRowNumber = 1; // Start from 1 and increment for every new row

        int playerRow = Integer.parseInt(level.substring(0, 2), 16) - 1; // value in file is 1 based
        int playerColumn = Integer.parseInt(level.substring(2, 4), 16) - 1; // value in file is 1 based

        int row = 0;
        int column = 0;
        boolean anythingInRow = false;
        MazeFieldView mazeFieldView = null;
        mazeFields.put(row, new HashMap<Integer, MazeFieldView>());
        for (int i = 4; i < level.length(); i++) {
            switch (level.charAt(i)) {
                case '0':
                    mazeFieldView = new MazeFieldView(activity, row, column, false, false, false, anythingInRow);
                    mazeFields.get(row).put(column, mazeFieldView);
                    frame.addView(mazeFieldView);
                    column++;
                    break;
                case '1':
                    mazeFieldView = new MazeFieldView(activity, row, column, false, false, true, true);
                    mazeFields.get(row).put(column, mazeFieldView);
                    frame.addView(mazeFieldView);
                    column++;
                    anythingInRow = true;
                    break;
                case '2':
                    mazeFieldView = new MazeFieldView(activity, row, column, false, true, false, false);
                    mazeFields.get(row).put(column, mazeFieldView);
                    frame.addView(mazeFieldView);
                    column++;
                    anythingInRow = true;
                    break;
                case '3':
                    mazeFieldView = new MazeFieldView(activity, row, column, true, false, false, true);
                    mazeFields.get(row).put(column, mazeFieldView);
                    frame.addView(mazeFieldView);
                    column++;
                    anythingInRow = true;
                    break;
                case 'f':
                    maxColumnNumber = Math.max(maxColumnNumber, column + 1);
                    maxRowNumber++;

                    row++;
                    column = 0;
                    anythingInRow = false;
                    mazeFields.put(row, new HashMap<Integer, MazeFieldView>());
                    break;
            }
        }

        currentWorkerPosition = mazeFields.get(playerRow).get(playerColumn);
        currentWorkerPosition.setWorker(true);

        initializeMazeUI(frame);
    }

    public void initializeMazeUI(RelativeLayout frame) {
        float fieldWidth = (frame.getWidth()) / maxColumnNumber;
        float fieldHeight = (frame.getHeight()) / maxRowNumber;

        fieldSize = (int) Math.min(fieldWidth, fieldHeight);

        for (Map<Integer, MazeFieldView> row : mazeFields.values()) {
            for (MazeFieldView field : row.values()) {
                field.initializeUI(fieldSize);
            }
        }
    }

    public boolean moveBox(RelativeLayout frame, float x, float y){
        int[] locationOnScreen = new int[2];
        frame.getLocationOnScreen(locationOnScreen);

        int newPlayerRow = (int) ((y - locationOnScreen[1]) / fieldSize);
        int newPlayerColumn = (int) ((x - locationOnScreen[0]) / fieldSize);

        // Don't move if already there
        if (newPlayerRow == currentWorkerPosition.getRow() && newPlayerColumn == currentWorkerPosition.getColumn()){
            return true;
        }

        // New position must be one field from the current position
        if (Math.abs(newPlayerRow - currentWorkerPosition.getRow()) + Math.abs(newPlayerColumn - currentWorkerPosition.getColumn()) > 1){
            // Invalid situation, moved more than one place, stop moving box
            return false;
        }

        // New position is empty - move player
        MazeFieldView newPlayerPosition = mazeFields.get(newPlayerRow).get(newPlayerColumn);
        FieldState newPlayerFieldState = newPlayerPosition.getFieldState();
        if (newPlayerFieldState == FieldState.FLOOR || newPlayerFieldState == FieldState.DOCK){
            moveOnePosition(newPlayerPosition);
            return true;
        }

        // New position has box - try to move the player and the box
        if (newPlayerFieldState == FieldState.BOX || newPlayerFieldState == FieldState.BOX_DOCKED){
            // move right
            if (newPlayerRow == currentWorkerPosition.getRow() && newPlayerColumn == currentWorkerPosition.getColumn() + 1){
                MazeFieldView newBoxPosition = mazeFields.get(newPlayerRow).get(newPlayerColumn + 1);
                FieldState newBoxFieldState = newBoxPosition.getFieldState();
                if (newBoxFieldState == FieldState.DOCK || newBoxFieldState == FieldState.FLOOR){
                    moveBoxOnePosition(newPlayerPosition, newBoxPosition);
                }
            }
            // move left
            if (newPlayerRow == currentWorkerPosition.getRow() && newPlayerColumn == currentWorkerPosition.getColumn() - 1){
                MazeFieldView newBoxPosition = mazeFields.get(newPlayerRow).get(newPlayerColumn - 1);
                FieldState newBoxFieldState = newBoxPosition.getFieldState();
                if (newBoxFieldState == FieldState.DOCK || newBoxFieldState == FieldState.FLOOR){
                    moveBoxOnePosition(newPlayerPosition, newBoxPosition);
                }
            }
            // move down
            if (newPlayerRow == currentWorkerPosition.getRow() + 1 && newPlayerColumn == currentWorkerPosition.getColumn()){
                MazeFieldView newBoxPosition = mazeFields.get(newPlayerRow + 1).get(newPlayerColumn);
                FieldState newBoxFieldState = newBoxPosition.getFieldState();
                if (newBoxFieldState == FieldState.DOCK || newBoxFieldState == FieldState.FLOOR){
                    moveBoxOnePosition(newPlayerPosition, newBoxPosition);
                }
            }
            // move up
            if (newPlayerRow == currentWorkerPosition.getRow() - 1 && newPlayerColumn == currentWorkerPosition.getColumn()){
                MazeFieldView newBoxPosition = mazeFields.get(newPlayerRow - 1).get(newPlayerColumn);
                FieldState newBoxFieldState = newBoxPosition.getFieldState();
                if (newBoxFieldState == FieldState.DOCK || newBoxFieldState == FieldState.FLOOR){
                    moveBoxOnePosition(newPlayerPosition, newBoxPosition);
                }
            }
        }
        return false;
    }

    public void gotoCoordinate(RelativeLayout frame, float x, float y) {
        int[] locationOnScreen = new int[2];
        frame.getLocationOnScreen(locationOnScreen);

        int newPlayerRow = (int) ((y - locationOnScreen[1]) / fieldSize);
        int newPlayerColumn = (int) ((x - locationOnScreen[0]) / fieldSize);

        // Don't move if already there
        if (newPlayerRow == currentWorkerPosition.getRow() && newPlayerColumn == currentWorkerPosition.getColumn()){
            return;
        }

        // Don't move if destination field does not exist
        if (!mazeFields.containsKey(newPlayerRow) ||
                !mazeFields.get(newPlayerRow).containsKey(newPlayerColumn) ||
                !mazeFields.get(newPlayerRow).get(newPlayerColumn).isFloor()) {
            return;
        }

        final List<MazeFieldView> path = PathFinder.findPath(this, currentWorkerPosition, mazeFields.get(newPlayerRow).get(newPlayerColumn));
        if (path == null) {
            return;
        }

        Thread t = new Thread() {
            public void run() {
                while (path.size() > 0) {
                    moveOnePosition(path.remove(0));
                    SystemClock.sleep(100);
                }
            }
        };
        t.start();
    }

    private void moveOnePosition(MazeFieldView newPosition) {
        currentWorkerPosition.setWorker(false);
        currentWorkerPosition.postInvalidate();

        currentWorkerPosition = newPosition;

        newPosition.setWorker(true);
        newPosition.postInvalidate();
    }

    private void moveBoxOnePosition(MazeFieldView oldPosition, MazeFieldView newPosition){
        oldPosition.setBox(false);
        oldPosition.postInvalidate();

        newPosition.setBox(true);
        newPosition.postInvalidate();
    }

    public Map<Integer, Map<Integer, MazeFieldView>> getMazeFields() {
        return mazeFields;
    }

    public boolean levelComplete() {
        for (Map<Integer, MazeFieldView> row : mazeFields.values()) {
            for (MazeFieldView field : row.values())
                if (field.getFieldState() == FieldState.DOCK || field.getFieldState() == FieldState.BOX){
                    return false;
                }
        }
        return true;
    }
}
