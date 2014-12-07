package com.brus.sokobrus;

import android.widget.RelativeLayout;

import com.brus.sokobrus.view.components.MazeFieldView;
import com.brus.sokobrus.view.model.MazeField;

/**
 * Created by brus on 12/7/2014.
 */
public class Helper {
    public static final String SELECTED_LEVEL = "SELECTED_LEVEL";
    public static final String SHARED_PREFERENCES = "shared_preferences";
    public static final String CURRENT_LEVEL = "current_level";
    public static final int LEVEL_MIN_VALUE = 1;

    public static int calculateFieldViewId(int row, int col) {
        return row * 100 + col;
    }

    public static MazeFieldView getFieldView(MazeField field, RelativeLayout frame) {
        return (MazeFieldView) frame.findViewById(calculateFieldViewId(field.getRow(), field.getColumn()));
    }
}
