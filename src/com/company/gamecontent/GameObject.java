package com.company.gamecontent;

import com.company.gamecontent.Parallelepiped.GridRectangle;
import com.company.gamecontent.ParallelogramHorizontal.GridMatrixHorizontal;
import com.company.gamecontent.ParallelogramVertical.GridMatrixVectical;

import com.company.gamegraphics.GraphBugfixes;
import com.company.gamegraphics.GraphExtensions;
import com.company.gamegraphics.Sprite;
import com.company.gamethread.Main;
import com.company.gametools.MathTools;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;

import static com.company.gamecontent.Restrictions.*;
import static com.company.gametools.MathTools.in_range;
import static com.company.gametools.MathTools.randomSign;
import static com.company.gametools.MathTools.sqrVal;

import static com.company.gamethread.Main.terminateNoGiveUp;

// For details read the DOC "Data Structure"
public class GameObject implements Moveable, Rotatable, Centerable, Renderable, Selectable {
    private static Logger LOG = LogManager.getLogger(GameObject.class.getName());

    private Parallelepiped parallelepiped;

    // TODO Use Point2D class
    protected Integer[] destPoint;   // The map point to move to (has x, y)

    // Current orientation angle of the objects' sprite, measured in radians.
    // Since we do all calculation in the Cartesian coordinate system
    // the value 0 (that is 0°) means "to east".
    private double currAngle;

    protected Sprite sprite;

    // TODO Check body block usability
    // We should decide if we implement object of complex form.
    // That is, the object which contains of several block items
    // which are not a solid rectangular parallelepiped. */
//    bodyBlocks[][] bodyBlocks;

    protected int playerId;

    protected int hitPoints;
    protected int maxHitPoints;
    protected int speed;
    protected double rotation_speed;
    protected int preMoveAngle;

//    protected int armor;             // 0..100% - percentage damage decrement

    // absolute damage decrement - minimal HP amount that makes some
    // damage to the object
    // (lower damages are just congested and make no damage) */
//    protected int hardness;

    protected HashMap<Resource,Integer> res;   // res[0] is mass, res[1] is energy, res[2} is money etc.

//    protected int burnChanceOnHit;           // 0..100% - gives the chance of ignition at the bullet hitting.
//    protected int explosionChanceOnHit;      // 0..100% - similar with the previous one

    // 0..100% - this is another one. On each calculation step
    // the object can explode if it is burning.
//    protected int explosionChanceOnBurn;

//    protected boolean isBroken;
//    protected boolean isBurning;
//    protected boolean isDying;
//    protected boolean isMoving;

    protected boolean isSelected;


    // Here x,y,z - coordinates on grid (not absolute)
    public GameObject(Sprite sprite, int x, int y, int z, int sX, int sY, int sZ, HashMap<Resource,Integer> res, int hp, int speed, int rot_speed, int preMoveAngle, int arm, int hard, int bch, int ech, int eco) {
        // 1 - parent class specific parameters
        // 2 - validation
        if (sprite == null) {
            throw new IllegalArgumentException("Failed to initialize GameObject with spr=null.");
        }

        // TODO: check if the object borders are within map area!
        boolean valid;

        int maxX = GameMap.getInstance().getMaxX();
        int maxY = GameMap.getInstance().getMaxY();
        int maxZ = GameMap.getInstance().getMaxZ();

        // Check object coordinates
        valid = in_range(0, x, MAX_X, false);
        valid = valid && in_range(0, y, MAX_Y, false);
        valid = valid && in_range(0, z, MAX_Z, false);

        // Check object stats
        valid = valid && in_range(0, hp, MAX_HP, false);
        valid = valid && in_range(-MAX_SPEED, speed, MAX_SPEED, false);
        valid = valid && (preMoveAngle <= MAX_PRE_MOVE_ANGLE);

        // Check object dimensions
        valid = valid && in_range(0, sX, getMaxObjectSizeBlocks() + 1, true);
        valid = valid && in_range(0, sY, getMaxObjectSizeBlocks() + 1, true);
        valid = valid && in_range(0, sZ, getMaxObjectSizeBlocks() + 1, true);

        // Check that we don't create the object overlapped with another object
        // In order to use the function occupiedByAnotherObject for still not completely created class instance
        // we have to define the objec dimensions first:
        this.parallelepiped = new Parallelepiped(x * BLOCK_SIZE, y * BLOCK_SIZE, z * BLOCK_SIZE, sX, sY, sZ);
        if (GameMap.getInstance().occupied(getRect(), this)) {
            valid = false;
        }
        // Check object resources limits
        valid = valid && in_range(
                0, res.get(Resource.MASS), MAX_MASS + 1, true
        );

        valid = valid && in_range(
                0, res.get(Resource.ENERGY), MAX_ENERGY, false
        );

        if (!valid) {
            terminateNoGiveUp(
                    1000,
                    "Failed to initialize " + getClass() + ". Some of parameters are beyond the restricted boundaries."
            );
        }

        this.sprite = sprite;

        this.res = new HashMap<Resource,Integer>();
        this.res.put(Resource.MASS, res.get(Resource.MASS));
        this.res.put(Resource.ENERGY, res.get(Resource.ENERGY));

        this.maxHitPoints = hp;
        this.speed = speed;
        this.rotation_speed = Math.toRadians(rot_speed);

        // Only for Rotatable game objects (the object that have "face").
        // The object must turn towards the destination/target direction
        // until the angle between the ray where the objects face looks
        // and the ray towards the destination/target direction becomes < preMoveAngle
        // before it starts moving towards the destination/target.
        // 0 or negative value means that we don't take care and move immediately.
        // It is used in the method moveTo().

        this.preMoveAngle = 0; // default
        // Allow this only for Rotatable (that is oriented) game objects
        if (this instanceof Rotatable) {
            this.preMoveAngle = preMoveAngle;
        }

//        this.armor = arm;
//        this.hardness = hard;
//        this.burnChanceOnHit = bch;
//        this.explosionChanceOnHit = ech;
//        this.explosionChanceOnBurn = eco;

        // 3 - default values
        this.hitPoints = hp;
//        isBroken = false;
//        isBurning = false;
//        isDying = false;
//        isMoving = false;
        this.isSelected = false;
        this.destPoint = null;
        //this.destAngle = 0;

        // Default - at the beginning of the game all objects are oriented to north
        this.currAngle = INIT_ANGLE;

        // FIXME this.playerId = Faction.NEUTRAL
        this.playerId = -1;

        // Mark the object on the map
        GameMap.getInstance().registerObject(this);
    }

