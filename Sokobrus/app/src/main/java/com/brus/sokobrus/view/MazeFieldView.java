package com.brus.sokobrus.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import com.brus.sokobrus.R;

/**
 * Created by brus on 11/8/2014.
 */
public class MazeFieldView extends View {
    private final Paint painter = new Paint();

    private int fieldSize;
    private float x;
    private float y;

    private int row;
    private int column;

    private boolean worker;
    private boolean box;
    private boolean wall;
    private boolean dock;
    private boolean floor;

    public MazeFieldView(Context context, int row, int column, boolean box, boolean wall, boolean dock, boolean floor) {
        super(context);

        this.row = row;
        this.column = column;

        this.box = box;
        this.wall = wall;
        this.dock = dock;
        this.floor = floor;
        this.worker = false;

        painter.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap bitmap = getBitmap();
        if (bitmap != null) {
            canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, fieldSize, fieldSize, false), x, y, painter);
        }
    }

    public void initializeUI(int fieldSize) {
        this.fieldSize = fieldSize;
        x = column * fieldSize;
        y = row * fieldSize;
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

    private Bitmap getBitmap() {
        FieldState state = getFieldState();
        if (state == null){
            return null;
        }

        switch (state) {
            case BOX:
                return BitmapFactory.decodeResource(getResources(), R.drawable.box);
            case BOX_DOCKED:
                return BitmapFactory.decodeResource(getResources(), R.drawable.box_docked);
            case DOCK:
                return BitmapFactory.decodeResource(getResources(), R.drawable.dock);
            case FLOOR:
                return BitmapFactory.decodeResource(getResources(), R.drawable.floor);
            case WORKER:
                return BitmapFactory.decodeResource(getResources(), R.drawable.worker);
            case WORKER_DOCKED:
                return BitmapFactory.decodeResource(getResources(), R.drawable.worker_docked);
            case WALL:
                return BitmapFactory.decodeResource(getResources(), R.drawable.wall);
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
