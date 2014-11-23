package com.brus.sokobrus.view;

import java.io.Serializable;

/**
 * Created by brus on 11/22/2014.
 */
public class Move implements Serializable {
    private MazeField workerStartPosition;
    private MazeField workerEndPosition;
    private MazeField boxEndPosition;

    public Move(MazeField workerStartPosition, MazeField workerEndPosition) {
        this.workerStartPosition = workerStartPosition;
        this.workerEndPosition = workerEndPosition;
    }

    public Move(MazeField workerStartPosition, MazeField workerEndPosition, MazeField boxEndPosition) {
        this(workerStartPosition, workerEndPosition);
        this.boxEndPosition = boxEndPosition;
    }

    public MazeField getWorkerStartPosition() {
        return workerStartPosition;
    }

    public void setWorkerStartPosition(MazeField workerStartPosition) {
        this.workerStartPosition = workerStartPosition;
    }

    public MazeField getWorkerEndPosition() {
        return workerEndPosition;
    }

    public void setWorkerEndPosition(MazeField workerEndPosition) {
        this.workerEndPosition = workerEndPosition;
    }

    public MazeField getBoxEndPosition() {
        return boxEndPosition;
    }

    public void setBoxEndPosition(MazeField boxEndPosition) {
        this.boxEndPosition = boxEndPosition;
    }
}