    // wrapper method
    public void render(Graphics g) {
        render(g, parallelepiped, currAngle);
    }

    // Method of the "Renderable" interface
    public void render(Graphics g, Parallelepiped parallelepiped, double rotation_angle) {
        // ----> Drawing sprite with actual orientation
        this.sprite.render(g, parallelepiped, INIT_ANGLE - rotation_angle);

        // ----> Drawing HP rectangle
        if (isSelected) {
            parallelepiped.render(g);
        }

        /* TODO Move it in HUD Class.render() */
        Color hpColor = null;

        int percentageHP   = 100 * hitPoints / maxHitPoints;
        int actualPartOfHP = (100 * hitPoints / maxHitPoints - 1) / 25;

        if (actualPartOfHP == 3){
            hpColor = Color.GREEN;
        }

        if (actualPartOfHP == 0){
            hpColor = Color.RED;
        }

        if (0 < actualPartOfHP && actualPartOfHP < 3) {
            hpColor = Color.YELLOW;
        }

        // "healthy" HP
        g.setColor(hpColor);
        GraphExtensions.fillRect(g, new Rectangle(getAbsLoc()[0], getAbsLoc()[1] + getAbsSize()[1], getAbsSize()[0] * percentageHP / 100, 5), 0);

        // "loosed" HP
        g.setColor(Color.BLACK);
        GraphExtensions.fillRect(g, new Rectangle(
                getAbsLoc()[0] + getAbsSize()[0] * percentageHP / 100,
                getAbsLoc()[1] + getAbsSize()[1],
                getAbsSize()[1] * (100 - percentageHP) / 100,
                5),
                0
        );

        GraphBugfixes.drawRect(g, new Rectangle(getAbsLoc()[0], getAbsLoc()[1] + getAbsSize()[1], getAbsSize()[0], 5));

        // Mark the center of the object
        g.setColor(Color.YELLOW);
        GraphBugfixes.drawRect(g, new Rectangle(getAbsCenterInteger()[0], getAbsCenterInteger()[1],
                2 - BLOCK_SIZE % 2, 2 - BLOCK_SIZE % 2));
    }

    public Parallelepiped getParallelepiped() {
        return parallelepiped;
    }

    public int[] getAbsLoc() { return parallelepiped.getAbsLoc(); }
    public int[] getLoc() { return parallelepiped.getLoc(); }
    public int[] getSize() { return parallelepiped.getSize(); }
    public int[] getAbsSize() { return parallelepiped.getAbsSize(); }
    public double[] getAbsCenterDouble() { return parallelepiped.getAbsCenterDouble(); }
    public Integer[] getAbsCenterInteger() { return parallelepiped.getAbsCenterInteger(); }
    public int getAbsRight() { return parallelepiped.getAbsRight(); }
    public int getAbsBottom() { return parallelepiped.getAbsBottom(); }

    public void setDestinationPoint(Integer [] dest) {
        // TODO: check if coordinates are within restrictions
        if (destPoint == null) {
            this.destPoint = new Integer[2];
        }

        this.destPoint[0] = dest[0];
        this.destPoint[1] = dest[1];

        if (this instanceof Shootable) {
            ((Shootable)this).unsetTargetObject();
            ((Shootable)this).unsetTargetPoint();
        }

        LOG.debug("Destination OBJ_" + this.playerId + ": x=" + dest[0] + ", y=" + dest[1]);
    }

