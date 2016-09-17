package com.wpwiii.movement2contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // add text about game in here
        TextView textView = (TextView) findViewById(R.id.textView4);
        textView.setText(R.string.mtc);

        // add button to close out activity
        // add onclick to help button
        Button okButton = (Button) findViewById(R.id.button6);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });



    }

}
