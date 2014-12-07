package com.brus.sokobrus.view.model;

import com.brus.sokobrus.view.components.MazeFieldView;
import com.brus.sokobrus.view.model.FieldState;

import java.io.Serializable;

/**
 * Created by brus on 11/23/2014.
 */
public class MazeField implements Serializable {
    private int row;
    private int column;

    private boolean worker;
    private boolean box;
    private boolean wall;
    private boolean dock;
    private boolean floor;

    public MazeField(int row, int column, boolean box, boolean wall, boolean dock, boolean floor) {
        this.row = row;
        this.column = column;

        this.box = box;
        this.wall = wall;
        this.dock = dock;
        this.floor = floor;
        this.worker = false;
    }

    public FieldState getFieldState() {
        // Worker
        if (worker) {
            if (dock) {
                return FieldState.WORKER_DOCKED;
            }
            return FieldState.WORKER;
        }

        // Box
        if (box) {
            if (dock) {
                return FieldState.BOX_DOCKED;
            }
            return FieldState.BOX;
        }

        // Wall
        if (wall) {
            return FieldState.WALL;
        }

        if (dock) {
            return FieldState.DOCK;
        }

        if (floor) {
            return FieldState.FLOOR;
        }

        return null;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public boolean isWorker() {
        return worker;
    }

    public void setWorker(boolean worker) {
        this.worker = worker;
    }

    public boolean isFloor() {
        return floor;
    }

    public boolean isBox() {
        return box;
    }

    public void setBox(boolean box) {
        this.box = box;
    }
}