    public boolean rotateTo(Integer [] point) {
        if (ROTATE_MOD > 0) rotateToPointOnRay(point);
        //else rotateToAngle(point);

        // This is only "Tank" object logic
        //  We not moving while angle to target will not be small enough
        // TODO 1) If Tank start moving we need move to "looking forward" direction
        //  and turn Tank in the direction of rotation
        // TODO 2) If Tank not moving but target moving, Tank must rotate to target
        if (this.preMoveAngle > 0 && !angleBetweenRayAndPointLessThan(point, Math.toRadians(this.preMoveAngle))) {
                return true;
        }

        return false;
    }

/*
    public void rotateToAngle(Integer [] point) {
        double destAngle =...;
        if (Math.abs(currAngle - destAngle) < rotation_speed) {
            return;
        }

        int direction;
        if (rotateMode > 0) direction = getRotationDirectionRay(point);
        else direction = getRotationDirectionPolar();

        // It's clear that the point lies behind the ray that is 180°
        // Otherwise (in case 0°) Math.abs(currAngle - destAngle) < rotation_speed must return true
        if (direction == 0) {
            direction = randomSign();
        }

        // Rotate
        this.currAngle += rotation_speed * direction;
        this.currAngle %= Math.toRadians(360); // TODO: maybe it is possible to optimize division (for example write own func which subtract 360 until it gets less than 360)
        LOG.debug("New Sprite Ang: " + currAngle);
    }
*/

/*
    public Integer[] getTargetOrDestinationPoint() {
        Integer v[] = null;
        if (destPoint != null) {
            v = new Integer[] {destPoint[0], destPoint[1]};
        } else if (this instanceof Shootable) {
            GameObject target = ((Shootable)this).getTargetObject();
            if (target != null) {
                v = new Integer[] {target.loc[0], target.loc[1]};
            }
        }

        return v;
    }
*/

    public void rotateToPointOnRay(Integer[] point) {

        if (point == null || angleBetweenRayAndPointSmallEnough(point)) {
            LOG.debug("Destination reached or undefined, rotation aborted");
            return;
        }

        int direction = getRotationDirectionRay(point);
        LOG.debug("New direction: " + direction);

        // It's clear that the point lies behind the ray that is 180°
        // Otherwise (in case 0°) angleBetweenRayAndPoint*** must return true
        if (direction == 0) {
            direction = randomSign();
        }

        this.currAngle += rotation_speed * direction;
        this.currAngle %= Math.toRadians(360); // TODO: maybe it is possible to optimize division (for example write own func which subtract 360 until it gets less than 360)

        LOG.debug("New Sprite Ang: " + currAngle);
     }

