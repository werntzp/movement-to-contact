package com.wpwiii.movement2contact;

import android.graphics.Color;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.widget.AdapterView;
import android.widget.GridView;
import android.view.View;
import android.widget.Button;
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
import android.media.MediaPlayer;
import java.util.concurrent.TimeUnit;
import android.os.Handler;

class Utils {

    // Delay mechanism

    public interface DelayCallback{
        void afterDelay();
    }

    public static void delay(int secs, final DelayCallback delayCallback){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                delayCallback.afterDelay();
            }
        }, secs * 1000); // afterDelay will be executed after (secs*1000) milliseconds.
    }
}

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

    public static final int AGG_VERY = 3;
    public static final int AGG_SOME = 2;
    public static final int AGG_NO = 1;

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
    private int _turnSuppressed = 0;
    private int _aggression = 0;

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
    protected void setTurnSuppressed(int val) { _turnSuppressed = val; }
    protected void setAggression(int val) { _aggression = val; }

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
    protected int getTurnSuppressed() { return _turnSuppressed; }
    protected int getAggression() { return _aggression; }

}


// class for each map square
class MapSquare {

    private int _terrainType = 0;
    private Unit _unit = null;
    private int _row = 0;
    private int _col = 0;

    protected void setTerrainType(int val) {
        _terrainType = val;
    }
    protected void setRow(int val) {
        _row = val;
    }
    protected void setCol(int val) {
        _col = val;
    }
    protected void setUnit(Unit val) {
        _unit = val;
    }

    protected Unit getUnit() {
        return _unit;
    }
    protected int getCol() {
        return _col;
    }
    protected int getRow() {
        return _row;
    }
    protected int getTerrainType() {
        return _terrainType;
    }

};

// main class
public class GameActivity extends AppCompatActivity {

    GridView _gridView;
    MapAdapter _mapAdapter = new MapAdapter(this);
    private static final String TAG = "GameActivity";
    public static final int MAX_ROWS = 15;
    public static final int MAX_COLS = 10;
    public static final int MAX_ARRAY = 150;
    private Integer[] _mapArray = new Integer[MAX_ARRAY];
    TextView _turnText;
    TextView _actionText;
    String _actionString = "";
    String _turnString = "";
    Intent _myIntent = getIntent();
    MapSquare[] _mapSquares;
    private Integer[] _imageIds = new Integer[MAX_ARRAY];
    private Unit _activeUnit = null;
    private MapSquare _activeSq = null;
    private int _turn = 1;

