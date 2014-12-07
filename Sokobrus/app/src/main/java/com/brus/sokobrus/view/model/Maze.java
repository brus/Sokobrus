package com.brus.sokobrus.view.model;

import android.app.Activity;
import android.os.SystemClock;
import android.widget.RelativeLayout;

import com.brus.sokobrus.Helper;
import com.brus.sokobrus.view.PathFinder;
import com.brus.sokobrus.view.components.MazeFieldView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by brus on 11/8/2014.
 */
public class Maze implements Serializable{

    private Map<Integer, Map<Integer, MazeField>> mazeFields;
    private List<Move> moves;
    private int maxColumnNumber;
    private int maxRowNumber;
    private int fieldSize;
    MazeField currentWorkerPosition;

    public void Load(Activity activity, RelativeLayout frame, String level) {
        mazeFields = new HashMap<Integer, Map<Integer, MazeField>>();
        moves = new ArrayList<Move>();
        maxColumnNumber = 0;
        maxRowNumber = 1; // Start from 1 and increment for every new row

        int playerRow = Integer.parseInt(level.substring(0, 2), 16) - 1; // value in file is 1 based
        int playerColumn = Integer.parseInt(level.substring(2, 4), 16) - 1; // value in file is 1 based

        int row = 0;
        int column = 0;
        boolean anythingInRow = false;
        MazeFieldView mazeFieldView = null;
        mazeFields.put(row, new HashMap<Integer, MazeField>());
        for (int i = 4; i < level.length(); i++) {
            switch (level.charAt(i)) {
                case '0':
                    MazeField mazeField = new MazeField(row, column, false, false, false, anythingInRow);
                    mazeFieldView = new MazeFieldView(activity, mazeField);
                    mazeFields.get(row).put(column, mazeField);
                    frame.addView(mazeFieldView);
                    column++;
                    break;
                case '1':
                    mazeField = new MazeField(row, column, false, false, true, true);
                    mazeFieldView = new MazeFieldView(activity, mazeField);
                    mazeFields.get(row).put(column, mazeField);
                    frame.addView(mazeFieldView);
                    column++;
                    anythingInRow = true;
                    break;
                case '2':
                    mazeField = new MazeField(row, column, false, true, false, false);
                    mazeFieldView = new MazeFieldView(activity, mazeField);
                    mazeFields.get(row).put(column, mazeField);
                    frame.addView(mazeFieldView);
                    column++;
                    anythingInRow = true;
                    break;
                case '3':
                    mazeField = new MazeField(row, column, true, false, false, true);
                    mazeFieldView = new MazeFieldView(activity, mazeField);
                    mazeFields.get(row).put(column, mazeField);
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
                    mazeFields.put(row, new HashMap<Integer, MazeField>());
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

        for (int i = 0; i < frame.getChildCount(); i++) {
            ((MazeFieldView)frame.getChildAt(i)).initializeUI(fieldSize);
        }
    }

    public void initializeAfterSerialization(Activity activity, RelativeLayout frame) {
        for (Map<Integer, MazeField> row : mazeFields.values()) {
            for (MazeField mazeField : row.values()) {
                MazeFieldView view = new MazeFieldView(activity, mazeField);
                frame.addView(view);
            }
        }

        initializeMazeUI(frame);
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
        MazeField newPlayerPosition = mazeFields.get(newPlayerRow).get(newPlayerColumn);
        FieldState newPlayerFieldState = newPlayerPosition.getFieldState();
        if (newPlayerFieldState == FieldState.FLOOR || newPlayerFieldState == FieldState.DOCK){
            moveWorkerOnePosition(newPlayerPosition, frame);
            return true;
        }

        // New position has box - try to move the player and the box
        if (newPlayerFieldState == FieldState.BOX || newPlayerFieldState == FieldState.BOX_DOCKED){
            // move right
            if (newPlayerRow == currentWorkerPosition.getRow() && newPlayerColumn == currentWorkerPosition.getColumn() + 1){
                MazeField newBoxPosition = mazeFields.get(newPlayerRow).get(newPlayerColumn + 1);
                FieldState newBoxFieldState = newBoxPosition.getFieldState();
                if (newBoxFieldState == FieldState.DOCK || newBoxFieldState == FieldState.FLOOR){
                    moveBoxOnePosition(newPlayerPosition, newBoxPosition, frame);
                }
            }
            // move left
            if (newPlayerRow == currentWorkerPosition.getRow() && newPlayerColumn == currentWorkerPosition.getColumn() - 1){
                MazeField newBoxPosition = mazeFields.get(newPlayerRow).get(newPlayerColumn - 1);
                FieldState newBoxFieldState = newBoxPosition.getFieldState();
                if (newBoxFieldState == FieldState.DOCK || newBoxFieldState == FieldState.FLOOR){
                    moveBoxOnePosition(newPlayerPosition, newBoxPosition, frame);
                }
            }
            // move down
            if (newPlayerRow == currentWorkerPosition.getRow() + 1 && newPlayerColumn == currentWorkerPosition.getColumn()){
                MazeField newBoxPosition = mazeFields.get(newPlayerRow + 1).get(newPlayerColumn);
                FieldState newBoxFieldState = newBoxPosition.getFieldState();
                if (newBoxFieldState == FieldState.DOCK || newBoxFieldState == FieldState.FLOOR){
                    moveBoxOnePosition(newPlayerPosition, newBoxPosition, frame);
                }
            }
            // move up
            if (newPlayerRow == currentWorkerPosition.getRow() - 1 && newPlayerColumn == currentWorkerPosition.getColumn()){
                MazeField newBoxPosition = mazeFields.get(newPlayerRow - 1).get(newPlayerColumn);
                FieldState newBoxFieldState = newBoxPosition.getFieldState();
                if (newBoxFieldState == FieldState.DOCK || newBoxFieldState == FieldState.FLOOR){
                    moveBoxOnePosition(newPlayerPosition, newBoxPosition, frame);
                }
            }
        }
        return false;
    }

    public void gotoCoordinate(final RelativeLayout frame, float x, float y) {
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

        // Don't move if destination field is not empty floor field
        if (!(mazeFields.get(newPlayerRow).get(newPlayerColumn).getFieldState() == FieldState.FLOOR ||
                mazeFields.get(newPlayerRow).get(newPlayerColumn).getFieldState() == FieldState.DOCK)){
            return;
        }

        final List<MazeField> path = PathFinder.findPath(this, currentWorkerPosition, mazeFields.get(newPlayerRow).get(newPlayerColumn));
        if (path == null) {
            return;
        }

        Thread t = new Thread() {
            public void run() {
                while (path.size() > 0) {
                    moveWorkerOnePosition(path.remove(0), frame);
                    SystemClock.sleep(100);
                }
            }
        };
        t.start();
    }

    private void moveWorkerOnePosition(MazeField newWorkerPosition, RelativeLayout frame) {
        moves.add(new Move(currentWorkerPosition, newWorkerPosition));
        moveWorker(newWorkerPosition, frame);
    }

    private void moveBoxOnePosition(MazeField newWorkerPosition, MazeField newBoxPosition, RelativeLayout frame){
        moves.add(new Move(currentWorkerPosition, newWorkerPosition, newBoxPosition));
        moveWorker(newWorkerPosition, frame);
        moveBox(newWorkerPosition, newBoxPosition, frame);
    }

    private void moveWorker(MazeField newWorkerPosition, RelativeLayout frame) {
        currentWorkerPosition.setWorker(false);
        Helper.getFieldView(currentWorkerPosition, frame).postInvalidate();

        currentWorkerPosition = newWorkerPosition;

        newWorkerPosition.setWorker(true);
        Helper.getFieldView(newWorkerPosition, frame).postInvalidate();
    }

    private void moveBox(MazeField oldBoxPosition, MazeField newBoxPosition, RelativeLayout frame){
        oldBoxPosition.setBox(false);
        Helper.getFieldView(oldBoxPosition, frame).postInvalidate();

        newBoxPosition.setBox(true);
        Helper.getFieldView(newBoxPosition, frame).postInvalidate();
    }

    public Map<Integer, Map<Integer, MazeField>> getMazeFields() {
        return mazeFields;
    }

    public boolean levelComplete() {
        for (Map<Integer, MazeField> row : mazeFields.values()) {
            for (MazeField field : row.values())
                if (field.getFieldState() == FieldState.DOCK || field.getFieldState() == FieldState.BOX){
                    return false;
                }
        }
        return true;
    }

    public void undoMove(RelativeLayout frame) {
        if (moves.size() == 0){
            return;
        }

        Move lastMove = moves.remove(moves.size() - 1);

        // Undo worker
        currentWorkerPosition.setWorker(false);
        Helper.getFieldView(currentWorkerPosition, frame).postInvalidate();
        currentWorkerPosition = lastMove.getWorkerStartPosition();
        currentWorkerPosition.setWorker(true);
        Helper.getFieldView(currentWorkerPosition, frame).postInvalidate();

        // Undo box
        if (lastMove.getBoxEndPosition() != null){
            lastMove.getBoxEndPosition().setBox(false);
            Helper.getFieldView(lastMove.getBoxEndPosition(), frame).postInvalidate();

            lastMove.getWorkerEndPosition().setBox(true);
            Helper.getFieldView(lastMove.getWorkerEndPosition(), frame).postInvalidate();
        }
    }
}
