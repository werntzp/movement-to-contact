package com.wpwiii.movement2contact;

import android.graphics.Color;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.widget.AdapterView;
import android.widget.GridView;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import java.util.Random;
import com.wpwiii.movement2contact.R;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

// class for units
class Unit {

    public static final int OWNER_PLAYER = 0;
    public static final int OWNER_OPFOR = 1; 

    public static final int TYPE_INF = 0; 
    public static final int TYPE_MG = 1; 
    public static final int TYPE_HQ = 2; 
    public static final int TYPE_SNIPER = 3; 
    public static final int TYPE_MORTAR = 4;
    
    public static final int SIZE_SQUAD = 0;
    public static final int SIZE_PLATOON = 1;
    public static final int SIZE_SECTION = 2; 
    public static final int SIZE_TEAM = 3; 

    public static final int EFF_GREEN = 3; 
    public static final int EFF_AMBER = 2; 
    public static final int EFF_RED = 1; 
    public static final int EFF_BLACK = 0; 

    private int _type = TYPE_INF; 
    private int _size = SIZE_PLATOON;
    private int _eff = EFF_GREEN; 
    private int _owner = OWNER_PLAYER; 
    private boolean _isVisible = false;
    private String _name = "";
    private boolean _isActive = false;
    private int _maxMove = 0;
    private int _remainingMove = 0;
    private int _attackRange = 0;
    private boolean _hasAttacked = false;
    private boolean _isSuppressed = false;
    private int _attackNumber = 0;

    public Unit(String name, int type, int size, int owner, int attackRange, int attackNumber, int maxMove) {

        // set incoming values and defaults
        _owner = owner;
        if (_owner == OWNER_PLAYER) {
            _isVisible = true;
        }
        _type = type;
        _size = size;
        _attackRange = attackRange;
        _attackNumber = attackNumber;
        _eff = EFF_GREEN;
        _maxMove = maxMove;
        _remainingMove = maxMove;
        _isActive = false;
        _hasAttacked = false;
        _isSuppressed = false;
        _name = name;

    }

    protected void setIsSuppressed(boolean val) { _isSuppressed = val; }
    protected void setHasAttacked(boolean val) {
        _hasAttacked = val;
    }
    protected void setRemainingMove(int val) {
        _remainingMove = val;
    }
    protected void setIsActive(boolean val) {
        _isActive = val;
    }
    protected void setIsVisible(boolean val) {
        _isVisible = val;
    }
    protected void setEff(int val) {
        _eff = val;
    }

    protected int getAttackNumber() {
        return _attackNumber;
    }
    protected boolean getIsSuppressed() {
        return _isSuppressed;
    }
    protected boolean getHasAttacked() {
        return _hasAttacked;
    }
    protected int getAttackRange() {
        return _attackRange;
    }
    protected int getRemainingMove() {
        return _remainingMove;
    }
    protected int getMaxMove() {
        return _maxMove;
    }
    protected boolean getIsActive() {
        return _isActive;
    }
    protected boolean getIsVisible() {
        return _isVisible;
    }
    protected int getType() {
        return _type; 
    }
    protected int getSize() {
        return _size; 
    }
    protected int getEff() {
        return _eff; 
    }
    protected int getOwner() {
        return _owner; 
    }
    protected String getName() { return _name; }

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
    private ImageView imgPrevClicked = null;


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
    // getUnitIcon
    // ===========================
    int getUnitIcon(Unit u) {

        int img = 0;

        // make decisions for icon based on size and type
        if (u.getType() == Unit.TYPE_INF) {
            if (u.getSize() == Unit.SIZE_SQUAD) {
                img = R.drawable.inf_squad;
            } else {
                switch (u.getEff()) {
                    case Unit.EFF_AMBER:
                        img = R.drawable.inf_platoon_amber;
                        break;
                    case Unit.EFF_BLACK:
                        img = R.drawable.inf_platoon_black;
                        break;
                    case Unit.EFF_RED:
                        img = R.drawable.inf_platoon_red;
                        break;
                    default:
                        img = R.drawable.inf_platoon_green;
                }
            }
        }
        if (u.getType() == Unit.TYPE_MG) {
            switch (u.getEff()) {
                case Unit.EFF_AMBER:
                    img = R.drawable.mg_team_amber;
                    break;
                case Unit.EFF_BLACK:
                    img = R.drawable.mg_team_black;
                    break;
                case Unit.EFF_RED:
                    img = R.drawable.mg_team_red;
                    break;
                default:
                    img = R.drawable.mg_team_green;
            }
        }
        if ((u.getType() == Unit.TYPE_MORTAR) && (u.getOwner() == Unit.OWNER_PLAYER)) {
            switch (u.getEff()) {
                case Unit.EFF_AMBER:
                    img = R.drawable.mortar_section_amber;
                    break;
                case Unit.EFF_BLACK:
                    img = R.drawable.mortar_section_black;
                    break;
                case Unit.EFF_RED:
                    img = R.drawable.mortar_section_red;
                    break;
                default:
                    img = R.drawable.mortar_section_green;
            }
        }
        if ((u.getType() == Unit.TYPE_MORTAR) && (u.getOwner() == Unit.OWNER_OPFOR)) {
            img = R.drawable.mortar_section;
        }
        if (u.getType() == Unit.TYPE_HQ) {
            switch (u.getEff()) {
                case Unit.EFF_AMBER:
                    img = R.drawable.hq_section_amber;
                    break;
                case Unit.EFF_BLACK:
                    img = R.drawable.hq_section_black;
                    break;
                case Unit.EFF_RED:
                    img = R.drawable.hq_section_red;
                    break;
                default:
                    img = R.drawable.hq_section_green;
            }
        }
        if (u.getType() == Unit.TYPE_SNIPER) {
            img = R.drawable.sniper_team;
        }

        return img;



    }

