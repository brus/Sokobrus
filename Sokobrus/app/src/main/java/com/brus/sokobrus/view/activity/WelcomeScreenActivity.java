package com.brus.sokobrus.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.brus.sokobrus.R;
import com.brus.sokobrus.Helper;

/**
 * Created by brus on 12/7/2014.
 */
public class WelcomeScreenActivity extends Activity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome_screen);
        initializeComponents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeSelectLevelPicker();
    }

    private void initializeComponents() {
        initializePlayOnClickListener();
    }

    private void initializeSelectLevelPicker() {
        NumberPicker picker = (NumberPicker) findViewById(R.id.selected_level);
        picker.setMinValue(Helper.LEVEL_MIN_VALUE);
        SharedPreferences sharedPref = getSharedPreferences(Helper.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        int level = sharedPref.getInt(Helper.CURRENT_LEVEL, Helper.LEVEL_MIN_VALUE);
        picker.setMaxValue(level);
        picker.setValue(level);
    }

    private void initializePlayOnClickListener() {
        Button play = (Button) findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberPicker picker = (NumberPicker) findViewById(R.id.selected_level);
                Intent intent = new Intent(WelcomeScreenActivity.this, SokobrusActivity.class);
                intent.putExtra(Helper.SELECTED_LEVEL, picker.getValue());
                startActivity(intent);
            }
        });
    }
}
