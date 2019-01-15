package com.shadowdragonsoftware.movement2contact;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AchievementsActivity extends AppCompatActivity {

    private static final String TAG = "AchievementsActivity";
    private static final String ACHIEVEMENTSFILENAME = "achievements.dat";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        Button okButton = findViewById(R.id.button6);
        okButton.setTextColor(Color.parseColor("#ffff00"));
        okButton.setBackgroundResource(R.drawable.button_border_enabled);

        /* now, set a touch listener to go load the file and resume the game */
        okButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Button btn = findViewById(R.id.button6);
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setTextColor(Color.parseColor("#ffffff"));
                    btn.setBackgroundResource(R.drawable.button_border_selected);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setTextColor(Color.parseColor("#ffff00"));
                    btn.setBackgroundResource(R.drawable.button_border_enabled);
                    finish();
                }
                return true;
            }
        });

        // load up all the achievement descriptions an array
        String[] achievements_desc = {
                getString(R.string.achievement_movewithapurpose_desc),
                getString(R.string.achievement_eatasoupsandwich_desc),
                getString(R.string.achievement_elevenbangbang_desc),
                getString(R.string.achievement_dontbeinrearwithgear_desc),
                getString(R.string.achievement_makeitrainsteel_desc),
                getString(R.string.achievement_lollygagger_desc),
                getString(R.string.achievement_beltfedbadass_desc),
                getString(R.string.achievement_dangerclose_desc),
                getString(R.string.achievement_gleaming_desc)
        };

        // load up achievement titles in array
        String[] achievements_title = {
                getString(R.string.achievement_movewithapurpose_title),
                getString(R.string.achievement_eatasoupsandwich_title),
                getString(R.string.achievement_elevenbangbang_title),
                getString(R.string.achievement_dontbeinrearwithgear_title),
                getString(R.string.achievement_makeitrainsteel_title),
                getString(R.string.achievement_lollygagger_title),
                getString(R.string.achievement_beltfedbadass_title),
                getString(R.string.achievement_dangerclose_title),
                getString(R.string.achievement_gleaming_title)
        };

        // decide on which icon to send in
        Integer[] achievements_icon = {R.drawable.achievement_locked,R.drawable.achievement_locked,R.drawable.achievement_locked,
                R.drawable.achievement_locked,R.drawable.achievement_locked,R.drawable.achievement_locked,R.drawable.achievement_locked,
                R.drawable.achievement_locked,R.drawable.achievement_locked};

        // load up each achievement and see if they've unlocked it, so that will determine icon
        // load up achievements to set values in case they've uh, already achieved some
        try {
            FileInputStream fis = openFileInput(ACHIEVEMENTSFILENAME);
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String[] achievements = br.readLine().split(",");
            for (int i = 0; i < 9; i++) {
                if (Utils.convertToBoolean(achievements[i])) { achievements_icon[i] = R.drawable.achievement_unlocked; }
            }

        }
        catch (Exception e) {
            // if error reading achievements, just keep them all defaulted to false
            Log.e(TAG, e.getMessage());
        }

        // load up the list view
        AchievementsAdapter adapter = new AchievementsAdapter(this, achievements_title, achievements_desc, achievements_icon);
        ListView list = findViewById(R.id.lvwAchievements);
        list.setAdapter(adapter);

    }
}