    // TODO next_x, next_y
    // FIXME boolean ?
    public boolean moveTo(Integer [] next) {
        // FIXME Not good calculate angle every time. Need optimize in future
        if (this instanceof Rotatable && rotateTo(next)) {
            return true;
        }

        // Calculate future coordinates where we want to move hypothetically (if nothing prevents this)
        int new_x, new_y, new_z;
        Integer new_center[] = MathTools.getNextPointOnRay(getAbsCenterInteger(), next, speed);
        LOG.debug("new_center_x=" + new_center[0] + ", new_center_y=" + new_center[1]);
        LOG.debug("old_center_x=" + getAbsCenterInteger()[0] + ", old_center_y=" + getAbsCenterInteger()[1]);
        
        // translation vector
        int dx = new_center[0] - getAbsCenterInteger()[0];
        int dy = new_center[1] - getAbsCenterInteger()[1];
        LOG.debug("dx=" + dx + ", dy=" + dy);
        if ((dx == 0) && (dy == 0)) {
            // Destination point reached already
            unsetDestinationPoint();
            return false;
        }

        // move left-top object angle to the same vector which the center was moved to
        new_x = getAbsLoc()[0] + dx;
        new_y = getAbsLoc()[1] + dy;
        new_z = getAbsLoc()[2]; // so far we don't support 3D

        LOG.debug("move?: (" + getAbsLoc()[0] + "," + getAbsLoc()[1] + ")->(" + new_x + ", " + new_y + "), speed=" + speed);

        if (! GameMap.getInstance().contains(
                // new_x, new_y, new_z - absolute coordinates, not aliquote to the grid vertices
                new Parallelepiped(new_x, new_y, new_z, getSize()[0], getSize()[1], getSize()[2]))
        ) {
            // prevent movement outside the map
            LOG.debug("prevent movement outside the map");
            return false;
        }

        Rectangle new_rect = getRect();
        new_rect.translate(dx, dy); // translocated rectangle

        //if (GameMap.getInstance().occupied(new_rect, this)) {
        if (INTERSECTION_STRATEGY_SEVERITY > 0) {

            // Hmm.. we cannot move exactly to the destination point, because it is occupied
            // However we can get to it as closer as possible.

            // ALGORITHM FOR ANALYSING OF IMPEDIMENTS ON THE WAY OF THE OBJECT WITH RECTANGLE SHAPE

            // Step 1. Detect all map(grid) blocks which are intersected or contained by two parallelograms
            // which are represented by the movement (translation) of two "forward"-edges of the object rectangle.
            // In case the movement is completely horizontal or vertical there will be only one parallelogram.
            // pgmHoriz - parallelogram represented by the movement of the top/bottom edge
            // pgmVert - parallelogram represented by the movement of the right/left edge

            Graphics currentGraphics = Main.getFrame().getRootPane().getGraphics(); // for DEBUG
            ParallelogramHorizontal pgmHoriz = null;
            ParallelogramVertical pgmVert = null;
            GridMatrixHorizontal pgmHorizOccupiedBlocks = null;
            GridMatrixVectical pgmVertOccupiedBlocks = null;

            if (dy != 0) { // a)
                if (dy < 0) pgmHoriz = new ParallelogramHorizontal(new_rect.x, new_rect.y, new_rect.width, getAbsLoc()[1] - new_rect.y + 1, -dx);
                if (dy > 0) pgmHoriz = new ParallelogramHorizontal(getAbsLoc()[0], getAbsLoc()[1] + getAbsSize()[1] - 1, new_rect.width, new_rect.y - getAbsLoc()[1] + 1, dx);
                pgmHorizOccupiedBlocks = new GridMatrixHorizontal(pgmHoriz);
                pgmHoriz.render(currentGraphics); // DEBUG (draw parallelogram)
                pgmHorizOccupiedBlocks.render(currentGraphics); // DEBUG (draw occupied blocks)
            }
            if (dx != 0) { // b)
                if (dx < 0) pgmVert = new ParallelogramVertical(new_rect.x, new_rect.y, getAbsLoc()[0] - new_rect.x + 1, new_rect.height, -dy);
                if (dx > 0) pgmVert = new ParallelogramVertical(getAbsLoc()[0] + getAbsSize()[0] - 1, getAbsLoc()[1], new_rect.x - getAbsLoc()[0] + 1, new_rect.height, dy);
                pgmVertOccupiedBlocks = new GridMatrixVectical(pgmVert);
                pgmVert.render(currentGraphics); // DEBUG (draw parallelogram)
                pgmVertOccupiedBlocks.render(currentGraphics); // DEBUG (draw occupied blocks)
            }

            // Step 2. Iterate through all blocks obtained on the step 1 checking the map regarding which game objects
            // are located on each block which is being iterated. Store all such blocks to a list/hash with no repetition.
            // Exclude the current game object (unit) itself.
            HashSet<Rectangle> affectedObjects = new HashSet<Rectangle>();

            // Checking blocks occupied by the horizontal parallelogram:
            if (dy != 0) { // a)
                for (int i = 0; i <= pgmHorizOccupiedBlocks.bottom - pgmHorizOccupiedBlocks.top; i++) {
                    for (int j = pgmHorizOccupiedBlocks.left[i]; j <= pgmHorizOccupiedBlocks.right[i]; j++) {
                        HashSet<GameObject> objectsOnBlock = GameMap.getInstance().objectsOnMap[j][i + pgmHorizOccupiedBlocks.top];
                        for (GameObject gameObject : objectsOnBlock) {
                            if (gameObject != this) affectedObjects.add(gameObject.getRect());
                        }

                        // In case of INTERSECTION_STRATEGY_SEVERITY == 2 it is not allowed
                        // to go through blocks partly occupied by another objects
                        if (
                                (INTERSECTION_STRATEGY_SEVERITY > 1) // only one object is allowed on the block
                             && (!objectsOnBlock.isEmpty())          // somebody is on the block
                             && (!objectsOnBlock.contains(this))     // but not me
                        ) {
                            affectedObjects.add(GameMap.getInstance().landscapeBlocks[j][i + pgmHorizOccupiedBlocks.top].getRect());
                        }
                    }
                }
            }
            // Checking blocks occupied by the vertical parallelogram:
            if (dx != 0) { // b)
                for (int i = 0; i <= pgmVertOccupiedBlocks.right - pgmVertOccupiedBlocks.left; i++) {
                    for (int j = pgmVertOccupiedBlocks.top[i]; j <= pgmVertOccupiedBlocks.bottom[i]; j++) {
                        HashSet<GameObject> objectsOnBlock = GameMap.getInstance().objectsOnMap[i + pgmVertOccupiedBlocks.left][j];
                        for (GameObject gameObject : objectsOnBlock) {
                            if (gameObject != this) affectedObjects.add(gameObject.getRect());
                        }

                        // In case of INTERSECTION_STRATEGY_SEVERITY == 2 it is not allowed
                        // to go through blocks partly occupied by another objects
                        if (
                                (INTERSECTION_STRATEGY_SEVERITY > 1) // only one object is allowed on the block
                                        && (!objectsOnBlock.isEmpty())          // somebody is on the block
                                        && (!objectsOnBlock.contains(this))     // but not me
                        ) {
                            affectedObjects.add(GameMap.getInstance().landscapeBlocks[i + pgmVertOccupiedBlocks.left][j].getRect());
                        }
                    }
                }
            }

            /* Step 3. Iterate through all game objects (their rectangles) from the step 2 in order to get:
               a) All of them whose bottom (if dy > 0)/top(if dy < 0) edge has a common section with that parallelogram,
               whose edges are parallel Ox, that is pgmHoriz.
               b) All of them whose left(if dx > 0)/right(if dx < 0) edge has a common section with that parallelogram,
               whose edges are parallel Oy, that is pgmVert.
               c) All of them whose left-bottom(if dx > 0, dy > 0)/right-bottom(if dx < 0, dy > 0)/
               right-top(if dx < 0, dy < 0)/left-top(if dx > 0, dy < 0) vertice belongs to the terminating section between parallelograms.
             */
            HashSet<Rectangle> suspectedObjectsA = new HashSet<Rectangle>();
            HashSet<Rectangle> suspectedObjectsB = new HashSet<Rectangle>();
            HashSet<Rectangle> suspectedObjectsC = new HashSet<Rectangle>();

            /* Cs corresponds to the direction where the current object is going to move
               Depending on the direction we choose the corresponding edge or vertice.
               For example:
               a) The object is moving to the right and down => Cs is a right-bottom vertice.
               Cs[0] = x_right_bottom, Cs[1] = y_right_bottom
               b) If the object is moving just left (dy=0) => Cs is an abscisse of the left edge.
               Cs[0] = x_left, Cs[1] = null (impossible to choose a point, the whole edge is moving if dy=0)
               c) If the object is moving just down (dx=0) => Cs is an ordinate of the bottom edge.
               Cs[1] = y_bottom, Cs[0] = null (impossible to choose a point, the whole edge is moving if dx=0)
               d) dx=dx=0 => Cs[0]=Cs[1]=null (should be impossible, because we check above that dx!=0 or dy!=0)
             */
            Integer [] Cs = new Integer [] {null, null};

            if (dx < 0) Cs[0] = getAbsLoc()[0]; // left of the current object
            if (dx > 0) Cs[0] = getAbsRight(); // right of the current object
            if (dy < 0) Cs[1] = getAbsLoc()[1]; // top of the current object
            if (dy > 0) Cs[1] = getAbsBottom(); // bottom of the current object

            // Ct is where Cs should be moved to hypothetically
            Integer [] Ct = new Integer []{null, null};
            if (Cs[0] != null) Ct[0] = Cs[0] + dx;
            if (Cs[1] != null) Ct[1] = Cs[1] + dy;

            for (Rectangle rect : affectedObjects) {

                GraphExtensions.fillRect(currentGraphics, rect, 1); // DEBUG
                Integer [] go_top_left = new Integer[] {rect.x, rect.y};
                Integer [] go_top_right = new Integer[] {rect.x + rect.width - 1, rect.y};
                Integer [] go_bottom_left = new Integer[] {rect.x, rect.y + rect.height - 1};
                Integer [] go_bottom_right = new Integer[] {rect.x + rect.width - 1, rect.y + rect.height - 1};

                // a
                if (dy != 0) {
                    // NOTE: Here "dy < 0" means movement UP since Y_ORIENT=-1 (we are not in Cartesian system in the game).
                    if (dy < 0) { // UP
                        // check intersection/touching of the object rectangle by the bottom edge of the impediment
                        if (pgmHoriz.intersects(go_bottom_left, go_bottom_right) > -1) {
                            suspectedObjectsA.add(rect);
                        }
                    }
                    if (dy > 0) { // DOWN
                        // check intersection/touching of the object rectangle by the top edge of the impediment
                        if (pgmHoriz.intersects(go_top_left, go_top_right) > -1) {
                            suspectedObjectsA.add(rect);
                        }
                    }
                }

                // b
                if (dx != 0) {
                    if (dx > 0) { // RIGHT
                        // check intersection/touching of the object rectangle by the left edge of the impediment
                        if (pgmVert.intersects(go_top_left, go_bottom_left) > -1) {
                            suspectedObjectsB.add(rect);
                        }
                    }
                    if (dx < 0) { // LEFT
                        // check intersection/touching of the object rectangle by the right edge of the impediment
                        if (pgmVert.intersects(go_top_right, go_bottom_right) > -1) {
                            suspectedObjectsB.add(rect);
                        }
                    }
                }

                // c
                if ((dx != 0) && (dy != 0)) {
                    if ((dx > 0) && (dy < 0)) { // UP-RIGHT
                        // check if the bottom-left vertice of the impediment belongs to the common section CsCt
                        // connecting pgmHoriz and pgmVert
                        if (MathTools.sectionContains(Cs, go_bottom_left, Ct) > -1) {
                            suspectedObjectsC.add(rect);
                        }
                    }
                    if ((dx < 0) && (dy < 0)) { // UP-LEFT
                        // check if the bottom-left vertice of the impediment belongs to the common section CsCt
                        // connecting pgmHoriz and pgmVert
                        if (MathTools.sectionContains(Cs, go_bottom_right, Ct) > -1) {
                            suspectedObjectsC.add(rect);
                        }
                    }
                    if ((dx > 0) && (dy > 0)) { // DOWN-RIGHT
                        // check if the top-left vertice of the impediment belongs to the common section CsCt
                        // connecting pgmHoriz and pgmVert
                        if (MathTools.sectionContains(Cs, go_top_left, Ct) > -1) {
                            suspectedObjectsC.add(rect);
                        }
                    }
                    if ((dx < 0) && (dy > 0)) { // DOWN-LEFT
                        // check if the top-right vertice of the impediment belongs to the common section CsCt
                        // connecting pgmHoriz and pgmVert
                        if (MathTools.sectionContains(Cs, go_top_right, Ct) > -1) {
                            suspectedObjectsC.add(rect);
                        }
                    }
                }

            }

            /* Step 4.
               a) From the game objects obtained on the step 3a choose only one whose bottom(if dy > 0)/top(if dy < 0) edge
               is lowest(if dy > 0)/highest(if dy < 0) and remember its ordinate Ya.
               b) From the game objects obtained on the step 3b choose only one whose left(if dx > 0)/right(if dx < 0) edge
               is leftest(if dx > 0)/rightest(if dx < 0) and remember its abscissa Xb.
               c) From the game objects obtained on the step 3c choose only one whose left-bottom(if dx 0, dy > 0)/
               right-bottom(if dx < 0, dy > 0)/right-top(if dx < 0, dy < 0)/left-top(if dx > 0, dy > 0) vertice
               is closest to Cs and remember its coordinates Xc, Yc.
             */

            int opt_line_A_found = 0;
            int opt_line_B_found = 0;
            int opt_point_C_found = 0;

            // a
            Integer Ya = null;
            if (suspectedObjectsA.size() > 0) opt_line_A_found = 1;
            for (Rectangle rect : suspectedObjectsA) {

                GraphExtensions.fillRect(currentGraphics, rect, 2); // DEBUG

                if (dy < 0) { // UP
                    int Y_bottom = GraphBugfixes.getMaxY(rect);
                    if (Ya == null) {
                        Ya = Y_bottom; // first time only
                        continue;
                    }
                    // the lowest bottom
                    if (Y_bottom > Ya) Ya = Y_bottom;
                }
                if (dy > 0) { // DOWN
                    int Y_top = rect.y;
                    if (Ya == null) {
                        Ya = Y_top; // first time only
                        continue;
                    }
                    // the highest top
                    if (Y_top < Ya) Ya = Y_top;
                }
            }

            // b
            Integer Xb = null;
            if (suspectedObjectsB.size() > 0) opt_line_B_found = 1;
            for (Rectangle rect : suspectedObjectsB) {

                GraphExtensions.fillRect(currentGraphics, rect, 2); // DEBUG

                if (dx > 0) { // RIGHT
                    int X_left = rect.x;
                    if (Xb == null) {
                        Xb = X_left; // first time only
                        continue;
                    }
                    // the leftest left
                    if (X_left < Xb) Xb = X_left;
                }
                if (dx < 0) { // LEFT
                    int X_right = GraphBugfixes.getMaxX(rect);
                    if (Xb == null) {
                        Xb = X_right; // first time only
                        continue;
                    }
                    // the rightest right
                    if (X_right > Xb) Xb = X_right;
                }
            }

            // c
            Integer [] Pc = null;
            Integer [] Pc_opt = null;
            Long distSqrValMin = null;
            Long distSqrValCurr = -1L;
            if (suspectedObjectsC.size() > 0) opt_point_C_found = 1;

            for (Rectangle rect : suspectedObjectsC) {

                GraphExtensions.fillRect(currentGraphics, rect, 2); // DEBUG
                Integer [] go_top_left = new Integer[] {rect.x, rect.y};
                Integer [] go_top_right = new Integer[] {rect.x + rect.width - 1, rect.y};
                Integer [] go_bottom_left = new Integer[] {rect.x, rect.y + rect.height - 1};
                Integer [] go_bottom_right = new Integer[] {rect.x + rect.width - 1, rect.y + rect.height - 1};

                // TODO: it is known outside of the looop about signs of dx, dy
                // It is possible to avoid these 4 if-else here?
                // UP-RIGHT
                if ((dx > 0) && (dy < 0)) Pc_opt = go_bottom_left;
                // UP-LEFT
                if ((dx < 0) && (dy < 0)) Pc_opt = go_bottom_right;
                // DOWN-RIGHT
                if ((dx > 0) && (dy > 0)) Pc_opt = go_top_left;
                // DOWN-LEFT
                if ((dx < 0) && (dy > 0)) Pc_opt = go_top_right;

                // validation
                if ((dx == 0) || (dy == 0)) terminateNoGiveUp(1000, "Impossible: dx or dy = 0 on thep 4c.");

                if (Pc == null) { // first time only
                    Pc = new Integer[] {Pc_opt[0], Pc_opt[1]};
                    distSqrValMin = MathTools.distSqrVal(Cs, Pc);
                    continue;
                }

                distSqrValCurr = MathTools.distSqrVal(Cs, Pc);
                if (distSqrValCurr < distSqrValMin) {
                    distSqrValMin = distSqrValCurr;
                    Pc = new Integer[] {Pc_opt[0], Pc_opt[1]};
                }
            }

            /*  Step 5.
                Having 3 "restricting" points/lines from 4a, 4b, 4c calculate 3 position candidates
                where the object center should be moved to at the end. Choose that one out of 3
                which is closest to the center (we choose the minimal, because the FIRST impediment
                which the object meets on the way stops it).
             */

            int num_opt_points = 0;
            LOG.debug("a_num=" + suspectedObjectsA.size() + ", b_num=" + suspectedObjectsB.size() + ", c_num=" + suspectedObjectsC.size());
            LOG.debug("a=" + opt_line_A_found + ", b=" + opt_line_B_found + ", c=" + opt_point_C_found);

            HashMap<Integer, Integer[]> opt_points = new HashMap<Integer, Integer[]>();

            // a (dy != 0)
            if (opt_line_A_found != 0) {
                LOG.debug("Ya=" + Ya);
                Ya -= (int)Math.signum(dy); // we should not overlap even 1 pixel of the border of the impediment
                opt_points.put(num_opt_points, new Integer[] {(Ya - Cs[1]) * dx / dy, Ya - Cs[1]});
                num_opt_points++;
            }
            // b (dx != 0)
            if (opt_line_B_found != 0) {
                LOG.debug("Xb=" + Xb);
                Xb -= (int)Math.signum(dx); // we should not overlap even 1 pixel of the border of the impediment
                opt_points.put(num_opt_points, new Integer[] {Xb - Cs[0], (Xb - Cs[0]) * dy / dx});
                num_opt_points++;
            }
            // c ( dx != 0 and dy != 0)
            if (opt_point_C_found != 0) {
                LOG.debug("Pc=(" + Pc[0] + ", " + Pc[1] + ")");

                Pc[0] -= (int)Math.signum(dx); // we should not overlap even 1 pixel of the border of the impediment
                Pc[1] -= (int)Math.signum(dy); // we should not overlap even 1 pixel of the border of the impediment
                opt_points.put(num_opt_points, new Integer[] {Pc[0] - Cs[0], Pc[1] - Cs[1]});
                num_opt_points++;
            }

            int i_opt = 0;
            if (num_opt_points > 0) {
                distSqrValCurr = -1L;
                distSqrValMin = MathTools.sqrVal(opt_points.get(0)[0]) + MathTools.sqrVal(opt_points.get(0)[1]);
                for (int i = 0; i < num_opt_points; i++) {
                    distSqrValCurr = MathTools.sqrVal(opt_points.get(i)[0]) + MathTools.sqrVal(opt_points.get(i)[1]);
                    if (distSqrValCurr < distSqrValMin) {
                        distSqrValMin = distSqrValCurr;
                        i_opt = i;
                    }
                }

                int dx_opt = opt_points.get(i_opt)[0];
                int dy_opt = opt_points.get(i_opt)[1];
                LOG.debug("dx_opt=" + dx_opt + ", dy_opt=" + dy_opt);
                //return false;
                //}

                if ((dx_opt == 0) && (dy_opt == 0)) {
                    LOG.warn("Cannot move even to 1 pixel, everything occupied!");
                }
                if ((dx_opt == dx) && (dy_opt == dy)) {
                    LOG.warn("The performance-consuming calculation started in vain: dx=dx_opt, dy_dy_opt.");
                }

                new_center[0] = getAbsCenterInteger()[0] + dx_opt;
                new_center[1] = getAbsCenterInteger()[1] + dy_opt;
                new_x = getAbsLoc()[0] + dx_opt;
                new_y = getAbsLoc()[1] + dy_opt;
                new_z = getAbsLoc()[2]; // so far we don't support 3D

                new_rect = getRect();
                new_rect.translate(dx_opt, dy_opt); // translocated rectangle
            }

            if (GameMap.getInstance().occupied(new_rect, this)) {
                Main.terminateNoGiveUp(1000, "Objects overlapping has been detected! The algorithm has a bug!");
            }
        }

        // All checks passed - do movement finally:
        if (new_center[0] == next[0] && new_center[1] == next[1]) {
            // Destination point reached
            unsetDestinationPoint();
        }

        GameMap.getInstance().eraseObject(this);

        this.parallelepiped.loc[0] = new_x;
        this.parallelepiped.loc[1] = new_y;
        this.parallelepiped.loc[2] = new_z;

        GameMap.getInstance().registerObject(this);

//        LOG.debug("move: x=" + loc[0] + ", y=" + loc[1] + ", obj=" + this);

        return true;
    }

