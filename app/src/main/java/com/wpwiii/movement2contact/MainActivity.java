package com.wpwiii.movement2contact;

import com.wpwiii.movement2contact.Prefs;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
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
import android.util.Log;
import java.io.FileOutputStream;
import android.content.Context;
import android.view.View.OnTouchListener;
import 	android.view.MotionEvent;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String SAVEGAMEFILENAME = "savegame.dat";

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
        boolean enableResume = false;

        try {
            String[] files = fileList();
            enableResume = false;
            for (String file : files) {
                if (file.equals(SAVEGAMEFILENAME)) {
                    enableResume = true;
                    break;
                }
            }
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
            enableResume = false;
        }

        Button resumeButton = (Button) findViewById(R.id.buttonResume);
        resumeButton.setEnabled(enableResume);
        if (enableResume) {
            resumeButton.setTextColor(Color.parseColor("#ffff00"));
            resumeButton.setBackgroundResource(R.drawable.button_border_enabled);
        }
        else {
            resumeButton.setTextColor(Color.parseColor("#c0c0c0"));
            resumeButton.setBackgroundResource(R.drawable.button_border_disabled);
        }

        Log.d(TAG, "onResume");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startMediaPlayer();

        Log.d(TAG, "onRestart");

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

        Log.d(TAG, "onPause");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _mediaPlayer = MediaPlayer.create(this, R.raw.holst);
        _mediaPlayer.setVolume(1.0f, 1.0f);
        _mediaPlayer.setLooping(true);
        startMediaPlayer();
        boolean enableResume = false;

        // set custom font on label and buttons
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Army.ttf");
        TextView text = (TextView) findViewById(R.id.textViewAppName);
        text.setTypeface(tf);
        Typeface tft = Typeface.createFromAsset(getAssets(), "fonts/ArmyThin.ttf");


        // disable the resume button if no saved game
        Button resumeButton = (Button) findViewById(R.id.buttonResume);
        resumeButton.setTypeface(tft);
        // if there is a saved game file, enable the resume button, otherwise disable it
        try {
            String[] files = fileList();
            enableResume = false;
            for (String file : files) {
                if (file.equals(SAVEGAMEFILENAME)) {
                    enableResume = true;
                    break;
                }
            }
        }
        catch (Exception e) {
            enableResume = false;
            Log.e(TAG, e.getMessage());
        }

        // enable or disable if a save file exists
        resumeButton.setEnabled(enableResume);
        if (enableResume) {
            resumeButton.setTextColor(Color.parseColor("#ffff00"));
            resumeButton.setBackgroundResource(R.drawable.button_border_enabled);
        }
        else {
            resumeButton.setTextColor(Color.parseColor("#c0c0c0"));
            resumeButton.setBackgroundResource(R.drawable.button_border_disabled);
        }

        // now, set a touch listener to go load the file and resume the game
        resumeButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Button btn = (Button) findViewById(R.id.buttonResume);
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setTextColor(Color.parseColor("#ffffff"));
                    btn.setBackgroundResource(R.drawable.button_border_selected);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setTextColor(Color.parseColor("#ffff00"));
                    btn.setBackgroundResource(R.drawable.button_border_enabled);
                    Intent myIntent = new Intent(MainActivity.this, GameActivity.class);
                    myIntent.putExtra("NEW_GAME", "false");
                    startActivity(myIntent);
                }
                return true;
            }
        });

        // add ontouchlistener to new game button; on the release of the button, go ahead and fire off new game activities
        Button newButton = (Button) findViewById(R.id.buttonNew);
        newButton.setTypeface(tft);
        newButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Button btn = (Button) findViewById(R.id.buttonNew);
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setTextColor(Color.parseColor("#ffffff"));
                    btn.setBackgroundResource(R.drawable.button_border_selected);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setTextColor(Color.parseColor("#ffff00"));
                    btn.setBackgroundResource(R.drawable.button_border_enabled);
                    // starting a new game, so delete any saved game if it exists
                    try {
                        deleteFile(SAVEGAMEFILENAME);
                    }
                    catch (Exception e) {
                        // do nothing
                        Log.e(TAG, e.getMessage());
                    }
                    Intent myIntent = new Intent(MainActivity.this, GameActivity.class);
                    myIntent.putExtra("NEW_GAME", "true");
                    startActivity(myIntent);
                }
                return true;
            }
        });

        // add onclick to help image
        ImageView imageViewHelp = (ImageView) findViewById(R.id.imageViewHelp);
        imageViewHelp.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, HelpActivity.class);
                myIntent.putExtra("NEW_GAME", "false");
                startActivity(myIntent);
            }
        });

        final ArrayList selectedItems = new ArrayList();
        selectedItems.add(Prefs.SETTINGS_MUSIC);
        selectedItems.add(Prefs.SETTINGS_SOUND);
        final CharSequence[] choices = {getString(R.string.music), getString(R.string.soundfx)};
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
                builder.setTitle(getString(R.string.dialog_settings));
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
