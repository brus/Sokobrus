package com.brus.sokobrus.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.brus.sokobrus.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private static final String SAVE_STATE_KEY = "Maze";
    private static String SHARED_PREFERENCES = "shared_preferences";
    private static String CURRENT_LEVEL = "current_level";
    private RelativeLayout frame;
    private GestureDetector gestureDetector;
    private Maze maze = new Maze();
    private int mouseDownId;
    private int currentLevel = 0;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        frame = (RelativeLayout) findViewById(R.id.main_layout);

        setupGestureDetector();

        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        currentLevel = sharedPref.getInt(CURRENT_LEVEL, 0);
        loadMaze(0);
//        if (savedInstanceState == null) {
//            loadMaze(currentLevel);
//        } else {
//            maze = (Maze) savedInstanceState.getSerializable(SAVE_STATE_KEY);
//            maze.initializeAfterSerialization(this, frame);
//        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(SAVE_STATE_KEY, maze);
        // TODO Save state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            maze.initializeMazeUI(frame);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.select_level).setTitle(String.format(getString(R.string.select_level), currentLevel + 1));

        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.select_level) {
            return true;
        } else if (id == R.id.undo) {
            maze.undoMove();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mouseDownId == -1) {
                    mouseDownId = event.getActionIndex();
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getActionIndex() == mouseDownId) {
                    boolean valid = maze.moveBox(frame, event.getX(mouseDownId), event.getY(mouseDownId));
                    if (valid) {
                        if (levelComplete()) {
                            mouseDownId = -1;
                        }
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mouseDownId == event.getActionIndex()) {
                    mouseDownId = -1;
                    return true;
                }
                break;
        }
        return false;
    }

    private boolean levelComplete() {
        if (maze.levelComplete()) {
            SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
            int lastLevel = sharedPref.getInt(CURRENT_LEVEL, 0);
            final int nextLevel = currentLevel + 1;
            if (lastLevel == currentLevel) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(CURRENT_LEVEL, nextLevel);
                editor.commit();
            }

            final AlertDialog dialog = new AlertDialog.Builder(this).create();
            LayoutInflater inflater = getLayoutInflater();
            View levelCompleteView = inflater.inflate(R.layout.level_complete, null);
            levelCompleteView.findViewById(R.id.main_menu_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                }
            });
            levelCompleteView.findViewById(R.id.replay_level_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    frame.removeAllViews();
                    loadMaze(currentLevel);
                    menu.findItem(R.id.select_level).setTitle(String.format(getString(R.string.select_level), currentLevel));
                    frame.postInvalidate();
                    dialog.dismiss();
                }
            });
            levelCompleteView.findViewById(R.id.next_level_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    frame.removeAllViews();
                    loadMaze(nextLevel);
                    menu.findItem(R.id.select_level).setTitle(String.format(getString(R.string.select_level), nextLevel));
                    frame.postInvalidate();
                    dialog.dismiss();
                }
            });
            dialog.setView(levelCompleteView);
            dialog.show();

            return true;
        }
        return false;
    }

    private void loadMaze(int level) {
        AssetManager assetManager = getAssets();
        try {
            InputStream stream = assetManager.open("rooms.dat");
            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = null;
            List<String> levels = new ArrayList<String>();
            while ((line = bufferedReader.readLine()) != null) {
                levels.add(line);
            }
            maze.Load(this, frame, levels.get(level).substring(1, levels.get(level).length()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupGestureDetector() {

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent event) {
                maze.gotoCoordinate(frame, event.getX(), event.getY());
                return true;
            }
        });
    }

}
