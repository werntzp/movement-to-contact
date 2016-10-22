package com.wpwiii.movement2contact;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.widget.TextView;
import android.graphics.Typeface;
import android.widget.ImageView;

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

        // set custom font on label and buttons
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Army.ttf");
        TextView text = (TextView) findViewById(R.id.textViewAppName);
        text.setTypeface(tf);


        // disable the resume button if no saved game
        Button resumeButton = (Button) findViewById(R.id.buttonResume);
        resumeButton.setTypeface(tf);
        resumeButton.setEnabled(false);
        resumeButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, GameActivity.class);
                myIntent.putExtra("NEW_GAME", false);
                startActivity(myIntent);
            }
        });

        // add onclick to new game button
        Button newButton = (Button) findViewById(R.id.buttonNew);
        newButton.setTypeface(tf);
        newButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, GameActivity.class);
                myIntent.putExtra("NEW_GAME", Boolean.TRUE);
                startActivity(myIntent);
            }
        });

        // add onclick to help image
        ImageView imageViewHelp = (ImageView) findViewById(R.id.imageViewHelp);
        imageViewHelp.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, HelpActivity.class);
                myIntent.putExtra("NEW_GAME", Boolean.FALSE);
                startActivity(myIntent);
            }
        });

        // add onclick to sound image
        ImageView imageViewSound = (ImageView) findViewById(R.id.imageViewSound);
        imageViewSound.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // add a dialog here later
            }
        });


    }
}
