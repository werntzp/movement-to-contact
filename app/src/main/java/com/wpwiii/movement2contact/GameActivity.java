package com.wpwiii.movement2contact;

import com.wpwiii.movement2contact.Prefs;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.media.MediaPlayer;
import android.os.Handler;
import android.app.AlertDialog;
import android.content.DialogInterface;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import android.content.Context;

class Utils {

    // Delay mechanism

    interface DelayCallback{
        void afterDelay();
    }

    static void delay(int secs, final DelayCallback delayCallback){
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

    static final int OWNER_PLAYER = 0;
    static final int OWNER_OPFOR = 1;

    static final int TYPE_INF = 0;
    static final int TYPE_MG = 1;
    static final int TYPE_HQ = 2;
    static final int TYPE_SNIPER = 3;
    static final int TYPE_MORTAR = 4;
    
    static final int SIZE_SQUAD = 0;
    static final int SIZE_PLATOON = 1;
    static final int SIZE_SECTION = 2;
    static final int SIZE_TEAM = 3;

    static final int EFF_GREEN = 3;
    static final int EFF_AMBER = 2;
    static final int EFF_RED = 1;
    static final int EFF_BLACK = 0;

    static final int AGG_VERY = 3;
    static final int AGG_SOME = 2;
    static final int AGG_NO = 1;

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

    void setIsSuppressed(boolean val) { _isSuppressed = val; }
    void setHasAttacked(boolean val) {
        _hasAttacked = val;
    }
    void setRemainingMove(int val) {
        _remainingMove = val;
    }
    void setIsActive(boolean val) {
        _isActive = val;
    }
    void setIsVisible(boolean val) {
        _isVisible = val;
    }
    void setEff(int val) {
        _eff = val;
    }
    void setTurnSuppressed(int val) { _turnSuppressed = val; }
    void setAggression(int val) { _aggression = val; }

    int getAttackNumber() {
        return _attackNumber;
    }
    boolean getIsSuppressed() {
        return _isSuppressed;
    }
    boolean getHasAttacked() {
        return _hasAttacked;
    }
    int getAttackRange() {
        return _attackRange;
    }
    int getRemainingMove() {
        return _remainingMove;
    }
    int getMaxMove() {
        return _maxMove;
    }
    boolean getIsActive() {
        return _isActive;
    }
    boolean getIsVisible() {
        return _isVisible;
    }
    int getType() {
        return _type; 
    }
    int getSize() {
        return _size; 
    }
    int getEff() {
        return _eff; 
    }
    int getOwner() {
        return _owner; 
    }
    String getName() { return _name; }
    int getTurnSuppressed() { return _turnSuppressed; }
    int getAggression() { return _aggression; }

}


// class for each map square
class MapSquare {

    private int _terrainType = 0;
    private Unit _unit = null;
    private int _row = 0;
    private int _col = 0;

    void setTerrainType(int val) {
        _terrainType = val;
    }
    void setRow(int val) {
        _row = val;
    }
    void setCol(int val) {
        _col = val;
    }
    void setUnit(Unit val) {
        _unit = val;
    }

    Unit getUnit() {
        return _unit;
    }
    int getCol() {
        return _col;
    }
    int getRow() {
        return _row;
    }
    int getTerrainType() {
        return _terrainType;
    }

};

// main class
public class GameActivity extends AppCompatActivity {

    GridView _gridView;
    MapAdapter _mapAdapter = new MapAdapter(this);
    private static final String TAG = "GameActivity";
    private static final String SAVEGAMEFILENAME = "savegame.dat";
    public static final int MAX_ROWS = 15;
    public static final int MAX_COLS = 10;
    public static final int MAX_ARRAY = 150;
    public static final int MAX_TURNS = 20;
    public static final int GAME_OVER_WIN = 1;
    public static final int GAME_OVER_LOSE = 0;
    Integer[] _mapArray = new Integer[MAX_ARRAY];
    TextView _turnText;
    TextView _actionText;
    String _actionString = "";
    String _turnString = "";
    Intent _myIntent = getIntent();
    MapSquare[] _mapSquares;
    Integer[] _imageIds = new Integer[MAX_ARRAY];
    Unit _activeUnit = null;
    MapSquare _activeSq = null;
    int _turn = 1;
    MediaPlayer _mpInfPlatoon = null;
    MediaPlayer _mpInfSquad = null;
    MediaPlayer _mpMG = null;
    MediaPlayer _mpMortar = null;
    MediaPlayer _mpSniper = null;
    int _enemyUnitCount = 0;
    boolean _persistGame = true;
    boolean _enemyTurn = false;

