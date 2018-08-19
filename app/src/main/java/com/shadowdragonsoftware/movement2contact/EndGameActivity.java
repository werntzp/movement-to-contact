package com.shadowdragonsoftware.movement2contact;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EndGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        // pull out the values that were passed in
        Bundle extras = getIntent().getExtras();
        TextView txtEndGameTitle = (TextView) findViewById(R.id.txtEndGameTitle);
        txtEndGameTitle.setText(extras.getString("TITLE"));

        TextView txtEndGameMessage = (TextView) findViewById(R.id.txtEndGameMessage);
        txtEndGameMessage.setText(extras.getString("MESSAGE"));

        Button btnClose = (Button) findViewById(R.id.btnClose);

        btnClose.setTextColor(Color.parseColor("#ffff00"));
        btnClose.setBackgroundResource(R.drawable.button_border_enabled);

        // now, set a touch listener to go end the game
        btnClose.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Button btn = (Button) findViewById(R.id.btnClose);
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setTextColor(Color.parseColor("#ffffff"));
                    btn.setBackgroundResource(R.drawable.button_border_selected);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setTextColor(Color.parseColor("#ffff00"));
                    btn.setBackgroundResource(R.drawable.button_border_enabled);
                }

                Intent myIntent = new Intent(EndGameActivity.this, MainActivity.class);
                startActivity(myIntent);

                return true;
            }
        });




    }
}