    public void deselect() {
        this.isSelected = false;
    }

    public Rectangle getRect () {
        return parallelepiped.getAbsBottomRect();
    }

    public GridRectangle getGridRect() {
        return parallelepiped.getBottomRect();
    }

    // TODO Use Patterns here for: Point, point_x, point_y, GameObject, Building
    public boolean contains (Integer[] point) {
        return this.getRect().contains(point[0], point[1]);
    }

    // TODO Remove setters. Use Class.attr = newVal
    public void setOwner(int plId) {
        this.playerId = plId;
    }

    public void unsetDestinationPoint() {
        this.destPoint = null;
    }

    public void select() {
        this.isSelected = true;
    }

    // TODO Remove getters. Use Class.attr
    public int getPlayerId() {
        return playerId;
    }

/*
    public int getRotationDirectionPolar () {
        double destAngle = ...;
        double diffAngles = destAngle - currAngle;

        if (diffAngles > 0 && diffAngles < Math.toRadians(180)) {
            return 1;
        }

        if (diffAngles > Math.toRadians(180)) {
            return -1;
        }

        if (diffAngles < 0 && diffAngles > -Math.toRadians(180)) {
            return -1;
        }

        if (diffAngles < -Math.toRadians(180)) {
            return 1;
        }

        return 0;
    }
*/