    // ===========================
    // calculateScore
    // ===========================
    int calculateStars() {

        int stars = 0;
        int score = 0;
        Unit blueUnit = null;

        // subtract how many turns from 15
        score = (15 - _turn);
        // iterate through units and get a +1 for each unit still at green, -1 for any black units
        for (int ctr = 0; ctr < MAX_ARRAY; ctr++) {

            blueUnit = _mapSquares[ctr].getUnit();
            // see if unit there and belongs to player
            if ((blueUnit != null) && (blueUnit.getOwner() == Unit.OWNER_PLAYER)) {
                switch (blueUnit.getEff()) {
                    case Unit.EFF_GREEN:
                        score += 1;
                        break;
                    case Unit.EFF_BLACK:
                        score -= 1;
                }
            }

        }

        // now, take score to generate stars
        if (score >= 10) {
            stars = 3;
        }
        else if ((score < 10) && (score > 5)) {
            stars = 2;
        }
        else {
            stars = 1;
        }

        return stars;
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
        int secs = 2;
        int img = 0;
        boolean isHit = false;
        boolean isSuppressed = false;
        boolean isNoLongerCombatEffective = false;

        // first, if enemy wasn't visible, it now is, so draw it
        pos = getArrayPosforRowCol(toSq.getRow(), toSq.getCol());
        v = (ImageView) _mapAdapter.getItem(pos);

        try {
            img = getUnitIcon(redUnit);
            v.setImageResource(img);
            _mapAdapter.setItem(v, img, pos);
        } catch (Exception e) {
            img = toSq.getTerrainType();
            v.setImageResource(img);
            _mapAdapter.setItem(v, img, pos);
        }

        // also, highight it in red
        selectUnit(pos, Color.RED);
        final int unitPos = pos;

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
        playSound(blueUnit);

        // issue #39 - need to simulate attack by mortar on empty square, but can skip a lot of this if that is the case
        if (redUnit != null) {
            // determine whether hit took place and then any damage
            if (roll <= attackNum) {
                // hit!
                isHit = true;
                damage = getRandomNumber(10, 1);
                Log.d(TAG, "damage roll: " + Integer.toString(damage));
                if (damage <= 8) {
                    redUnit.setEff(redUnit.getEff() - 1);
                    Log.d(TAG, "Enemy unit effectiveness now " + redUnit.getEff());
                }

                // set the unit to visible since we hit it
                redUnit.setIsVisible(true);
                // certain types of unit cause supression
                switch (blueUnit.getType()) {
                    case Unit.TYPE_MORTAR:
                    case Unit.TYPE_MG:
                        redUnit.setIsSuppressed(true);
                        redUnit.setTurnSuppressed(_turn);
                        isSuppressed = true;
                }
                // update array
                _mapSquares[pos].setUnit(redUnit);
            }
        }

        // if combat effectiveness now black, just get rid of it
        if ((redUnit != null) && (redUnit.getEff() == Unit.EFF_BLACK)) {
            img = toSq.getTerrainType();
            v.setImageResource(img);
            _mapAdapter.setItem(v, img, pos);
            isNoLongerCombatEffective = true;
            toSq.setUnit(null);
            _mapSquares[pos] = toSq;
            secs = 1;
            deselectUnit(unitPos);
            // reduce number of enemy units by one
            _enemyUnitCount--;
            Log.d(TAG, "There are now " + _enemyUnitCount + " enemy units left");
        } else {
            Utils.delay(secs, new Utils.DelayCallback() {
                @Override
                public void afterDelay() {
                    deselectUnit(unitPos);

                }
            });
        }


        // issue 34 - if player attacking enemy, tell less info
        if (blueUnit.getOwner() == Unit.OWNER_PLAYER) {
            // next, see if unit was there
            if (redUnit == null) {
                attackMsg = getString(R.string.attackmsg_nounit);
            } else {
                attackMsg = String.format(getString(R.string.player_attackmsg), blueUnit.getName(), redUnit.getName());
            }
        } else {
            // if enemy attacking us, tell more info
            if ((isHit) && (damage <= 8) && (isSuppressed)) {
                // they were hit and suppressed
                attackMsg = String.format(getString(R.string.attackmsg_sup), redUnit.getName());
            } else if ((isHit) && (damage <= 8) && (!isSuppressed)) {
                // they were hit but not suppressed
                attackMsg = String.format(getString(R.string.attackmsg_nosup), redUnit.getName());
            } else if (isNoLongerCombatEffective) {
                // destroyed
                attackMsg = String.format(getString(R.string.attackmsg_killed), redUnit.getName());
            }
            else {
                // not even hit or hit but no damage, so same message
                attackMsg = String.format(getString(R.string.attackmsg_miss), redUnit.getName());
            }
        }

        /*
        try {
            Thread.sleep(1000);
        }
        catch (Exception e) {
            // do nothing
        }
        */

        _actionText.setText(attackMsg);

        // are there any enemy units left?
        if (_enemyUnitCount == 0) {
            showEndGameDialog(GAME_OVER_WIN);
        }

        Log.d(TAG, "Exit doAttack");

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

        // issue #2
        // if we're in middle of enemy turn, don't handle clicks
        if (_enemyTurn) {
            return;
        }

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
            selectUnit(position, Color.BLUE);
            if (_activeUnit.getEff() == Unit.EFF_BLACK) {
                _actionText.setText(String.format(getString(R.string.combat_ineffective), _activeUnit.getName()));
                _activeUnit = null;
                _activeSq = null;
                deselectUnit(position);
                return;
            }
            if (_activeUnit.getHasAttacked()) {
                _actionText.setText(String.format(getString(R.string.move_left_cantattack), _activeUnit.getName(), _activeUnit.getRemainingMove()));
            }
            else {
                _actionText.setText(String.format(getString(R.string.move_left_canattack), _activeUnit.getName(), _activeUnit.getRemainingMove()));
            }
            return;
        }

