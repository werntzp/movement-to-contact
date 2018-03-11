package com.shadowdragonsoftware.movement2contact;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.webkit.WebView;

public class HelpActivity extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // get the custom font
        //Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Army.ttf");

        // get the webview and load up local html page
        WebView wv = (WebView) findViewById(R.id.webView1);
        wv.loadUrl("file:///android_asset/help.html");

        // add button to close out activity
        // add onclick to help button
        Button okButton = (Button) findViewById(R.id.button6);
        //okButton.setTypeface(tf);
        okButton.setTextColor(Color.parseColor("#ffff00"));
        okButton.setBackgroundResource(R.drawable.button_border_enabled);

        // now, set a touch listener to go load the file and resume the game
        okButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Button btn = (Button) findViewById(R.id.button6);
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


    }

}