    // ===========================
    // setUpFriendlyUnits
    // ===========================
    void setUpFriendlyUnits() {

        // open up csv in res/raw folder
        InputStream ins = getResources().openRawResource(
                getResources().getIdentifier("units",
                        "raw", getPackageName()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
        StringBuilder out = new StringBuilder();
        String line;
        String item;
        Unit unit;
        Integer pos = 0;

        try {
            while ((line = reader.readLine()) != null) {
                String [] items = line.split(",");
                // create a unit class
                unit = new Unit(items[0], Integer.parseInt(items[2]), Integer.parseInt(items[3]), Integer.parseInt(items[1]),
                        Integer.parseInt(items[4]), Integer.parseInt(items[5]), Integer.parseInt(items[6]));
                // drop that into the map array
                pos = getArrayPosforRowCol(Integer.parseInt(items[7]), Integer.parseInt(items[8]));
                mapSquares[pos].setUnit(unit);
                // also, overwrite the image for images array
                imageIds[pos] = getUnitIcon(unit);

            }
            reader.close();
        }
        catch (IOException ioe) {
            // do nothing
        }


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
                col = 1;
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
        setUpFriendlyUnits();

        // set the initial turn text
        turnString  = "Movement To Contact - Turn 1 - Your turn";
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

        // set the long press

        gridView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v,
                                           int position, long id) {
                Log.d(TAG, "LongClick at position: " + Integer.toString(position));
                return true;
            }
        });


        // set the onlick
        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Log.d(TAG, "Clicked on position: " + Integer.toString(position));

                // always clear out action text
                actionText.setText("");

                ImageView imgClicked =  (ImageView) v;
                // if a player unit, up the padding and change background color to show highlight
                switch ((Integer)imgClicked.getTag()) {
                    case R.drawable.inf_platoon_amber:
                    case R.drawable.inf_platoon_black:
                    case R.drawable.inf_platoon_green:
                    case R.drawable.inf_platoon_red:
                    case R.drawable.mg_team_amber:
                    case R.drawable.mg_team_black:
                    case R.drawable.mg_team_green:
                    case R.drawable.mg_team_red:
                    case R.drawable.hq_section_amber:
                    case R.drawable.hq_section_black:
                    case R.drawable.hq_section_green:
                    case R.drawable.hq_section_red:
                    case R.drawable.mortar_section_amber:
                    case R.drawable.mortar_section_black:
                    case R.drawable.mortar_section_green:
                    case R.drawable.mortar_section_red:
                        // set new one
                        imgClicked.setPadding(5,5,5,5);
                        imgClicked.setBackgroundColor(Color.BLUE);
                        // get info on unit selected
                        Unit u = mapSquares[position].getUnit();
                        // update the action text
                        actionText.setText("Selected: " + u.getName());
                    default:
                        // reset old one (if there was one)
                        if (imgPrevClicked != null) {
                            imgPrevClicked.setPadding(2, 2, 2, 2);
                            imgPrevClicked.setBackgroundColor(Color.BLACK);
                        }

                }
                // store
                imgPrevClicked = imgClicked;

            }
        });


    }
}