        // #3 if we have active square and unit, did they select on adjacent map square?
        if (isAdjacent(_activeSq, ms)) {
            // yes, adjacent
            // (a) if friendly there, switch to that unit
            if ((ms.getUnit() != null) && (ms.getUnit().getOwner() == Unit.OWNER_PLAYER)) {
                // make sure unit able to do anything
                if (_activeUnit.getIsSuppressed()) {
                    _actionText.setText(String.format(getString(R.string.suppressed), _activeUnit.getName()));
                    return;
                }
                deselectUnit(getArrayPosforRowCol(_activeSq.getRow(), _activeSq.getCol()));
                selectUnit(position, Color.BLUE);
                if (_activeUnit.getHasAttacked()) {
                    _actionText.setText(String.format(getString(R.string.move_left_cantattack), _activeUnit.getName(), _activeUnit.getRemainingMove()));
                }
                else {
                    _actionText.setText(String.format(getString(R.string.move_left_canattack), _activeUnit.getName(), _activeUnit.getRemainingMove()));
                }
                _activeUnit = u;
                _activeSq = ms;
                return;
            }
            // (b) if empty, move in (assuming move left)
            if (ms.getUnit() == null) {
                // make sure unit able to do anything
                if (_activeUnit.getIsSuppressed()) {
                    _actionText.setText(String.format(getString(R.string.suppressed), _activeUnit.getName()));
                    return;
                }
                if (doMove(_activeSq, ms)) {
                    _activeUnit = ms.getUnit();
                    if (_activeUnit.getHasAttacked()) {
                        _actionText.setText(String.format(getString(R.string.move_left_cantattack), _activeUnit.getName(), _activeUnit.getRemainingMove()));
                    }
                    else {
                        _actionText.setText(String.format(getString(R.string.move_left_canattack), _activeUnit.getName(), _activeUnit.getRemainingMove()));
                    }
                }

                return;
            }
            // (c) if enemy there, attack
            if ((ms.getUnit() != null) && (ms.getUnit().getOwner() == Unit.OWNER_OPFOR)) {
                // make sure unit able to do anything
                if (_activeUnit.getIsSuppressed()) {
                    _actionText.setText(String.format(getString(R.string.suppressed), _activeUnit.getName()));
                    return;
                }
                // make sure unit can attack (based on combat effectiveness)
                if (_activeUnit.getEff() == Unit.EFF_BLACK) {
                    _actionText.setText(String.format(getString(R.string.combat_ineffective), _activeUnit.getName()));
                    return;
                }
                // if enemy not visible, we stumbled onto them so they get a first shot
                if (!ms.getUnit().getIsVisible()) {
                    doOpForAttack(ms, _activeSq);
                }
                if (!_activeUnit.getHasAttacked()) {
                    doAttack(_activeSq, ms);
                }
                else {
                    _actionText.setText(String.format(getString(R.string.already_attacked), _activeUnit.getName()));
                }
            }

            // (d) otherwise, ignore

        }
        else {
            // not adjacent
            // (a) if friendly, switch focus
            if ((ms.getUnit() != null) && (ms.getUnit().getOwner() == Unit.OWNER_PLAYER)) {
                deselectUnit(getArrayPosforRowCol(_activeSq.getRow(), _activeSq.getCol()));
                selectUnit(position, Color.BLUE);
                _activeUnit = u;
                _activeSq = ms;
                if (_activeUnit.getHasAttacked()) {
                    _actionText.setText(String.format(getString(R.string.move_left_cantattack), _activeUnit.getName(), _activeUnit.getRemainingMove()));
                }
                else {
                    _actionText.setText(String.format(getString(R.string.move_left_canattack), _activeUnit.getName(), _activeUnit.getRemainingMove()));
                }
                return;
            }
            // (b) if enemy there and in range, attack
            if ((_activeSq != null) && (ms.getUnit() != null) && (getDistanceBetweenSquares(_activeSq.getRow(), _activeSq.getCol(), ms.getRow(), ms.getCol()) <= _activeUnit.getAttackRange())) {
                // next, need to see if enemy unit visible; if not, only mortar can do recon by fire and attack
                if (ms.getUnit().getIsVisible()) {
                    doAttack(_activeSq, ms);
                    return;
                }
                else {
                    if (_activeUnit.getType() == Unit.TYPE_MORTAR) {
                        doAttack(_activeSq, ms);
                    }
                    else {
                        // ignore
                    }
                }
            }
            // (c) we have a mortar firing into an empty space, which is still legal
            if ((_activeSq != null) && (_activeUnit.getType() == Unit.TYPE_MORTAR) && (getDistanceBetweenSquares(_activeSq.getRow(), _activeSq.getCol(), ms.getRow(), ms.getCol()) <= _activeUnit.getAttackRange())) {
                doAttack(_activeSq, ms);
            }
            // (d)
            else if (_activeSq != null)  {
                _actionText.setText(R.string.out_of_range);
            }

            // (c) otherwise, ignore

        }

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
        int img = 0;
        boolean isSuppressed = false;
        boolean isHit = false;
        ImageView v = null;
        Unit redUnit = fromSq.getUnit();
        Unit blueUnit = toSq.getUnit();
        MapSquare msHQ = null;

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
        img = getUnitIcon(redUnit);
        v.setImageResource(img);
        _mapAdapter.setItem(v, img, pos);
        selectUnit(pos, Color.BLUE);
        _mapAdapter.notifyDataSetChanged();

        // store so we can deselect later
        final int redUnitPos = pos;
        final int blueUnitPos = getArrayPosforRowCol(toSq.getRow(), toSq.getCol());

        // highlight the unit we're attacking
        selectUnit(blueUnitPos, Color.RED);

        // after a 2 second delay, deselect the unit
        Utils.delay(2, new Utils.DelayCallback() {
            @Override
            public void afterDelay() {
                deselectUnit(redUnitPos);
                deselectUnit(blueUnitPos);
            }
        });

        // pick a sound to play
        playSound(redUnit);

        // determine whether hit took place and then any damage
        if (roll <= attackNum) {
            // hit!
            isHit = true;
            damage = getRandomNumber(10, 1);
            Log.d(TAG, "damage roll: " + Integer.toString(damage));
            if (damage <= 8) {
                blueUnit.setEff(blueUnit.getEff() - 1);
                Log.d(TAG, blueUnit.getName() + " effectiveness now " + blueUnit.getEff());
            }

            // certain types of unit cause supression
            switch (redUnit.getType()) {
                case Unit.TYPE_SNIPER:
                case Unit.TYPE_MG:
                    // snipers and mg can suppress, but if target unit is HQ unit or adjacent to HQ unit, no suppression occurs
                    if (blueUnit.getType() == Unit.TYPE_HQ) {
                        // no suppression
                    }
                    else {
                        msHQ = getHQUnitMapSquare();
                        if (isAdjacent(toSq, msHQ)) {
                            // no suppression
                        }
                        else {
                            blueUnit.setIsSuppressed(true);
                            blueUnit.setTurnSuppressed(_turn);
                            isSuppressed = true;
                        }
                    }

            }

            // update array
            pos = getArrayPosforRowCol(toSq.getRow(), toSq.getCol());
            _mapSquares[pos].setUnit(blueUnit);
            // also, grab the imageview and update the image (especially if effectiveness changed)
            v = (ImageView) _mapAdapter.getItem(pos);
            img = getUnitIcon(blueUnit);
            v.setImageResource(img);
            _mapAdapter.setItem(v, img, pos);
            _mapAdapter.notifyDataSetChanged();

        }