    public int getRotationDirectionRay (Integer [] point) {

        if (point == null) throw new NullPointerException("getRotationDirectionRay: destPoint is NULL!");

        /*
        Hayami uses the following formula to determine on which of two semi-planes (left or right)
        obtained by the division of the 2D-space by the given ray with center in O(x0, y0) and direction-vector (xv,yv)
        lays the given point P(xp, yp):

                                           invariant = xv * (yp - y0) - (xp - x0) * yv

        If invariant > 0 then the point (xp, yp) lays on the left semi-plane from the vector (xv,yv)
        If invariant < 0 then the point (xp, yp) lays on the right semi-plane from the vector (xv,yv)
        If invariant == 0 then the point (xp, yp) lays on the line given by the vector (xv,yv)

        The goal is to rotate the ray given by the vector (xv, yv) until it intersects the point (xp, yp),
        but in the direction of minimal angle (shorter angle). It is obvious that it is shorter to turn to
        that semi-plane where the point is located.

        Thus,
        if invatiant > 0 we should turn left (counter clockwise)
        if invariant < 0 we should turn right (clockwise)
        if invariant == 0 we should turn backwards (180°)

        */

        // Point "O" - center of the object
        double x0 =             getAbsCenterDouble()[0];
        double y0 = Y_ORIENT  * getAbsCenterDouble()[1];

        // Point "P"
        int xp = point[0];
        int yp = Y_ORIENT  * point[1];

        // The direction-vector of the ray has coordinates (cos fi, sin fi) where fi = this.currAngle, so ...
        double invariant = (yp - y0) * Math.cos(this.currAngle) - (xp - x0) * Math.sin(this.currAngle);
        //double angleLeft = Math.acos(((xp - x0) * Math.cos(this.currAngle) + (yp - y0) * Math.sin(this.currAngle))
        //        / Math.sqrt(sqrVal(xp - x0) + sqrVal(yp - y0)));

	    // counter clockwise (1), clockwise (-1) or just backwards (0)
        // NOTE: In the Cartesian coordinate system the angle is growing counter clockwise!
        return (int)Math.signum(invariant);

    }

