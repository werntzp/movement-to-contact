package com.wpwiii.movement2contact;

import com.wpwiii.movement2contact.Prefs;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    MediaPlayer _mediaPlayer = null;

    private void startMediaPlayer() {

        // only start the music if set in preferences
        if (Prefs.getValue(MainActivity.this, Prefs.KEY_MUSIC) == 1) {

            try {
                _mediaPlayer.start();
            } catch (Exception e) {
                // media player failed, so kick it on again
                _mediaPlayer = MediaPlayer.create(this, R.raw.holst);
                _mediaPlayer.start();
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        startMediaPlayer();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startMediaPlayer();

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
        startMediaPlayer();

        // set custom font on label and buttons
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Army.ttf");
        TextView text = (TextView) findViewById(R.id.textViewAppName);
        text.setTypeface(tf);
        Typeface tft = Typeface.createFromAsset(getAssets(), "fonts/ArmyThin.ttf");


        // disable the resume button if no saved game
        Button resumeButton = (Button) findViewById(R.id.buttonResume);
        resumeButton.setTypeface(tft);
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
        newButton.setTypeface(tft);
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

        final ArrayList selectedItems = new ArrayList();
        selectedItems.add(Prefs.SETTINGS_MUSIC);
        selectedItems.add(Prefs.SETTINGS_SOUND);
        final CharSequence[] choices = {"Music", "Sound Effects"};
        final boolean[] defaults = {true, true};

        // read from prefs to decide whether to add true or false for music and sound
        if (Prefs.getValue(this, Prefs.KEY_MUSIC) == 0) {
            defaults[0] = false;
        }
        if (Prefs.getValue(this, Prefs.KEY_SOUND) == 0) {
            defaults[1] = false;
        }

        // add onclick to sound image
        ImageView imageViewSound = (ImageView) findViewById(R.id.imageViewSound);
        imageViewSound.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
                builder.setTitle("Settings");
                builder.setMultiChoiceItems(choices, defaults,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    selectedItems.add(which);
                                } else if (selectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    selectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        });

                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do they want music on or off?
                        if (selectedItems.contains(Prefs.SETTINGS_MUSIC)) {
                            Prefs.setValue(MainActivity.this, Prefs.KEY_MUSIC, 1);
                            try {
                                startMediaPlayer();

                            }
                            catch (Exception e) {
                                // media player failed, so kick it on again
                                _mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.holst);
                                startMediaPlayer();

                            }
                        }
                        else {
                            Prefs.setValue(MainActivity.this, Prefs.KEY_MUSIC, 0);
                            try {
                                // stop the media player
                                _mediaPlayer.stop();
                                _mediaPlayer.reset();
                                _mediaPlayer.release();
                            }
                            catch (Exception e) {
                                // do nothing
                            }
                        }
                        // do they want sound effects on or off?
                        if (selectedItems.contains(Prefs.SETTINGS_SOUND)) {
                            Prefs.setValue(MainActivity.this, Prefs.KEY_SOUND, 1);
                        }
                        else {
                            Prefs.setValue(MainActivity.this, Prefs.KEY_SOUND, 0);
                        }


                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


    }
}
