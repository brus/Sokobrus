package com.brus.sokobrus.view.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import com.brus.sokobrus.Helper;
import com.brus.sokobrus.R;
import com.brus.sokobrus.view.model.FieldState;
import com.brus.sokobrus.view.model.MazeField;

/**
 * Created by brus on 11/8/2014.
 */
public class MazeFieldView extends View {
    private MazeField mazeField;

    private final Paint painter = new Paint();

    private int fieldSize;
    private float x;
    private float y;

    public MazeFieldView(Context context, MazeField mazeField) {
        super(context);

        setId(Helper.calculateFieldViewId(mazeField.getRow(), mazeField.getColumn()));
        this.mazeField = mazeField;
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
        x = mazeField.getColumn() * fieldSize;
        y = mazeField.getRow() * fieldSize;
    }

    private Bitmap getBitmap() {
        FieldState state = mazeField.getFieldState();
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


}