        // decide on which message to use
        if ((isHit) && (damage <=8) && (isSuppressed)) {
            // they were hit and suppressed
            attackMsg = String.format(getString(R.string.attackmsg_sup), blueUnit.getName());
        }
        else if ((isHit) && (damage <=8) && (!isSuppressed)) {
            // they were hit but not suppressed
            attackMsg = String.format(getString(R.string.attackmsg_nosup), blueUnit.getName());
        }
        else {
            // not even hit or hit but no damage, so same message
            attackMsg = String.format(getString(R.string.attackmsg_miss), blueUnit.getName());
        }

        // update label
        _actionText.setText(attackMsg);

        // make the attackimg unit visible and set their attacked flag
        redUnit.setHasAttacked(true);
        redUnit.setIsVisible(true);
        _mapSquares[redUnitPos].setUnit(redUnit);

        // freeze the ui
        /*
        try {
            Thread.sleep(2000);
        }
        catch (Exception e) {
            // do nothing
        }
        */

    }

    // ===========================
    // doOpForMove
    // ===========================
    void doOpForMove(MapSquare fromSq, int fromPos) {

        MapSquare ms = fromSq;
        MapSquare toSquare = null;
        int pos = 0;
        Unit u = fromSq.getUnit();
        int row = ms.getRow();
        int col = ms.getCol();
        int toRow = 0;
        int toCol = 0;
        boolean isAbleToMove = false;
        int tries = 0;
        ImageView v = null;
        int img = 0;

        // outer loop
        while ((!isAbleToMove) && (tries <= 3)) {

            // start with where we are at
            toRow = row;
            toCol = col;

            // based on aggression factor, enemy units move towards top of map, side to side or retreat backwards
            switch (u.getAggression()) {
                case 3:
                    // subtract 1 from current row
                    toRow--;
                    // get random number, subtract -1 and then add that to column
                    toCol = toCol + (getRandomNumber(2, 0) - 1);
                    break;
                case 2:
                    // column can go left or right, so pick 0 or 1
                    toCol = getRandomNumber(1, 0);
                    if (toCol == 0) {
                        toCol = col - 1;
                    } else {
                        toCol = col + 1;
                    }
                    break;
                case 1:
                    // subtract 1 from current row
                    toRow++;
                    // get random number, subtract -1 and then add that to column
                    toCol = toCol + (getRandomNumber(2, 0) - 1);
            }

            // make sure toRow and toCol aren't outside map boundaries
            if (toRow > MAX_ROWS) {
                toRow = MAX_ROWS;
            }
            if (toRow < 0) {
                toRow = 0;
            }
            if (toCol > MAX_COLS) {
                toCol = MAX_COLS;
            }
            if (toCol < 0) {
                toCol = 0;
            }

            // find that map square
            pos = getArrayPosforRowCol(toRow, toCol);
            // before we move there, anything in there?
            toSquare = _mapSquares[pos];
            if (toSquare.getUnit() == null) {
                // make the unit visible for now
                u.setIsVisible(true);
                // able to move into that spot; move the unit into that spot
                _mapSquares[pos].setUnit(u);
                _mapSquares[fromPos].setUnit(null);
                // redraw the two icons
                v = (ImageView) _mapAdapter.getItem(fromPos);
                if (v != null) {
                    img = fromSq.getTerrainType();
                    v.setImageResource(img);
                    _mapAdapter.setItem(v, img, fromPos);
                }
                v = (ImageView) _mapAdapter.getItem(pos);
                if (v != null) {
                    img = getUnitIcon(u);
                    v.setImageResource(img);
                    _mapAdapter.setItem(v, img, pos);
                }
                // set flag so we pop out of loop
                isAbleToMove = true;
            }
            else {
                // nope, something there so just increment the tries
                tries++;
            }


        }



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
        boolean stayVisible = false;
        int adjRow;
        int adjCol;
        int pos = 0;
        Unit blueUnit = null;
        int result;
        int attackNum;
        double distanceActual = 0.0;
        double distanceAllowed = 2.0;
        int roll = 0;
        int damage = 0;
        String attackMsg = "";
        ImageView v = null;
        int img = 0;
        Button endTurnButton = (Button) findViewById(R.id.button1);

        Log.d(TAG, "Enter doOpForTurn");

        // set flag here so we know we're in enemy turn
        _enemyTurn = true;

        // disable end turn button
        endTurnButton.setBackgroundResource(R.drawable.button_border_disabled);
        endTurnButton.setTextColor(Color.parseColor("#c0c0c0"));
        endTurnButton.setEnabled(false);

        // loop through map looking for enemy units
        Log.d(TAG, "About to look through units to see which ones can attack or will move");

        // iterate through array
        for (int ctr = 0; ctr < MAX_ARRAY; ctr++) {

            redUnit = _mapSquares[ctr].getUnit();
            // have to meet all these conditions ... is a unit there that is computer and not suppressed; also don't fire at a player unit if they are already combat infective
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

                        if ((blueUnit != null) && (blueUnit.getOwner() == Unit.OWNER_PLAYER) && (blueUnit.getEff() != Unit.EFF_BLACK)) {
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
                        if ((blueUnit != null) && (blueUnit.getOwner() == Unit.OWNER_PLAYER) && (blueUnit.getEff() != Unit.EFF_BLACK) && (getDistanceBetweenSquares(fromSq.getRow(), fromSq.getCol(), toSq.getRow(), toSq.getCol()) < redUnit.getAttackRange())) {
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
                    Log.d(TAG, "redUnit " + redUnit.getName() + " set to visible after attacking");
                    _mapSquares[ctr].setUnit(redUnit);
                }

                // now, figure out movement -- if they just attacked, don't move
                if (!justAttacked) {
                    doOpForMove(_mapSquares[ctr], ctr);
                }

            }

        }

        // now, loop through all the units again to figure out which enmy units should get hidden off the map again
        // they stay visible if adjacent to friendly unit or within 2 of hq unit, otherwise 50% chance they are able to slip out of sight (but still in same square)
        Log.d(TAG, "About to look through units to see which ones should remain visible or be hidden");
        for (int ctr = 0; ctr < MAX_ARRAY; ctr++) {

            redUnit = _mapSquares[ctr].getUnit();
            if ((redUnit != null) && (redUnit.getOwner() == Unit.OWNER_OPFOR) && (!redUnit.getHasAttacked())) {
                // see if adjacent to any friendly forces
                fromSq = _mapSquares[ctr];
                row = fromSq.getRow();
                col = fromSq.getCol();
                stayVisible = false;

                Log.d(TAG, "Checking for redUnit " + redUnit.getName() + " at " + Integer.toString(row) + ", " + Integer.toString(col));

                // first, randomly figure out if they should stay visible (2 in 10 chance)
                result = getRandomNumber(10, 1);
                //Log.d(TAG, "redUnit random number to stay visible: " + Integer.toString(result));
                if (result <= 2) {
                    stayVisible = true;
                }
                else {
                    stayVisible = false;
                }

                for (int x = 0; x < MAX_ARRAY; x++) {
                    toSq = _mapSquares[x];
                    blueUnit = toSq.getUnit();
                    if ((blueUnit != null) && (blueUnit.getOwner() == Unit.OWNER_PLAYER)) {
                        // 1 for all units, except hq which is 2
                        distanceAllowed = 1.0;
                        if (blueUnit.getType() == Unit.TYPE_HQ) {
                            distanceAllowed = 2.0;
                            //Log.d(TAG, "blueUnit is HQ, so distance can be up to 2");
                        }

                        // see how far from enemy unit
                        distanceActual = getDistanceBetweenSquares(row, col, toSq.getRow(), toSq.getCol());
                        //Log.d(TAG, "distanceActual is " + Double.toString(distanceActual) + ", distanceAllowed is " + Double.toString(distanceAllowed));
                        if ((distanceActual <= distanceAllowed)) {
                            // yes, enemy should remain visible, so do nothing
                            Log.d(TAG, "redUnit " + redUnit.getName() + " at " + Integer.toString(row) + ", " + Integer.toString(col) + " should remain visible");
                            stayVisible = true;
                            redUnit.setIsVisible(true);
                            break;
                        }
                        else {
                            stayVisible = false;
                            redUnit.setIsVisible(false);
                        }


                    }

                }

                // we're out of loop, so if we should not be visible, swap them out for terrain
                if (!stayVisible) {
                    Log.d(TAG, "redUnit " + redUnit.getName() + " at " + Integer.toString(row) + ", " + Integer.toString(col) + " is not visible, so swapping back in terrain icon");
                    v = (ImageView) _mapAdapter.getItem(ctr);
                    if (v != null) {
                        img = fromSq.getTerrainType();
                        v.setImageResource(img);
                        _mapAdapter.setItem(v, img, ctr);
                    }
                    else {
                        Log.d(TAG, "ImageView was null ... redUnit " + redUnit.getName() + " at " + Integer.toString(row) + ", " + Integer.toString(col));
                    }
                }


                _mapAdapter.notifyDataSetChanged();

                // update the array
                _mapSquares[ctr].setUnit(redUnit);

            }

        }

        // and reset on way out
        _enemyTurn = false;


    }

