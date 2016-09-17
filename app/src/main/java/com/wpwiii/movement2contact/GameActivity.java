package com.wpwiii.movement2contact;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.widget.AdapterView;
import android.widget.GridView;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.util.Log;
import android.widget.TextView;
import android.content.Intent;
import java.util.Random;
import com.wpwiii.movement2contact.R;

// class for units
class Unit {




}


// class for each map square
class MapSquare {

    private int _terrainType = 0;
    private Unit _unit = null;
    private int _row = 0;
    private int _col = 0;

    protected int getTerrainType() {
        return _terrainType;
    }

    protected void setTerrainType(int val) {
        _terrainType = val;
    }

    protected int getRow() {
        return _row;
    }

    protected void setRow(int val) {
        _row = val;
    }

    protected int getCol() {
        return _col;
    }

    protected void setCol(int val) {
        _col = val;
    }

    protected Unit getUnit() {
        return _unit;
    }

    protected void setUnit(Unit val) {
        _unit = val;
    }

};

// main class
public class GameActivity extends AppCompatActivity {

    GridView gridView;
    MapAdapter mapAdapter = new MapAdapter(this);
    private static final String TAG = "GameActivity";
    public static final int MAX_ROWS = 15;
    public static final int MAX_COLS = 10;
    public static final int MAX_ARRAY = 150;
    private Integer[] mapArray = new Integer[MAX_ARRAY];
    TextView turnText;
    TextView actionText;
    String actionString = "";
    String turnString = "";
    Intent myIntent = getIntent();
    MapSquare[] mapSquares;
    private Integer[] imageIds = new Integer[MAX_ARRAY];


    // ===========================
    // getArrayPosforRowCol
    // ===========================
    int getArrayPosforRowCol(int row, int col) {
        int pos = 0;
        for (pos = 0; pos < MAX_ARRAY; pos++) {
            if ((mapSquares[pos].getRow() == row) && (mapSquares[pos].getCol() == col)) {
                break;
            }
        }
        return pos;
    }

    // ===========================
    // setUpFriendlyUnits
    // ===========================
    void setUpFriendlyUnits() {

    }

    // ===========================
    // setUpEnemyUnits
    // ===========================
    void setUpEnemyUnits() {

    }


    // ===========================
    // randomizeTerrain
    // ===========================
    int randomizeTerrain() {

        Random randomGenerator = new Random();
        int terrainType = 0;
        switch (randomGenerator.nextInt(10) + 1) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                terrainType = R.drawable.scrub;
                break;
            case 7:
            case 8:
            case 9:
                terrainType = R.drawable.woods;
                break;
            default:
                terrainType = R.drawable.rocky;

        }

        return terrainType;
    }

    // ===========================
    // createNewGame
    // ===========================
    void newGame() {

        // create array of map objects
        mapSquares = new MapSquare[MAX_ARRAY];
        MapSquare ms;

        // iterate and populate mapsquares
        int row = 1;
        int col = 1;
        int terrainType = 0;

        // first, set row, col and terrain type for every map square
        for (int ctr = 0; ctr < MAX_ARRAY; ctr++) {
            // set the row and column
            ms = new MapSquare();
            ms.setRow(row);
            ms.setCol(col);
            col++;
            if (col > MAX_COLS) {
                col = 0;
                row++;
            };
            // randomly assign a terrain type
            terrainType = randomizeTerrain();
            ms.setTerrainType(terrainType);
            // also, set to image id array we'll pass in later
            imageIds[ctr] = terrainType;
             // now add to array
            mapSquares[ctr] = ms;

        }

        // next, manually set friendly units


        // set the initial turn text
        turnString  = "Player 1 Turn";
        actionString = "";


    }

    // ===========================
    // resumeGame
    // ===========================
    void resumeGame() {

    }

    // ===========================
    // onCreate
    // ===========================
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        // get the two textviews for status
        turnText = (TextView) findViewById(R.id.textView2);
        actionText = (TextView) findViewById(R.id.textView8);

        // new game?
        if (myIntent != null) {
            if (myIntent.getBooleanExtra("NEW_GAME", Boolean.TRUE)) {
                newGame();
            } else {
                resumeGame();
            }
        }
        else {
            newGame();
        }

        // set the mapadapter to build out the initial map
        gridView = (GridView) findViewById(R.id.gridView1);
        // pass in the array of image ids
        mapAdapter.setImageArray(imageIds);
        gridView.setAdapter(mapAdapter);

        // set the textviews
        turnText.setText(turnString);
        actionText.setText(actionString);

        // set the onlick
        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Log.d(TAG, "Clicked on position: " + Integer.toString(position));
            }
        });


    }
}
