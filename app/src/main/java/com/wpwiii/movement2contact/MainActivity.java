package com.wpwiii.movement2contact;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
