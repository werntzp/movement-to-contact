package com.shadowdragonsoftware.movement2contact;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class EndGameActivity extends AppCompatActivity {

    private static final String TAG = "EndGameActivity";


    // ===========================
    // onBackPressed
    // ===========================
    @Override
    public void onBackPressed() {

        Log.d(TAG, "onBackPressed");
        Intent myIntent = new Intent(EndGameActivity.this, MainActivity.class);
        startActivity(myIntent);

    }


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

        ImageView img1 = (ImageView) findViewById(R.id.imgStar1);
        ImageView img2 = (ImageView) findViewById(R.id.imgStar2);
        ImageView img3 = (ImageView) findViewById(R.id.imgStar3);
        Drawable draw = getResources().getDrawable(R.drawable.achievement_unlocked);

        int stars = extras.getInt("STARS", 0);
        switch (stars) {
            case 1:
                img1.setImageDrawable(draw);
                break;
            case 2:
                img1.setImageDrawable(draw);
                img2.setImageDrawable(draw);
                break;
            case 3:
                img1.setImageDrawable(draw);
                img2.setImageDrawable(draw);
                img3.setImageDrawable(draw);
        }

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