    // ===========================
    // getArrayPosforRowCol
    // ===========================
    int getArrayPosforRowCol(int row, int col) {
        int pos = 0;
        for (pos = 0; pos < MAX_ARRAY; pos++) {
            if ((_mapSquares[pos].getRow() == row) && (_mapSquares[pos].getCol() == col)) {
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
        if ((u.getType() == Unit.TYPE_MG) && (u.getOwner() == Unit.OWNER_PLAYER)) {
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

        if (u.getType() == Unit.TYPE_MORTAR) {
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
        if ((u.getType() == Unit.TYPE_MG) && (u.getOwner() == Unit.OWNER_OPFOR)) {
            img = R.drawable.mg_team;
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
                _mapSquares[pos].setUnit(unit);
                // also, overwrite the image for images array
                _imageIds[pos] = getUnitIcon(unit);

            }
            reader.close();
        }
        catch (IOException ioe) {
            // do nothing
        }


    }


    // ===========================
    // placeEnemyUnit
    // ===========================
    void placeEnemyUnit(Unit u) {

        int col = 0;
        int row = 0;
        int pos = 0;

        Log.d(TAG, "Enter placeEnemyUnit");

        while (true) {
            // generate random row and col
            col = getRandomNumber(MAX_COLS, 1);
            row = getRandomNumber(MAX_ROWS, 8);
            if (!isMapSpotTaken(row, col)) {
                // found an empty spot, so break out of loop
                break;
            }
        }

        // all enemy units start out not visible
        u.setIsVisible(true);

        // get the actual position
        pos = getArrayPosforRowCol(row, col);
        // add unit to array
        _mapSquares[pos].setUnit(u);
        // swap out icon if visible
        if (u.getIsVisible()) {
            _imageIds[pos] = getUnitIcon(u);
        }

        Log.d(TAG, "Exit placeEnemyUnit");

    }

    // ==========================
    // getDistanceBetweenSquares
    // ==========================
    double getDistanceBetweenSquares(int x1, int y1, int x2, int y2) {

        Log.d(TAG, "Enter getDistanceBetweenSquares");

        double dx = Math.abs(x2 - x1);
        double dy = Math.abs(y2 - y1);
        double min = Math.min(dx, dy);
        double max = Math.max(dx, dy);
        double diagonalSteps = min;
        double straightSteps = max - min;
        double distance = Math.floor(Math.sqrt(2) * diagonalSteps + straightSteps);

        Log.d(TAG, "Exit getDistanceBetweenSquares. Distance = " + distance);

        return distance;

    }


    // ===========================
    // getRandomNumber
    // ===========================
    int getRandomNumber(int max, int min) {

        Random randomGenerator = new Random();
        return randomGenerator.nextInt((max - min) + 1) + min;

    }


    // ===========================
    // setUpEnemyUnits
    // ===========================
    void setUpEnemyUnits() {

        Log.d(TAG, "Enter setUpEnemyUnits");

        // open up csv in res/raw folder
        InputStream ins = getResources().openRawResource(
                getResources().getIdentifier("opfor",
                        "raw", getPackageName()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
        StringBuilder out = new StringBuilder();
        String line;
        String item;
        Unit unit;
        int pos = 0;
        int rifleSquads = 0;
        int mgTeams = 0;
        int sniperTeams = 0;
        String name = "";
        int owner = 0;
        int type = 0;
        int size = 0;
        int range = 0;
        int attack = 0;
        int move = 0;
        int aggression = Unit.AGG_NO;

        // first, let's figure out how many units we need to base off the templates in the CSV
        // how many bad guys (between 5 and 10)
        rifleSquads = getRandomNumber(7, 5);
        // then how many mg sections
        mgTeams = getRandomNumber(2, 0);
        // subtract out mg sections
        rifleSquads = rifleSquads - mgTeams;
        // tack on a possible sniper team
        sniperTeams = getRandomNumber(1, 0);

        try {
            while ((line = reader.readLine()) != null) {
                String [] items = line.split(",");

                // store all the fields so we can use later
                name = items[0];
                owner = Integer.parseInt(items[1]);
                type = Integer.parseInt(items[2]);
                size = Integer.parseInt(items[3]);
                range = Integer.parseInt(items[4]);
                attack = Integer.parseInt(items[5]);
                move = Integer.parseInt(items[6]);

                // for each enemy unit, pick an aggression factor (which we'll use later when it comes to movemebt)
                aggression = getRandomNumber(3, 1);

                // based on type, do another loop
                switch (type) {
                    case Unit.TYPE_INF:
                        // loop through inf units, find a spot for each, create a new unit and add to arrays
                        for (int ctr = 0; ctr < rifleSquads; ctr++) {
                            unit = new Unit(name, type, size, owner, range, attack, move);
                            unit.setAggression(aggression);
                            placeEnemyUnit(unit);
                        }

                    case Unit.TYPE_MG:
                        // loop through inf units, find a spot for each, create a new unit and add to arrays
                        for (int ctr = 0; ctr < mgTeams; ctr++) {
                            unit = new Unit(name, type, size, owner, range, attack, move);
                            unit.setAggression(aggression);
                            placeEnemyUnit(unit);
                        }

                    case Unit.TYPE_SNIPER:
                        // loop through inf units, find a spot for each, create a new unit and add to arrays
                        for (int ctr = 0; ctr < sniperTeams; ctr++) {
                            unit = new Unit(name, type, size, owner, range, attack, move);
                            unit.setAggression(aggression);
                            placeEnemyUnit(unit);
                        }
                }

            }
            reader.close();
        }
        catch (IOException ioe) {
            // do nothing
        }


        Log.d(TAG, "Exit setUpEnemyUnits");

    }

    // ===========================
    // getTerrainImage
    // ===========================
    int getTerrainImage(MapSquare ms) {

        return ms.getTerrainType();

    }


    // ===========================
    // getSound
    // ===========================
    int getSound(Unit u) {

        Log.d(TAG, "Enter getSound");

        int soundId = 0;

        switch (u.getType()) {
            case Unit.TYPE_HQ:
                soundId = R.raw.inf_squad;
                break;
            case Unit.TYPE_MG:
                soundId = R.raw.mg;
                break;
            case Unit.TYPE_MORTAR:
                soundId = R.raw.mortar;
                break;
            case Unit.TYPE_SNIPER:
                soundId = R.raw.sniper;
                break;
            default:
                if (u.getSize() == Unit.SIZE_PLATOON) {
                    soundId = R.raw.inf_platoon;
                }
                else {
                    soundId = R.raw.inf_squad;
                }
        }

        Log.d(TAG, "Exit getSound");

        return soundId;

    }

    // ===========================
    // getMoveCost
    // ===========================
    int getMoveCost(MapSquare ms) {

        Log.d(TAG, "Enter getMoveCost");

        int moveCost = 0;

        // see if unit has enough move
        switch (ms.getTerrainType()) {
            case R.drawable.scrub:
                moveCost = 1;
                break;
            case R.drawable.woods:
                moveCost = 2;
                break;
            case R.drawable.rocky:
                moveCost = 3;
        }

        Log.d(TAG, "Exit getMoveCost. moveCost = " + Integer.toString(moveCost));
        return moveCost;

    }

    // ===========================
    // IsMapSpotTaken
    // ===========================
    boolean isMapSpotTaken(int row, int col) {

        Log.d(TAG, "Enter isMapSpotTaken");

        MapSquare ms = null;

        // look at map square array to see if a unit already there
        ms = _mapSquares[getArrayPosforRowCol(row, col)];

        Log.d(TAG, "Exit isMapSpotTaken.");
        return (ms.getUnit() != null);

    }


    // ===========================
    // isAbleToMove
    // ===========================
    boolean isAbleToMoveInto(MapSquare ms, Unit u) {

        Log.d(TAG, "Enter isAbleToMoveInfo");

        int moveRequired = 0;

        // see if unit has enough move
        switch (ms.getTerrainType()) {
            case R.drawable.scrub:
                moveRequired = 1;
                break;
            case R.drawable.woods:
                moveRequired = 2;
                break;
            case R.drawable.rocky:
                moveRequired = 3;
        }

        Log.d(TAG, "Exit isAbleToMoveInto. u.getRemainingMove() = " + Integer.toString(u.getRemainingMove())+ ", moveRequired = " + Integer.toString(moveRequired));
        return (u.getRemainingMove() >= moveRequired);

    }


    // ===========================
    // doOpForAttack
    // ===========================
    void doOpForAttack(MapSquare fromSq, MapSquare toSq) {

        int attackNum = 0;
        int roll = 0;
        int damage = 0;
        String attackMsg = "";
        int pos = 0;
        ImageView v = null;
        Unit redUnit = fromSq.getUnit();
        Unit blueUnit = toSq.getUnit();

        // attack!
        // build up attack number. Use base value, minus any terrain modifier, minus effectiveness
        attackNum = redUnit.getAttackNumber();
        switch (toSq.getTerrainType()) {
            case R.drawable.woods:
            case R.drawable.rocky:
                attackNum = attackNum - 1;
        }
        switch (redUnit.getEff()) {
            case Unit.EFF_AMBER:
                attackNum = attackNum - 1;
                break;
            case Unit.EFF_RED:
                attackNum = attackNum - 2;
        }

        roll = getRandomNumber(10, 1);
        Log.d(TAG, "attack number: " + Integer.toString(attackNum));
        Log.d(TAG, "attack roll: " + Integer.toString(roll));


        // since this unit is attacking, they now get shown (whether hidden or not)
        pos = getArrayPosforRowCol(fromSq.getRow(), fromSq.getCol());
        v = (ImageView) _mapAdapter.getItem(pos);
        v.setImageResource(getUnitIcon(redUnit));
        selectUnit(pos);

        // determine whether hit took place and then any damage
        if (roll <= attackNum) {
            // hit!
            damage = getRandomNumber(10, 1);
            Log.d(TAG, "damage roll: " + Integer.toString(damage));
            if (damage <= 8) {
                blueUnit.setEff(blueUnit.getEff() - 1);
                Log.d(TAG, blueUnit.getName() + " effectiveness now " + blueUnit.getEff());
                attackMsg = blueUnit.getName() + " was hit and took damage.";
            } else {
                attackMsg = blueUnit.getName() + " was attacked, but did not suffer any damage.";
            }

            // certain types of unit cause supression
            switch (redUnit.getType()) {
                case Unit.TYPE_SNIPER:
                case Unit.TYPE_MG:
                    blueUnit.setIsSuppressed(true);
                    blueUnit.setTurnSuppressed(_turn);
                    attackMsg += " It was also suppressed (will not be able to attack or move again this turn).";
            }
            // update array
            pos = getArrayPosforRowCol(toSq.getRow(), toSq.getCol());
            _mapSquares[pos].setUnit(blueUnit);
            // also, grab the imageview and update the image (especially if effectiveness changed)
            v = (ImageView) _mapAdapter.getItem(pos);
            v.setImageResource(getUnitIcon(blueUnit));

        } else {
            attackMsg = blueUnit.getName() + " was attacked, but did not suffer any damage.";

        }

        // update label
        _actionText.setText(attackMsg);

        // make the attackimg unit visible and set their attacked flag
        redUnit.setHasAttacked(true);
        redUnit.setIsVisible(true);

        // deselect the red unit
        pos = getArrayPosforRowCol(fromSq.getRow(), fromSq.getCol());
        deselectUnit(pos);

        // pick a sound to play
        MediaPlayer mediaPlayer = MediaPlayer.create(this, getSound(redUnit));
        mediaPlayer.start();

    }

    // ===========================
    // doOpForTurn
    // ===========================
    void doOpForTurn() {

        Unit redUnit = null;
        MapSquare fromSq = null;
        MapSquare toSq = null;
        int row = 0;
        int col = 0;
        boolean justAttacked = false;
        int adjRow = 0;
        int adjCol = 0;
        int pos = 0;
        Unit blueUnit = null;
        int attackNum = 0;
        int roll = 0;
        int damage = 0;
        String attackMsg = "";
        ImageView v = null;

        // loop through map looking for enemy units

        // iterate through array
        for (int ctr = 0; ctr < MAX_ARRAY; ctr++) {

            redUnit = _mapSquares[ctr].getUnit();
            // have to meet all these conditions ... is a unit there that is computer and not suppressed
            if ((redUnit != null) && (redUnit.getOwner() == Unit.OWNER_OPFOR) && (!redUnit.getIsSuppressed()) && (redUnit.getEff() > Unit.EFF_BLACK)) {

                // found one, so get the row and column to see what is around it
                fromSq = _mapSquares[ctr];
                row = fromSq.getRow();
                col = fromSq.getCol();

                // #1 any units adjacent to player unit attack
                // look around it in each direction
                justAttacked = false;
                for (int x = -1; x <= 1; x++) {
                    // quick hack; if they attacked, broke out of inner loop but now we need to break out of outer loop
                    if (justAttacked) {
                        break;
                    }
                    for (int y = -1; y <= 1; y++) {
                        adjRow = (row + x);
                        adjCol = (col + y);
                        // make sure we didn't go out of bounds
                        if (adjRow < 1) {
                            adjRow = 1;
                        }
                        if (adjRow > MAX_ROWS) {
                            adjRow = MAX_ROWS;
                        }
                        if (adjCol < 1) {
                            adjCol = 1;
                        }
                        if (adjCol > MAX_COLS) {
                            adjCol = MAX_COLS;
                        }

                        // get the map array pos for row
                        pos = getArrayPosforRowCol(adjRow, adjCol);
                        // is there a player unit there?
                        toSq = _mapSquares[pos];
                        blueUnit = toSq.getUnit();

                        if ((blueUnit != null) && (blueUnit.getOwner() == Unit.OWNER_PLAYER)) {
                            // jump to attack code
                            doOpForAttack(fromSq, toSq);
                            // they don't want to move away from player unit, do set flag
                            justAttacked = true;
                            // break out of loop, we're done
                            break;
                        }
                    }

                }

                // #2 if we get here and justattacked still false, then nothing around them, so now see if any player units in range
                if (!justAttacked) {
                    // enemy unit that hasn't attacked yet, so now starting from where they are, find any player units within range
                    for (int x = 0; x < MAX_ARRAY; x++) {
                        // now, find player units within range
                        toSq = _mapSquares[x];
                        blueUnit = toSq.getUnit();
                        if ((blueUnit != null) && (blueUnit.getOwner() == Unit.OWNER_PLAYER) && (getDistanceBetweenSquares(fromSq.getRow(), fromSq.getCol(), toSq.getRow(), toSq.getCol()) < redUnit.getAttackRange())) {
                            // jump to attack code
                            doOpForAttack(fromSq, toSq);
                            justAttacked = true;
                            break;
                        }

                    }

                }

                if (justAttacked) {
                    // make the attackimg unit visible and set their attacked flag
                    redUnit.setHasAttacked(true);
                    redUnit.setIsVisible(true);
                    // pick a sound to play
                    MediaPlayer mediaPlayer = MediaPlayer.create(this, getSound(redUnit));
                    mediaPlayer.start();
                    // release media player
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                // now, figure out movement -- if they just attacked, don't move
                if (!justAttacked) {
                    // units with aggfac of 3, move towards nearest player unit, aggfac 2 have 50/50 shot and aggfac of 1 have 50/50 shot to stay in place or move away

                    // after moving, see if they are next to or within range of player to attack


                }



            }

        }

        return;

    }

    // ===========================
    // doEndTurn
    // ===========================
    void doEndTurn() {

        Log.d(TAG, "Enter doEndTurn");

        Unit u = null;
        MapSquare ms = null;
        int pos = 0;

        // loop through all the units and reset move, attack flag and maybe suppression flag
        for (int ctr = 0; ctr < MAX_ARRAY; ctr++) {
            ms = _mapSquares[ctr];
            u = ms.getUnit();
            if (u != null) {
                u.setRemainingMove(u.getMaxMove());
                u.setHasAttacked(false);
                _mapSquares[ctr].setUnit(u);
                if (u.getIsSuppressed()) {
                    if (_turn - u.getTurnSuppressed() > 1) {
                        u.setIsSuppressed(false);
                    }
                }
            }
        }

        // if any active unit, deselect it
        if (_activeSq != null) {
            pos = getArrayPosforRowCol(_activeSq.getRow(), _activeSq.getCol());
            deselectUnit(pos);
            _activeSq = null;
        }
        if (_activeUnit != null) {
            _activeUnit = null;
        }

        // clear out action text
        _actionText.setText("");

        // do the computer part of this turn
        _turnString  = "Movement To Contact - Turn " + Integer.toString(_turn) + " (OpFor)";
        _turnText.setText(_turnString);

        // actually do computer part of the turn
        doOpForTurn();

        // increment turn counter
        _turn++;

        _turnString  = "Movement To Contact - Turn " + Integer.toString(_turn) + " (You)";
        _turnText.setText(_turnString);


        Log.d(TAG, "Exit doEndTurn");



    }

    // ===========================
    // doMove
    // ===========================
    boolean doMove(MapSquare fromSq, MapSquare toSq) {

        Log.d(TAG, "Enter doMove");

        int posFrom = 0;
        int posTo = 0;
        Unit u = null;
        MapSquare ms = null;
        int icon = 0;
        ImageView v = null;

        // first, figure out which array positions we're talking about
        posFrom = getArrayPosforRowCol(fromSq.getRow(), fromSq.getCol());
        posTo = getArrayPosforRowCol(toSq.getRow(), toSq.getCol());

        // next, decide whether there's enough movement left to do it
        if (isAbleToMoveInto(_mapSquares[posTo], _mapSquares[posFrom].getUnit())) {

            // store what was there, then null it out
            ms = _mapSquares[posFrom];
            u = ms.getUnit();
            ms.setUnit(null);
            _mapSquares[posFrom] = ms;

            // get imageview for current square and remove the unit
            v = (ImageView) _mapAdapter.getItem(posFrom);
            v.setPadding(2,2,2,2);
            v.setBackgroundColor(Color.BLACK);
            v.setImageResource(ms.getTerrainType());

            // now drop that unit into new position
            ms = _mapSquares[posTo];
            u.setRemainingMove((u.getRemainingMove() - getMoveCost(ms)));
            ms.setUnit(u);
            _mapSquares[posTo] = ms;

            // re-draw icon in new spot
            v = (ImageView) _mapAdapter.getItem(posTo);
            v.setPadding(5,5,5,5);
            v.setBackgroundColor(Color.BLUE);
            v.setImageResource(getUnitIcon(u));

            // now set active
            _activeUnit = u;
            _activeSq = _mapSquares[posTo];

            // we did a move
            return true;

        }
        else {
            // no move occured
            return false;
        }

    }

    // ===========================
    // doAttack
    // ===========================
    void doAttack(MapSquare fromSq, MapSquare toSq) {

        Log.d(TAG, "Enter doAttack");

        Unit redUnit = toSq.getUnit();
        Unit blueUnit = fromSq.getUnit();
        ImageView v = null;
        int pos = 0;
        int icon = 0;
        int attackNum = 0;
        int roll = 0;
        int damage = 0;
        String attackMsg = "";

        // first, if enemy wasn't visible, it now is, so draw it
        pos = getArrayPosforRowCol(toSq.getRow(), toSq.getCol());
        v = (ImageView) _mapAdapter.getItem(pos);

        try {
            icon = getUnitIcon(redUnit);
            v.setImageResource(icon);
        }
        catch (Exception e) {
            v.setImageResource(toSq.getTerrainType());
        }

        // next, our unit has attacked, so set property
        blueUnit.setHasAttacked(true);

        // build up attack number. Use base value, minus any terrain modifier, minus effectiveness
        attackNum = blueUnit.getAttackNumber();
        switch (toSq.getTerrainType()) {
            case R.drawable.woods:
            case R.drawable.rocky:
                attackNum = attackNum - 1;
        }
        switch (blueUnit.getEff()) {
            case Unit.EFF_AMBER:
                attackNum = attackNum - 1;
                break;
            case Unit.EFF_RED:
                attackNum = attackNum - 2;
        }

        roll = getRandomNumber(10, 1);
        Log.d(TAG, "attack number: " + Integer.toString(attackNum));
        Log.d(TAG, "attack roll: " + Integer.toString(roll));

        // pick a sound to play
        MediaPlayer mediaPlayer = MediaPlayer.create(this, getSound(blueUnit));
        mediaPlayer.start();

        // determine whether hit took place and then any damage
        if (roll <= attackNum) {
            // hit!
            damage = getRandomNumber(10, 1);
            Log.d(TAG, "damage roll: " + Integer.toString(damage));
            if (damage <= 8) {
                redUnit.setEff(redUnit.getEff() - 1);
                Log.d(TAG, "Enemy unit effectiveness now " + redUnit.getEff());
                attackMsg = "The unit was hit and took damage.";
            }
            else {
                attackMsg = "The unit was attacked, but did not suffer any damage.";
            }

            // certain types of unit cause supression
            switch (blueUnit.getType()) {
                case Unit.TYPE_MORTAR:
                case Unit.TYPE_MG:
                    redUnit.setIsSuppressed(true);
                    redUnit.setTurnSuppressed(_turn);
                    attackMsg += " It was also suppressed (cannot attack or move for two turns).";
            }
            // update array
            toSq.setUnit(redUnit);
        }
        else {
            attackMsg = "The unit was attacked, but did not suffer any damage.";

        }

        // if combat effectiveness now black, just get rid of it
        if (redUnit.getEff() == Unit.EFF_BLACK) {
            v.setImageResource(toSq.getTerrainType());
            attackMsg = "The unit was hit, took damage and is no longer combat effective.";
            toSq.setUnit(null);

        }

        _actionText.setText(attackMsg);


        // release media player
//        mediaPlayer.reset();
  //      mediaPlayer.release();
    //    mediaPlayer = null;

        Log.d(TAG, "Exit doAttack");


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

    // ==========================
    // is the toSq map spot adjaent to the fromSqu?
    // ==========================
    boolean isAdjacent(MapSquare fromSq, MapSquare toSq) {

        Log.d(TAG, "Enter isAdjacent");

        if ((fromSq == null) || (toSq == null)) {
            return false;
        }

        int fromRow = fromSq.getRow();
        int fromCol = fromSq.getCol();
        boolean isAdj = false;

        // loop through to find all possible combinations
        for (int r = fromRow - 1; r < fromRow + 2; r++) {
            for (int c = fromCol - 1; c < fromCol + 2; c++) {
                // make sure within grid bounds
                if ((r > 0) && (r <= MAX_ROWS) && (c > 0) && (c <= MAX_COLS)) {
                    if ((r == toSq.getRow()) && (c == toSq.getCol())) {
                        isAdj = true;
                        break;
                    }
                }
            }
        }

        Log.d(TAG, "Exit isAdjacent. isAjd = " + Boolean.toString(isAdj));
        return isAdj;

    }

    // ==========================
    // is there a unit in this spot?
    // ==========================
    boolean isEmpty(MapSquare toSq) {
        return (toSq.getUnit() == null);

    }

    // ==========================
    // select unit in map square by changing border
    // ==========================
    void selectUnit(int pos) {
        ImageView v = (ImageView) _mapAdapter.getItem(pos);
        v.setPadding(5,5,5,5);
        v.setBackgroundColor(Color.BLUE);
    }

    // ==========================
    // deselect unit in map square by changing border
    // ==========================
    void deselectUnit(int pos) {
        ImageView v = (ImageView) _mapAdapter.getItem(pos);
        v.setPadding(2,2,2,2);
        v.setBackgroundColor(Color.BLACK);
    }


    // ===========================
    // createNewGame
    // ===========================
    void newGame() {
        // create array of map objects
        _mapSquares = new MapSquare[MAX_ARRAY];
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
            _imageIds[ctr] = terrainType;
             // now add to array
            _mapSquares[ctr] = ms;

        }

        // next, manually set friendly and enemy units
        setUpFriendlyUnits();
        setUpEnemyUnits();

        // set the initial turn text
        _turnString  = "Movement To Contact - Turn " + Integer.toString(_turn) + " (You)";
        _actionString = "";


    }

    // ===========================
    // resumeGame
    // ===========================
    void resumeGame() {

    }

    // ===========================
    // doClick
    // ===========================
    void doClick(View v, int position) {

        Log.d(TAG, "Enter doClick");

        MapSquare ms = null;
        Unit u = null;
        int owner = Unit.OWNER_PLAYER;
        int icon = 0;
        ImageView imgClicked =  (ImageView) v;

        // figure out what they clicked on
        ms = _mapSquares[position];
        u = ms.getUnit();

        // #1 if active square, quick check to see if we selected same one
        if ((_activeSq != null) && (_activeSq == ms)) {
            deselectUnit(position);
            _actionText.setText("");
            _activeSq = null;
            _activeUnit = null;
            return;
        }

        // #2 if no active unit, the current one is now active (assuming player owns it)
        if ((_activeUnit == null) && (u != null) && (u.getOwner() == Unit.OWNER_PLAYER)) {
            _activeUnit = u;
            _activeSq = ms;
            selectUnit(position);
            if (_activeUnit.getEff() == Unit.EFF_BLACK) {
                _actionText.setText(_activeUnit.getName() + " is no longer combat effective -- unable to move or attack.");
                _activeUnit = null;
                _activeSq = null;
                deselectUnit(position);
                return;
            }
            _actionText.setText(u.getName() + ": " + u.getRemainingMove() + " move points remaining");

            return;
        }

        // #3 if we have active square and unit, did they select on adjacent map square?
        if (isAdjacent(_activeSq, ms)) {
            // yes, adjacent
            // (a) if friendly there, switch to that unit
            if ((ms.getUnit() != null) && (ms.getUnit().getOwner() == Unit.OWNER_PLAYER)) {
                // make sure unit able to do anything
                if (_activeUnit.getIsSuppressed()) {
                    _actionText.setText(_activeUnit.getName() + " is surpressed -- unable to move or attack.");
                    return;
                }
                deselectUnit(getArrayPosforRowCol(_activeSq.getRow(), _activeSq.getCol()));
                selectUnit(position);
                _actionText.setText(u.getName() + ": " + u.getRemainingMove() + " move points remaining");
                _activeUnit = u;
                _activeSq = ms;
                return;
            }
            // (b) if empty, move in (assuming move left)
            if (ms.getUnit() == null) {
                // make sure unit able to do anything
                if (_activeUnit.getIsSuppressed()) {
                    _actionText.setText(_activeUnit.getName() + " is surpressed -- unable to move or attack.");
                    return;
                }
                if (doMove(_activeSq, ms)) {
                    _activeUnit = ms.getUnit();
                    _actionText.setText(_activeUnit.getName() + ": " + _activeUnit.getRemainingMove() + " move points remaining");
                }
                else {
                    _actionText.setText(_activeUnit.getName() + " is unable to move due to lack of remaining move points");
                }
                return;
            }
            // (c) if enemy there, attack
            if ((ms.getUnit() != null) && (ms.getUnit().getOwner() == Unit.OWNER_OPFOR)) {
                // make sure unit able to do anything
                if (_activeUnit.getIsSuppressed()) {
                    _actionText.setText(_activeUnit.getName() + "ß is surpressed -- unable to move or attack.");
                    return;
                }
                // make sure unit can attack (based on combat effectiveness)
                if (_activeUnit.getEff() == Unit.EFF_BLACK) {
                    _actionText.setText(_activeUnit.getName() + " is no longer combat effective -- unable to move or attack.");
                    return;
                }
                // if enemy not visible, we stumbled onto them so they get a first shot
                if (ms.getUnit().getIsVisible() == false) {
                    // TODO: enemy gets first shot in
                }
                if (_activeUnit.getHasAttacked() == false) {
                    doAttack(_activeSq, ms);
                    return;
                }
                else {
                    _actionText.setText(_activeUnit.getName() + " has already attacked this turn.");
                    return;
                }
            }

            // (d) otherwise, ignore
            return;

        }
        else {
            // not adjacent
            // (a) if friendly, switch focus
            if ((ms.getUnit() != null) && (ms.getUnit().getOwner() == Unit.OWNER_PLAYER)) {
                deselectUnit(getArrayPosforRowCol(_activeSq.getRow(), _activeSq.getCol()));
                selectUnit(position);
                _activeUnit = u;
                _activeSq = ms;
                _actionText.setText(_activeUnit.getName() + " has " + _activeUnit.getRemainingMove() + " move points remaining");
                return;
            }
            // (b) if enemy there and in range, attack
            if ((_activeSq != null) && (getDistanceBetweenSquares(_activeSq.getRow(), _activeSq.getCol(), ms.getRow(), ms.getCol()) <= _activeUnit.getAttackRange())) {
                doAttack(_activeSq, ms);
                return;
            }
            else if (_activeSq != null)  {
                _actionText.setText(_activeUnit.getName() + " is out of range to attack. You must be within " + Integer.toString(_activeUnit.getAttackRange())  + " squares.");
            }

            // (c) otherwise, ignore
            return;

        }

    }

    // ===========================
    // onBackPressed
    // ===========================
    @Override
    public void onBackPressed() {

        Log.d(TAG, "Enter onBackPressed");
        // TODO: persist the game to CSV
        Log.d(TAG, "Exit onBackPressed");
        finish();

    }

    // ===========================
    // onCreate
    // ===========================
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);
        Button buttonEndTurn = (Button) findViewById(R.id.button1);

        // get the two textviews for status
        _turnText = (TextView) findViewById(R.id.textView2);
        _actionText = (TextView) findViewById(R.id.textView8);

        // new game?
        if (_myIntent != null) {
            if (_myIntent.getBooleanExtra("NEW_GAME", Boolean.TRUE)) {
                newGame();
            } else {
                resumeGame();
            }
        }
        else {
            newGame();
        }

        // set the mapadapter to build out the initial map
        _gridView = (GridView) findViewById(R.id.gridView1);
        // pass in the array of image ids
        _mapAdapter.setImageArray(_imageIds);
        _gridView.setAdapter(_mapAdapter);

        // set the textviews
        _turnText.setText(_turnString);
        _actionText.setText(_actionString);

        // set the long press
        _gridView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v,
                                           int position, long id) {
                Log.d(TAG, "LongClick at position: " + Integer.toString(position));
                // TODO: Pop up dialog with terrain and/or unit details
                return true;
            }
        });


        // set the onlick
        _gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Log.d(TAG, "Clicked on position: " + Integer.toString(position));
                doClick(v, position);

            }
        });

        // button click
        buttonEndTurn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doEndTurn();

            }
        });

    }
}
