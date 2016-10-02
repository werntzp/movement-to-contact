package com.wpwiii.movement2contact;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    MediaPlayer _mediaPlayer = null;

    @Override
    protected void onResume() {
        super.onResume();
        try {
            _mediaPlayer.start();
        }
        catch (Exception e) {
            // media player failed, so kick it on again
            _mediaPlayer = MediaPlayer.create(this, R.raw.holst);
            _mediaPlayer.start();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            _mediaPlayer.start();
        }
        catch (Exception e) {
            // media player failed, so kick it on again
            _mediaPlayer = MediaPlayer.create(this, R.raw.holst);
            _mediaPlayer.start();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        try {
            _mediaPlayer.reset();
            _mediaPlayer.release();
        }
        catch (Exception e) {
            // do nothing
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _mediaPlayer = MediaPlayer.create(this, R.raw.holst);
        _mediaPlayer.setLooping(true);
        _mediaPlayer.start();

        // disable the resume button if no saved game
        Button resumeButton = (Button) findViewById(R.id.button2);
        resumeButton.setEnabled(false);
        resumeButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, GameActivity.class);
                myIntent.putExtra("NEW_GAME", false);
                startActivity(myIntent);
            }
        });

        // add onclick to new game button
        Button newButton = (Button) findViewById(R.id.button1);
        newButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, GameActivity.class);
                myIntent.putExtra("NEW_GAME", Boolean.TRUE);
                startActivity(myIntent);
            }
        });

        // add onclick to help button
        Button helpButton = (Button) findViewById(R.id.button4);
        helpButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, HelpActivity.class);
                myIntent.putExtra("NEW_GAME", Boolean.FALSE);
                startActivity(myIntent);
            }
        });


    }
}