    // ===========================
    // doEndTurn
    // ===========================
    void doEndTurn() {

        Log.d(TAG, "Enter doEndTurn");

        Unit u = null;
        MapSquare ms = null;
        MapSquare msHQ = null;
        int pos = 0;
        final Button endTurnButton = (Button) findViewById(R.id.button1);
        int img = 0;
        boolean gameOver = false;

        // time to quickly check if game should continue or they hit the two auto-end game business rules
        // #1 are we past max turns?
        if (_turn == MAX_TURNS) {
            // yes, we're the max turn before incrementing so next turn would push us over ... game over man, game over
            gameOver = true;
        }
        else {
            // #2 loop through all player units and if all are at eff black, then done
            gameOver = true;
            for (int ctr = 0; ctr < MAX_ARRAY; ctr++) {
                u = _mapSquares[ctr].getUnit();
                if ((u != null) && (u.getOwner() == Unit.OWNER_PLAYER) && (u.getEff() != Unit.EFF_BLACK)) {
                    // we found a unit owned by the player not at eff black, so ok to continue
                    gameOver = false;
                    break;
                }
            }
        }

        if (gameOver == true) {
            showEndGameDialog(GAME_OVER_LOSE);
        }


        // clear out action text
        _actionText.setText("");

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
                        // if no longer suppressed, need to flip icon back
                        pos = getArrayPosforRowCol(ms.getRow(), ms.getCol());
                        ImageView v = (ImageView) _mapAdapter.getItem(pos);
                        img = getUnitIcon(u);
                        v.setImageResource(img);
                        _mapAdapter.setItem(v, img, pos);
                    }
                    else {
                        // code here to see if hq unit adjacent to suppressed unit; if it is, remove the suppression right away (if a player unit)
                        msHQ = getHQUnitMapSquare();
                        if ((isAdjacent(msHQ, ms) && (u.getOwner() == Unit.OWNER_PLAYER)) ) {
                            u.setIsSuppressed(false);
                            // if no longer suppressed, need to flip icon back
                            pos = getArrayPosforRowCol(ms.getRow(), ms.getCol());
                            ImageView v = (ImageView) _mapAdapter.getItem(pos);
                            img = getUnitIcon(u);
                            v.setImageResource(img);
                            _mapAdapter.setItem(v, img, pos);
                        }

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

        // actually do computer part of the turn
        doOpForTurn();

        Utils.delay(2, new Utils.DelayCallback() {
            @Override
            public void afterDelay() {
                // turn button back on
                endTurnButton.setBackgroundResource(R.drawable.button_border_enabled);
                endTurnButton.setTextColor(Color.parseColor("#ffff00"));
                endTurnButton.setEnabled(true);
            }
        });

        // increment turn counter
        _turn++;
        _turnString  = String.format(getString(R.string.turn), Integer.toString(_turn), getString(R.string.you));
        _turnText.setText(_turnString);


        // save all the game data



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
        int img = 0;

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
            img = ms.getTerrainType();
            v.setImageResource(img);
            _mapAdapter.setItem(v, img, posFrom);

            // now drop that unit into new position
            ms = _mapSquares[posTo];
            u.setRemainingMove((u.getRemainingMove() - getMoveCost(ms)));
            ms.setUnit(u);
            _mapSquares[posTo] = ms;

            // re-draw icon in new spot
            v = (ImageView) _mapAdapter.getItem(posTo);
            v.setPadding(5,5,5,5);
            v.setBackgroundColor(Color.BLUE);
            img = getUnitIcon(u);
            v.setImageResource(img);
            _mapAdapter.setItem(v, img, posTo);

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
    // getArrayPosforRowCol
    // ===========================
    int getArrayPosforRowCol(int row, int col) {
        int pos = 0;
        for (pos = 0; pos < MAX_ARRAY; pos++) {
            if ((_mapSquares[pos].getRow() == row) && (_mapSquares[pos].getCol() == col)) {
                break;
            }
        }
        // do not return 150 because that means array out of bounds
        if (pos == MAX_ARRAY) {
            pos = MAX_ARRAY - 1;
        }
        return pos;
    }

    // ==========================
    // getDistanceBetweenSquares
    // ==========================
    double getDistanceBetweenSquares(int x1, int y1, int x2, int y2) {

        //Log.d(TAG, "Enter getDistanceBetweenSquares");

        double dx = Math.abs(x2 - x1);
        double dy = Math.abs(y2 - y1);
        double min = Math.min(dx, dy);
        double max = Math.max(dx, dy);
        double straightSteps = max - min;

        //Log.d(TAG, "Exit getDistanceBetweenSquares");

        return Math.floor(Math.sqrt(2) * min + straightSteps);

    }


    // ===========================
    // getHQUnitMapSquare
    // ===========================
    MapSquare getHQUnitMapSquare() {

        int pos = 0;
        MapSquare ms = null;
        Unit u = null;

        for (pos = 0; pos < MAX_ARRAY; pos++) {
            ms = _mapSquares[pos];
            u = ms.getUnit();
            if ((u != null) && (u.getOwner() == Unit.OWNER_PLAYER) && (u.getType() == Unit.TYPE_HQ)) {
                break;
            }
        }
        return ms;
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
    // getRandomNumber
    // ===========================
    int getRandomNumber(int max, int min) {

        Random randomGenerator = new Random();
        return randomGenerator.nextInt((max - min) + 1) + min;

    }

    // ===========================
    // getTerrainImage
    // ===========================
    int getTerrainImage(MapSquare ms) {

        return ms.getTerrainType();

    }


    // ===========================
    // getUnitIcon
    // ===========================
    int getUnitIcon(Unit u) {

        int img = 0;

        // first thing, if human player and unit is suppressed, grab that icon and just jump out
        if ((u.getIsSuppressed()) && (u.getOwner() == Unit.OWNER_PLAYER)) {
            switch (u.getType()) {
                case Unit.TYPE_INF:
                    img = R.drawable.inf_platoon_suppressed;
                    break;
                case Unit.TYPE_HQ:
                    img = R.drawable.hq_section_suppressed;
                    break;
                case Unit.TYPE_MG:
                    img = R.drawable.mg_team_suppressed;
                    break;
                case Unit.TYPE_MORTAR:
                    img = R.drawable.mortar_section_suppressed;
                    break;
            }
            return img;

        }

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
    // IsMapSpotTaken
    // ===========================
    boolean isMapSpotTaken(int row, int col) {

        Log.d(TAG, "Enter isMapSpotTaken");

        // look at map square array to see if a unit already there
        MapSquare ms = _mapSquares[getArrayPosforRowCol(row, col)];

        Log.d(TAG, "Exit isMapSpotTaken");
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

    // ===========================
    // newGame
    // ===========================
    void newGame() {
        // create array of map objects
        _mapSquares = new MapSquare[MAX_ARRAY];
        MapSquare ms;

        Log.d(TAG, "newGame");

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
        _turnString  = String.format(getString(R.string.turn), Integer.toString(_turn), getString(R.string.you));
        _actionString = "";

    }

    // ===========================
    // onBackPressed
    // ===========================
    @Override
    public void onBackPressed() {

        Log.d(TAG, "onBackPressed");
        persistGame();
        finish();

    }


    // ===========================
    // onCreate
    // ===========================
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        // setu p the media players needed
        _mpInfPlatoon = MediaPlayer.create(this, R.raw.inf_platoon);
        _mpInfSquad = MediaPlayer.create(this, R.raw.inf_squad);
        _mpMG = MediaPlayer.create(this, R.raw.mg);
        _mpMortar = MediaPlayer.create(this, R.raw.mortar);
        _mpSniper = MediaPlayer.create(this, R.raw.sniper);
        String newGame = "true";

        // get the custom font
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Army.ttf");
        Typeface tft = Typeface.createFromAsset(getAssets(), "fonts/ArmyThin.ttf");

        setContentView(R.layout.activity_game);
        Button buttonEndTurn = (Button) findViewById(R.id.button1);
        buttonEndTurn.setTypeface(tf);
        // button click
        buttonEndTurn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Button btn = (Button) findViewById(R.id.button1);
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setTextColor(Color.parseColor("#ffffff"));
                    btn.setBackgroundResource(R.drawable.button_border_selected);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setTextColor(Color.parseColor("#ffff00"));
                    btn.setBackgroundResource(R.drawable.button_border_enabled);
                    // do the computer part of this turn
                    _turnString = String.format(getString(R.string.turn), Integer.toString(_turn), getString(R.string.computer));
                    _turnText.setText(_turnString);
                    _turnText.invalidate();
                    // after a 1 second delay, end the turn
                    Utils.delay(1, new Utils.DelayCallback() {
                        @Override
                        public void afterDelay() {
                            doEndTurn();
                        }
                    });
                }
                return true;
            }
        });

