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

    }

    // ===========================
    // getTerrainImage
    // ===========================
    int getTerrainImage(MapSquare ms) {

        return ms.getTerrainType();

    }

    // ===========================
    // getMoveCost
    // ===========================
    int getMoveCost(MapSquare ms) {

        Log.d(TAG, "Entered getMoveCost");

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

        Log.d(TAG, "Exiting getMoveCost. moveCost = " + Integer.toString(moveCost));
        return moveCost;

    }


    // ===========================
    // canMove
    // ===========================
    boolean isAbleToMoveInto(MapSquare ms, Unit u) {

        Log.d(TAG, "Entered isAbleToMoveInfo");

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

        Log.d(TAG, "Exiting isAbleToMoveInto. u.getRemainingMove() = " + Integer.toString(u.getRemainingMove())+ ", moveRequired = " + Integer.toString(moveRequired));
        return (u.getRemainingMove() >= moveRequired);

    }

    // ===========================
    // doMove
    // ===========================
    boolean doMove(MapSquare fromSq, MapSquare toSq) {

        Log.d(TAG, "Entered doMove");

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

        /**
        var terrainMod = 0;
        var attackNum = 0;
        var damage = 0;
        var roll = 0;
        var enemyUnit = null;
        var arrayPos = 0;
        var effMod = 0;
        var strokeColor = "";
        var img = null;
        var t = null;
        var attackMsg = "";

        // store enemy unit
        enemyUnit = targetSq.unit;

        // if enemy wasn't already visible, draw the unit make it visible
        if (enemyUnit.visible === 0) {
            enemyUnit.visible = 1;
            strokeColor = getUnitStrokeColor(sq.unit);
            img = getUnitImage(sq.unit);
            t = getUnitXY(sq);
            drawUnit(img, strokeColor, t[0], t[1]);

        }

        // set the has_attacked property to 1
        friendlyUnit.has_attacked = 1;
        _mapArray[pos].sq = friendlyUnit;

        // quick check, if combat effectivenss is "black" (0), just bail
        if (friendlyUnit.eff === 0) {
            return;
        }

        // need to determine attack number; this is unit attack base - effectiveness modifier - terrain modifier
        if ((targetSq.terrain == "Woods") || (targetSq.terrain == "Rocky")) {
            terrainMod = -1;
        }

        if (friendlyUnit.eff == 1) {
            effMod = -2;
        } else if (friendlyUnit.eff == 2) {
            effMod = -1;
        }

        attackNum = friendlyUnit.attack - effMod - terrainMod;

        // pick a random number from 1 - 10 and see if attack num is below that
        roll = getRandomNum(1, 10);

        alertMessage("attackNum: " + attackNum + ", roll: " + roll);

        if (roll <= attackNum) {
            // hit!
            damage = getRandomNum(1, 10);
            alertMessage("damage: " + damage);
            if (damage <= 8) {
                enemyUnit.eff--;
                alertMessage("unit " + enemyUnit.name + " lost effectiveness, now at " + enemyUnit.eff);
                attackMsg = "The unit was hit and took damage.";
            }

            // certain types of unit cause supression
            if ((friendlyUnit.type == "mg") || (friendlyUnit.type == "sniper") || (friendlyUnit.type == "mortar")) {
                enemyUnit.is_suppressed = 1;
                alertMessage("unit supressed!");
                attackMsg += " It was also suppressed (no movement or attack possible).";
            }

        }

        // update text (since we attacked)
        if (friendlyUnit.player == "human") {
            setUnitText(friendlyUnit);
        }

        // if enemy unit has gone to black, just get rid of it.
        arrayPos = getArrayPosforRowCol(_mapArray, targetSq.row, targetSq.col);
        if ((enemyUnit.eff === 0) && (enemyUnit.player == "ai")) {
            alertMessage("unit " + targetSq.unit.name + " at row " + targetSq.row + ", col " + targetSq.col + " eliminated!");
            attackMsg = "Unit is no longer combat effective!";
            _mapArray[arrayPos].unit = null;
            // redraw that map square since unit gone
            drawMapSquare(targetSq);
        } else {
            // update the unit that was attacked
            _mapArray[arrayPos].unit = enemyUnit;
        }

        // pop-up if unit lost effectiveness
        if (attackMsg !== "") {
            alert(attackMsg);
        }

         **/

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

        Log.d(TAG, "Entered isAdjacent");

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

        Log.d(TAG, "Leaving isAdjacent. isAjd = " + Boolean.toString(isAdj));
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

        // next, manually set friendly units
        setUpFriendlyUnits();

        // set the initial turn text
        _turnString  = "Movement To Contact - Turn 1 - Your turn";
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

        Log.d(TAG, "Entered doClick");

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
            selectUnit(position);
            _actionText.setText(u.getName() + ": " + u.getRemainingMove() + " move points remaining");
            _activeUnit = u;
            _activeSq = ms;
            return;
        }

        // #3 if we have active square and unit, did they select on adjacent map square?
        if (isAdjacent(_activeSq, ms)) {
            // yes, adjacent
            // (a) if friendly there, switch to that unit
            if ((ms.getUnit() != null) && (ms.getUnit().getOwner() == Unit.OWNER_PLAYER)) {
                deselectUnit(getArrayPosforRowCol(_activeSq.getRow(), _activeSq.getCol()));
                selectUnit(position);
                _actionText.setText(u.getName() + ": " + u.getRemainingMove() + " move points remaining");
                _activeUnit = u;
                _activeSq = ms;
                return;
            }
            // (b) if empty, move in (assuming move left)
            if (ms.getUnit() == null) {
                if (doMove(_activeSq, ms)) {
                    u = ms.getUnit();
                    _actionText.setText(u.getName() + ": " + u.getRemainingMove() + " move points remaining");
                }
                else {
                    _actionText.setText("Unable to move due to lack of remaining move points");
                }
                return;
            }
            // (c) if enemy there, attack
            if ((ms.getUnit() != null) && (ms.getUnit().getOwner() == Unit.OWNER_OPFOR)) {
                doAttack(_activeSq, ms);
                return;
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
                _actionText.setText(u.getName() + ": " + u.getRemainingMove() + " move points remaining");
                _activeUnit = u;
                _activeSq = ms;
                return;
            }
            // (b) if enemy there and in range, attack

            // (c) otherwise, ignore
            return;

        }


        /**
            else {
                // we have an active square, did they click on an adjacent one?
                if ((_activeSq !== null) && (isAdjacent(_activeSq, sq))) {
                    alertMessage("active square and selected square is adjacent");
                    alertMessage("adjacent square isEmpty? " + isEmpty(sq));
                    // if empty and unit has move left, move there
                    if ((isEmpty(sq)) && (_activeSq.unit !== null) && (_activeSq.unit.move_cur > 0)) {
                        moveUnit(_activeSq, sq);
                    }
                    // if not empty, and there is friendly there, just bail
                    else if ((!isEmpty(sq)) && (sq.unit.player == "human")) {
                        alertMessage("friendly in our way ... switch over to that unit");
                        selectUnit(sq, pos);
                        setUnitText(sq.unit);
                        deselectUnit(_activeSq);
                    }
                    // if not emoty, there is enemy and haven't attacked yet, attack
                    else if ((!isEmpty(sq)) && (sq.unit.player == "ai") && (_activeSq.unit.has_attacked === 0)) {
                        doAttack(_activeSq.unit, sq, pos);
                        // don't swap out acive square
                        return;
                    }
                    // if not empty, there is enemy but we have attacked, just bail
                    else if ((!isEmpty(sq)) && (sq.unit.player == "ai") && (_activeSq.unit.has_attacked === 1)) {
                        alertMessage("enemy there but already attacked ... bail");
                        return;
                    }

                } else if ((_activeSq !== null) && (!isAdjacent(_activeSq, sq))) {
                    alertMessage("active square and selected square is not adjacent");
                    // if human player in spot, make that unit active
                    if ((sq.unit !== null) && (sq.unit.player == "human")) {
                        // deselect any other unit
                        deselectUnit(_activeSq);
                        selectUnit(sq);
                    } else {
                        // is there an enemy player there, within range and we haven't attacked yet?
                        if ((sq.unit !== null) && (sq.unit.player == "ai") && (_activeSq.unit !== null) && (_activeSq.unit.has_attacked === 0)) {
                            alertMessage("enemy player in square and we haven't attacked yet");
                            // what is the range?
                            if (getDistanceBetweenSquares(_activeSq.row, _activeSq.col, sq.row, sq.col) <= _activeSq.unit.range) {
                                alertMessage("enemy within range, about to attack!");
                                // actually can do an attack
                                doAttack(_activeSq.unit, sq, pos);
                            }
                            // jump out because we want to keep active square the unit that attacked
                            return;

                        } else {
                            // nope, just don't set an active square and bail
                            _activeSq = null;
                            return;

                        }
                    }

                }
                // clicked on a unit, but it is an enemy that is visible
                else if ((unit !== null) && (unit.player == "ai") && (unit.visible == 1)) {
                    alertMessage("clicked on visible enemy unit");
                    // it is an enemy unit, so can show some things (but not all)
                    setUnitText(unit);

                }

            }



        }
        else {

            // if there is a unit at that square, see who owns it
            if (u != null) {
                owner = u.getOwner();
                if (owner == Unit.OWNER_PLAYER) {
                    // selected one of our units, so set active unit, highlight it on the map and set action text


                }
                else {
                    // no active unit, and clicked on either terrain or enemy unit, so nothing to do
                    // except un-highlight anything that previously was
                    if (_imgPrevClicked != null) {
                        _imgPrevClicked.setPadding(2,2,2,2);
                        _imgPrevClicked.setBackgroundColor(Color.BLACK);
                    }
                }
            }

        }

         **/



    }


    // ===========================
    // onCreate
    // ===========================
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

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


    }
}