    public boolean angleBetweenRayAndPointLessThan(Integer [] point, double dAngle) {

        if (point == null) throw new NullPointerException("angleBetweenRayAndPointLessThan: destination and target points are both NULL!");

        // Point "O" - center of the object
        double x0 =             getAbsCenterDouble()[0];
        double y0 = Y_ORIENT  * getAbsCenterDouble()[1];

        // Point "P" - destination point of rotation
        int xp = point[0];
        int yp = Y_ORIENT  * point[1];

        // Distance to dest point less than 1 pixel
        // We don't check exactly == 0 because we use type double that can give small deviation
        if ((Math.abs(xp - x0) < 1) && (Math.abs(yp - y0) < 1)) {
            return true;
        }
        /* Here we use the formulae of the angle between two vectors:
                       cos (a1,b1) ^ (a2,b2) = (a1*a2 + b1*b2) / (|(a1,b1)| * |(a2,b2)|)
                       where |(ai,bi)| is then length of the i-vector, that is sqrt(ai*ai + bi*bi)

        We want to know when the angle between two vectors is less then the given value dAngle. Calculations:
                       (a1,b1) ^ (a2,b2) < dAngle

        f(x) = cos(x) is monotonically decreasing within [0; PI] and we consider only angles within [0; PI],
        because we don't care about orientation (we just need to get the angle between two rays given by two vectors).
        Taking into account the monotonic behaviour of f(x) = cos(x) in the interval [0; PI] we have this:
                       cos (a1,b1) ^ (a2,b2) > cos(dAngle)
                       (a1*a2 + b1*b2) / (|(a1,b1)| * |(a2,b2)|) > cos(dAngle)
                       a1*a2 + b1*b2 > |(a1,b1)| * |(a2,b2)| * cos(dAngle)
                       a1*a2 + b1*b2 > len(a1,b1) * len(a2,b2) * cos(dAngle)

        We are free to choose arbitrary the length of the vector which represents the ray of the objects' orientation,
        thus let us suppose that len(a1,b1) = 1, because a1 = cos(fi), b1 = sin(fi), therefore:
                       a2 * cos(fi) + b2 * sin(fi) > len(a2,b2) * cos(dAngle)

        Vector (a1,b1) represents the ray of the current objects' orientation (its length is 1)
        a1 = cos(this.currAngle), b1 = sin(this.currAngle)
        Vector (a2,b2) represents the ray from the center of the object towards destination rotation point "P"
        a2 = xp - x0, b2 = yp - y0

        */

        // len(a2,b2)
        double len = Math.sqrt(sqrVal(xp - x0) + sqrVal(yp - y0));
/*
        LOG.debug("Len = " + len);
        LOG.debug(
            "Current angle between vectors: " +
            Math.acos(
                ((xp - x0) * Math.cos(this.currAngle) + (yp - y0) * Math.sin(this.currAngle)) / len
            )
        );
        LOG.debug("a1 = " + 100*Math.cos(this.currAngle) + ", b1 = " + Math.sin(this.currAngle));
        LOG.debug("a2 = " + (xp - x0) + ", b2=" + (yp - y0));
        LOG.debug("currAngle = " + Math.toDegrees(this.currAngle));
*/
        return  (xp - x0) * Math.cos(this.currAngle) +
                (yp - y0) * Math.sin(this.currAngle) > len * Math.cos(dAngle);
    }