        // get the two textviews for status
        _turnText = (TextView) findViewById(R.id.textView2);
        _turnText.setTypeface(tft);
        _actionText = (TextView) findViewById(R.id.textView8);
        _actionText.setTypeface(tft);

        // set the mapadapter to build out the initial map
        _gridView = (GridView) findViewById(R.id.gridView1);

        // set the long press
        _gridView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v,
                                           int position, long id) {
                Log.d(TAG, "LongClick at position: " + Integer.toString(position));
                showDetails(position);
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

        // new game?
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newGame = "true";
            } else {
                newGame = extras.getString("NEW_GAME");
            }
        } else {
            newGame = (String) savedInstanceState.getSerializable("NEW_GAME");
        }

        if (newGame.equalsIgnoreCase("true")) {
            newGame();
        } else {
            resumeGame();
        }

        // pass in the array of image ids
        _mapAdapter.setImageArray(_imageIds);
        _gridView.setAdapter(_mapAdapter);

        // set the textviews
        _turnText.setText(_turnString);
        _actionText.setText(_actionString);


    }


    // ===========================
    // onPause
    // ===========================
    @Override
    public void onPause() {


        super.onPause();
        Log.d(TAG, "onPause");
        if (_persistGame) {
            persistGame();
        }
    }

    // ===========================
    // onResume
    // ===========================
    @Override
    public void onResume() {


        super.onResume();
        Log.d(TAG, "onResume");

    }

    // ===========================
    // persistGame
    // ===========================
    public void persistGame() {

        StringBuilder csv = new StringBuilder();
        MapSquare ms = null;
        Unit u = null;

        Log.d(TAG, "persistGame");

        // loop through the mapsqaures array and create csv file in format of:
        // <first row> current turn
        csv.append(Integer.toString(_turn) + "\r\n");

        //
        for (int ctr = 0; ctr < MAX_ARRAY; ctr++) {
            ms = _mapSquares[ctr];
            // row
            // col
            // terrain type
            csv.append(ms.getRow() + "," + ms.getCol() + "," + ms.getTerrainType());
            // unit owner
            u = ms.getUnit();
            if (u == null) {
                // put a -1 where there' be a unit so we don't try to instantiate one later
                csv.append(",-1");
            }
            else {
                // unit name
                // unit type
                // unit size
                // unit owner
                // unit isvisible
                // unit issuppressed
                // unit turnsuppressed
                // unit has attacked
                // unit attackrange
                // unit attacknumber
                // unit agg
                // unit eff
                // unit maxmove
                // unit remaining move
                csv.append("," + u.getName() + "," + u.getType() + "," + u.getSize() + "," + u.getOwner() + "," + u.getIsVisible() + "," + u.getIsSuppressed() + "," + u.getTurnSuppressed() +
                    "," + u.getHasAttacked() + "," + u.getAttackRange() + "," + u.getAttackNumber() + "," + u.getAggression() + "," + u.getEff() +
                    "," + u.getMaxMove() + "," + u.getRemainingMove());

            }
            csv.append("\r\n");

        }

        try {
            FileOutputStream fos = openFileOutput(SAVEGAMEFILENAME, Context.MODE_PRIVATE);
            fos.write(csv.toString().getBytes("UTF-8"));
            fos.close();
        }
        catch (Exception e) {
            // raise an error
            Log.e(TAG, e.getMessage());
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
        u.setIsVisible(false);

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

    // ===========================
    // playSound
    // ===========================
    void playSound(Unit u) {

        Log.d(TAG, "Enter getSound");

        // only play sound if set in preferences dialog
        if (Prefs.getValue(this, Prefs.KEY_SOUND) == 1) {

            switch (u.getType()) {
                case Unit.TYPE_HQ:
                    _mpInfSquad.start();
                    break;
                case Unit.TYPE_MG:
                    _mpMG.start();
                    break;
                case Unit.TYPE_MORTAR:
                    _mpMortar.start();
                    break;
                case Unit.TYPE_SNIPER:
                    _mpSniper.start();
                    break;
                default:
                    if (u.getSize() == Unit.SIZE_PLATOON) {
                        _mpInfPlatoon.start();
                    } else {
                        _mpInfSquad.start();
                    }
            }

        }
        else {
            Log.d(TAG, "No sound played due to preference setting");
        }

        Log.d(TAG, "Exit getSound");

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
    // resumeGame
    // ===========================
    void resumeGame() {

        Log.d(TAG, "resumeGame");


        String line = null;
        String[] vals = null;
        // create array of map objects
        _mapSquares = new MapSquare[MAX_ARRAY];
        MapSquare ms;
        int row = 1;
        int col = 1;
        int terrainType = 0;
        int ctr = 0;
        Unit unit = null;
        String unitName = "";

        // open up file from directory, read in the bytes and build out the map

        try {
            FileInputStream fis = openFileInput(SAVEGAMEFILENAME);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            // first line is game turn
            line = br.readLine();
            _turn = Integer.parseInt(line);
            // every line after that is map square
            while ((line = br.readLine()) != null) {
                vals = line.split(",");
                ms = new MapSquare();
                ms.setRow(Integer.parseInt(vals[0]));
                ms.setCol(Integer.parseInt(vals[1]));
                ms.setTerrainType(Integer.parseInt(vals[2]));
                _imageIds[ctr] = Integer.parseInt(vals[2]);
                // 3 - unit name (x)
                // 4 - unit type (x)
                // 5 - unit size (x)
                // 6 - unit owner (x)
                // 7 - unit isvisible (x)
                // 8 - unit issuppressed (x)
                // 9 - unit turnsuppressed (x)
                // 10 - unit has attacked (x)
                // 11 - unit attackrange (x)
                // 12 - unit attacknumber (x)
                // 13 - unit agg (x)
                // 14 - unit eff (x)
                // 15 - unit maxmove (x)
                // 16 - unit remaining move (x)
                unitName = vals[3];
                if (unitName.equalsIgnoreCase("-1")) {
                    // no unit in this square, so just add null one to the array
                    ms.setUnit(null);
                }
                else {
                    // yes, unit there so create it
                    unit = new Unit(vals[3], Integer.parseInt(vals[4]), Integer.parseInt(vals[5]), Integer.parseInt(vals[6]),
                            Integer.parseInt(vals[11]), Integer.parseInt(vals[12]), Integer.parseInt(vals[15]));
                    unit.setIsVisible(Boolean.parseBoolean(vals[7]));
                    unit.setIsSuppressed(Boolean.parseBoolean(vals[8]));
                    unit.setTurnSuppressed(Integer.parseInt(vals[9]));
                    unit.setHasAttacked(Boolean.parseBoolean(vals[10]));
                    unit.setAggression(Integer.parseInt(vals[13]));
                    unit.setEff(Integer.parseInt(vals[14]));
                    unit.setRemainingMove(Integer.parseInt(vals[16]));
                    ms.setUnit(unit);
                    // and we need to add that to the map (if visible)
                    if (unit.getIsVisible()) {
                        _imageIds[ctr] = getUnitIcon(unit);
                    }
                    // and count up how many enemy units
                    if (unit.getOwner() == Unit.OWNER_OPFOR) {
                        _enemyUnitCount++;
                    }

                }

                // add mapsquare to the array
                _mapSquares[ctr] = ms;

                ctr++;
            }




        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        // set the initial turn text
        _turnString  = String.format(getString(R.string.turn), Integer.toString(_turn), getString(R.string.you));
        _actionString = "";


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
                unit.setIsVisible(true);
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
        // then how many mg  sections
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

                // for each enemy unit, pick an aggression factor (which we'll use later when it comes to movement)
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
                        break;

                    case Unit.TYPE_MG:
                        // loop through inf units, find a spot for each, create a new unit and add to arrays
                        for (int ctr = 0; ctr < mgTeams; ctr++) {
                            unit = new Unit(name, type, size, owner, range, attack, move);
                            unit.setAggression(aggression);
                            placeEnemyUnit(unit);
                        }
                        break;

                    case Unit.TYPE_SNIPER:
                        // loop through inf units, find a spot for each, create a new unit and add to arrays
                        for (int ctr = 0; ctr < sniperTeams; ctr++) {
                            unit = new Unit(name, type, size, owner, range, attack, move);
                            unit.setAggression(aggression);
                            placeEnemyUnit(unit);
                        }
                        break;
                }

            }
            reader.close();
        }
        catch (IOException ioe) {
            // do nothing
        }

        // store number of enemy units
        _enemyUnitCount = rifleSquads + mgTeams + sniperTeams;
        Log.d(TAG, "There are " + _enemyUnitCount + " enemy units in this game.");

        Log.d(TAG, "Exit setUpEnemyUnits");

    }

    // ==========================
    // select unit in map square by changing border
    // ==========================
    void selectUnit(int pos, int color) {
        ImageView v = (ImageView) _mapAdapter.getItem(pos);
        v.setPadding(5,5,5,5);
        v.setBackgroundColor(color);
    }

    // ===========================
    // showDetails
    // ===========================
    void showDetails(int pos) {

        ImageView v = (ImageView) _mapAdapter.getItem(pos);
        MapSquare ms = _mapSquares[pos];
        Unit u = ms.getUnit();
        String title = null;
        String message = null;
        int icon = 0;

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);

        // let's see what we clicked on
        // no unit in there, so show terrain details
        if ((u != null) && (u.getOwner() == Unit.OWNER_PLAYER)) {
            // there is a unit there, so first make sure it is a player's unit
            if (u.getOwner() == Unit.OWNER_PLAYER) {
                icon = getUnitIcon(u);
                title = u.getName();
                switch (u.getType()) {
                    case Unit.TYPE_HQ:
                        message = getString(R.string.player_hq);
                        break;
                    case Unit.TYPE_INF:
                        message = getString(R.string.player_infantry);
                        break;
                    case Unit.TYPE_MG:
                        message = getString(R.string.player_mg);
                        break;
                    case Unit.TYPE_MORTAR:
                        message = getString(R.string.player_mortar);
                        break;
                }
            }
        }
        else if ((u != null) && (u.getOwner() == Unit.OWNER_OPFOR) && (u.getIsVisible())) {
            icon = getUnitIcon(u);
            title = u.getName();
            switch (u.getType()) {
                case Unit.TYPE_INF:
                    message = getString(R.string.opfor_infantry);
                    break;
                case Unit.TYPE_MG:
                    message = getString(R.string.opfor_mg);
                    break;
                case Unit.TYPE_SNIPER:
                    message = getString(R.string.opfor_sniper);
                    break;

            }
        }
        else {
            icon = getTerrainImage(ms);
            switch (icon) {
                case R.drawable.rocky:
                    title = getString(R.string.rocky);
                    message = getString(R.string.rocky_desc);
                    break;
                case R.drawable.woods:
                    title = getString(R.string.woods);
                    message = getString(R.string.woods_desc);
                    break;
                case R.drawable.scrub:
                    title = getString(R.string.scrub);
                    message = getString(R.string.scrub_desc);


            }

        }

        builder.setMessage(message);
        builder.setTitle(title);
        builder.setIcon(icon);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing, just close
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();



    }

    // ===========================
    // showEndGameDialog
    // ===========================
    public void showEndGameDialog(int message) {

        int title = 0;
        String body = null;

        // decide which title to use
        if (message == GAME_OVER_LOSE) {
            title = R.string.game_over_lose_title;
            body = getString(R.string.game_over_lose_body);
        }
        else {
            title = R.string.game_over_win_title;
            // they won the game, so also figure out the score
            body = String.format(getString(R.string.game_over_win_body), calculateStars());
        }

        // delete any saved game data
        try {
            deleteFile(SAVEGAMEFILENAME);
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        // set flag so when we hit onPause, don't try to save data
        _persistGame = false;

        // instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setMessage(body);
        builder.setTitle(getString(title));
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // back to main menu
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }


}
