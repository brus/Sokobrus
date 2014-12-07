package com.brus.sokobrus.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import com.brus.sokobrus.Helper;
import com.brus.sokobrus.view.model.Maze;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class SokobrusActivity extends Activity {

    private static final String SAVE_STATE_KEY = "Maze";
    private RelativeLayout frame;
    private GestureDetector gestureDetector;
    private Maze maze = new Maze();
    private int mouseDownId;
    private int currentLevel = 0;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sokobrus);

        frame = (RelativeLayout) findViewById(R.id.main_layout);
        frame.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                maze.initializeMazeUI(frame);
            }
        });

        setupGestureDetector();

        currentLevel = getIntent().getExtras().getInt(Helper.SELECTED_LEVEL);
        if (savedInstanceState == null) {
            loadMaze(currentLevel);
        } else {
            maze = (Maze) savedInstanceState.getSerializable(SAVE_STATE_KEY);
            maze.initializeAfterSerialization(this, frame);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(SAVE_STATE_KEY, maze);
        // TODO Save state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.select_level).setTitle(String.format(getString(R.string.select_level), currentLevel));

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
            maze.undoMove(frame);
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
            SharedPreferences sharedPref = getSharedPreferences(Helper.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            int lastAvailableLevel = sharedPref.getInt(Helper.CURRENT_LEVEL, Helper.LEVEL_MIN_VALUE);
            final int nextLevel = currentLevel + 1;
            if (lastAvailableLevel == currentLevel) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(Helper.CURRENT_LEVEL, nextLevel);
                editor.commit();
            }

            final AlertDialog dialog = new AlertDialog.Builder(this).create();
            LayoutInflater inflater = getLayoutInflater();
            View levelCompleteView = inflater.inflate(R.layout.level_complete, null);
            levelCompleteView.findViewById(R.id.main_menu_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SokobrusActivity.this, WelcomeScreenActivity.class);
                    startActivity(intent);
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
            maze.Load(this, frame, levels.get(level - 1).substring(1, levels.get(level - 1).length()));
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