    public boolean angleBetweenRayAndPointSmallEnough(Integer [] point) {
        // Sometimes it is better to turn one more time (even if the angle different is already less than given delta)
        // For example if delta is 45°, we turned and now the angle between the target and our object is 40°.
        // In such case it is better to do one more turn step and the angle will 40° - 45° = -5° which is more precise.

        // Let imagine we are on the position where delta less then dFi (that is we are closer to the desired destAngle
        // less than the rotation step). Then we can turn once again and we cross the destination ray.
        // So, before we cross the destination ray the delta is fi1=abs(destAngle - currAngle)
        // After we cross the delta will be fi2=abs(destAngle - currAngle - step)
        // Both of them less than step, but we want to choose the one which absolute value is less.

        // It is obvious that: fi1 + fi2 = step
        // It means that fi1 <= step /2 or fi2 <= step /2
        // So we COULD set the stopping critetion: angleBetweenRayAndPointLessThan(point, rotation_speed * 0.5)
        // However it is very risky since we are not in the perfect math world, but in computer
        // Thus we must take some value which is "a little more than" 0.5, but enough more to cover calc errors.

        // What is "close to, but not really more than" 0.5?
        // 2/3, 3/5, 4/7, 5/9, 6/10, ... - this sequence approximates to 0.5
        // the sequence formula: (n + 1) / 2n
        // let's take for example n = 5, then K = 0.6 which is a little more than 0.5, but safe.
        int N = 5;
        double K = (N + 1) / (2.0 * N);
        return angleBetweenRayAndPointLessThan(point, K * rotation_speed);
    }
}
