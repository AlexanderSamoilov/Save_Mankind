package com.company.gamecontent;

import com.company.gamegraphics.Sprite;
import com.company.gamethread.Main;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

// For details read the DOC "Data Structure"
public class GameObject implements Moveable {
    // TODO Use Point3D class loc_x, loc_y, loc_z
    public Integer[] loc;         // Location of object (x, y, and z for airplanes)

    // TODO Use Point2D class
    protected Integer[] destPoint;   // The map point to move to (has x, y)

    //private double destAngle;           // Destination angle for sprite rotation
    private double initAngle; // we need it because the fi_1="angle where the face/gun of the object points to" IS NOT
    // fi_2="the angle" to which we rotate the object sprite (when the object is created its fi_1=90 and fi_2=0)
    // This is because in Dekart coordinates "North" means 90° and not 0°.
    private double currAngle;           // Current angle of sprite rotation

    protected Sprite sprite;

    // TODO Check body block usability
    // We should decide if we implement object of complex form.
    // That is, the object which contains of several block items
    // which are not a solid rectangular parallelepiped. */
//    bodyBlocks[][] bodyBlocks;

    protected int playerId;

    // TODO size_x, size_y, size_z
    protected int[] size;            // Object dimensions in GameMap cells (sX, sY, sZ)

    protected int hitPoints;
    protected int maxHitPoints;
    protected int speed;
    protected double rotation_speed;
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
    public GameObject(Sprite sprite, int x, int y, int z, int sX, int sY, int sZ, HashMap<Resource,Integer> res, int hp, int speed, int rot_speed, int arm, int hard, int bch, int ech, int eco) {
        // 1 - parent class specific parameters
        // 2 - validation
        if (sprite == null) {
            throw new IllegalArgumentException("Failed to initialize GameObject with spr=null.");
        }

        // TODO: check if the object borders are within map area!
        boolean valid;

        // Check object coordinates
        valid = Main.in_range(0, x, Restrictions.MAX_X, false);
        valid = valid && Main.in_range(0, y, Restrictions.MAX_Y, false);
        valid = valid && Main.in_range(0, z, Restrictions.MAX_Z, false);

        // Check object stats
        valid = valid && Main.in_range(0, hp, Restrictions.MAX_HP, false);
        valid = valid && Main.in_range(-Restrictions.MAX_SPEED, speed, Restrictions.MAX_SPEED, false);

        // Check object dimensions
        valid = valid && Main.in_range(0, sX, Restrictions.getMaxObjectSizeBlocks() + 1, true);
        valid = valid && Main.in_range(0, sY, Restrictions.getMaxObjectSizeBlocks() + 1, true);
        valid = valid && Main.in_range(0, sZ, Restrictions.getMaxObjectSizeBlocks() + 1, true);

        // Check object resources limits
        valid = valid && Main.in_range(
                0, res.get(Resource.MASS), Restrictions.MAX_MASS + 1, true
        );

        valid = valid && Main.in_range(
                0, res.get(Resource.ENERGY), Restrictions.MAX_ENERGY, false
        );

        if (!valid) {
            Main.terminateNoGiveUp(
                    1000,
                    "Failed to initialize " + getClass() + ". Some of parameters are beyond the restricted boundaries."
            );
        }

        this.sprite = sprite;
        this.loc = new Integer[]{
                x * Restrictions.BLOCK_SIZE, y * Restrictions.BLOCK_SIZE, z * Restrictions.BLOCK_SIZE
        };

        this.size = new int[]{sX, sY, sZ};

        this.res = new HashMap<Resource,Integer>();
        this.res.put(Resource.MASS, res.get(Resource.MASS));
        this.res.put(Resource.ENERGY, res.get(Resource.ENERGY));

        this.maxHitPoints = hp;
        this.speed = speed;
        this.rotation_speed = rot_speed * Math.PI / 180.0;
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

        // Default - object look up
        this.initAngle = Math.toRadians(90);
        this.currAngle = this.initAngle;

        // FIXME this.playerId = Faction.NEUTRAL
        this.playerId = -1;

        // Mark the object on the map
        GameMap.getInstance().registerObject(this);
    }

    public void render(Graphics g) {
        int rect_x    = loc[0];
        int rect_y    = loc[1];
        int rect_w    = size[0] * Restrictions.BLOCK_SIZE;
        int rect_h    = size[1] * Restrictions.BLOCK_SIZE;

        // ----> Drawing sprite with actual orientation
        this.sprite.render(g, initAngle - currAngle, rect_x, rect_y, rect_w, rect_h);

        // ----> Drawing HP rectangle
        if (isSelected) {
            g.drawRect(rect_x, rect_y, rect_w, rect_h);
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
        g.fillRect(rect_x, rect_y + rect_h, rect_w * percentageHP / 100, 5);

        // "loosed" HP
        g.setColor(Color.BLACK);
        g.fillRect(
                rect_x + rect_w * percentageHP / 100,
                rect_y + rect_h,
                rect_w * (100 - percentageHP) / 100,
                5
        );

        g.drawRect(rect_x, rect_y + rect_h, rect_w, 5);
    }

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

        Main.printMsg("Destination OBJ_" + this.playerId + ": x=" + dest[0] + ", y=" + dest[1]);
    }

    public void rotateTo() {
        if (Restrictions.rotateMode > 0) rotateToPointOnRay();
        //else rotateToAngle();
    }

    /*
    public void rotateToAngle() {
        double destAngle =...;
        if (Math.abs(currAngle - destAngle) < rotation_speed) {
            return;
        }

        int rotation_direction;
        if (Restrictions.rotateMode > 0) rotation_direction = getRotationDirectionRay();
        else rotation_direction = getRotationDirectionPolar();

        // It's clear that the point lies behind the ray that is 180°
        // Otherwise (in case 0°) Math.abs(currAngle - destAngle) < rotation_speed must return true
        if (rotation_direction == 0) {
            rotation_direction = randomSign();
        }

        // Rotate
        this.currAngle -= rotation_speed * rotation_direction;
        this.currAngle %= Math.toRadians(360); // TODO: maybe it is possible to optimize division (for example write own func which subtract 360 until it gets less than 360)
        Main.printMsg("New Sprite Ang: " + currAngle);
    }
*/

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

    public void rotateToPointOnRay() {

        // Point "B" - destination or target point
        Integer v[] = getTargetOrDestinationPoint();

        if (v == null || angleBetweenRayAndPointLessThanDefaultValue()) {
            Main.printMsg("Destination reached, rotation aborted");
            return;
        }

        Main.printMsg("-----------");

        // TODO Think about optimizing with angleBetweenRayAndPointLessThan
        int rotation_direction = getRotationDirectionRay();
        Main.printMsg("New rota: " + rotation_direction);

        // It's clear that the point lies behind the ray that is 180°
        // Otherwise (in case 0°) angleBetweenRayAndPointLessThan must return true
        if (rotation_direction == 0) {
            rotation_direction = randomSign();
        }

        this.currAngle -= rotation_speed * rotation_direction;
        this.currAngle %= Math.toRadians(360); // TODO: maybe it is possible to optimize division (for example write own func which subtract 360 until it gets less than 360)
        Main.printMsg("New Sprite Ang: " + currAngle);

     }

    // TODO next_x, next_y
    // FIXME boolean ?
    public boolean moveTo(Integer [] next) {
        // FIXME Not good calculate angle every time. Need optimize in future
        this.rotateTo();

        // This is only "Tank" object logic
        //  We not moving while angle to target will not be small enough
        // TODO 1) If Tank start moving we need move to "looking forward" direction
        //  and turn Tank in the direction of rotation
        // TODO 2) If Tank not moving but target moving, Tank must rotate to target
        if (! angleBetweenRayAndPointLessThan(Math.toRadians(45))) {
            Main.printMsg("< 45");
            return false;
        }

        // FIXME replace later
        int size_x = size[0];
        int size_y = size[1];

        // Store current coordinates (we roll back changes if the calculation reveals that we cannot move)
        int new_x, new_y;
        int new_z = loc[2];

        double norm = Math.sqrt(sqrVal(next[0] - loc[0]) + sqrVal(next[1] - loc[1]));
        //Main.printMsg("norm=" + norm + ", speed=" + speed);

        // TODO Move it to Math.Class checkNorm()
        // Avoid division by zero and endless wandering around the destination point
        if (norm <= speed) {
            // One step to target
            new_x = next[0];
            new_y = next[1];

        } else {
            // Many steps to target
            new_x = loc[0] + (int)((next[0] - loc[0]) * speed / norm);
            new_y = loc[1] + (int)((next[1] - loc[1]) * speed / norm);
        }

        //Main.printMsg("move?: x=" + newX + ", y=" + newY + ", norm=" + norm);

        // TODO Rename later!
        int cube_w = size[0] * Restrictions.BLOCK_SIZE;
        int cube_h = size[1] * Restrictions.BLOCK_SIZE;
        int cube_d = size[2] * Restrictions.BLOCK_SIZE;

        // TODO Is this must be here or lower? (about validation data after this)
        if (isIntersect(new_x, new_y, size_x, size_y)) {
            return false;
        }

        // TODO: check if the object borders are within map area!
        boolean not_valid;
        not_valid = new_x < 0 || new_y < 0 || new_z < 0;

        not_valid = not_valid || new_x + cube_w >= Restrictions.getMaxXAbs();
        not_valid = not_valid || new_y + cube_h >= Restrictions.getMaxYAbs();
        not_valid = not_valid || new_z + cube_d >= Restrictions.getMaxZAbs();

        if (not_valid) {
            return false;
        }

        // All checks passed - do movement finally:
        if (new_x == next[0] && new_y == next[1]) {
            // Destination point reached
            unsetDestinationPoint();
        }

        GameMap.getInstance().eraseObject(this);

        this.loc[0] = new_x;
        this.loc[1] = new_y;
        GameMap.getInstance().registerObject(this);

//        Main.printMsg("move: x=" + loc[0] + ", y=" + loc[1] + ", obj=" + this);

        return true;
    }

    public boolean isIntersect(int new_x, int new_y, int sX, int sY) {
        if (Restrictions.INTERSECTION_STRATEGY_SEVERITY == 0) {
            return false;
        }

        int rect_w = size[0] * Restrictions.BLOCK_SIZE;
        int rect_h = size[1] * Restrictions.BLOCK_SIZE;

        // FIXME What is it?
        int left_block_x = new_x / Restrictions.BLOCK_SIZE;
        int right_block_x = left_block_x + sX;
        int top_block_y = new_y / Restrictions.BLOCK_SIZE;
        int bottom_block_y = top_block_y + sY;

        // Check if we intersect another object
        // 1 - obtain the list of the map blocks which are intersected by the line of the object
        // FIXME What is i or j?
        for (int i = left_block_x; i <= right_block_x; i++) {
            for (int j = top_block_y; j <= bottom_block_y; j++) {
                // FIXME What is this?
                if (
                    (i != left_block_x) && (i != right_block_x) && 
                    (j != top_block_y) && (j != bottom_block_y)
                ) {
                    // Skip all blocks which are in the middle
                    continue;
                }

                // TODO: remove these temporary defense after implement safe check of map bounds:
                int i_fixed = (i == GameMap.getInstance().getWidth()) ? i-1 : i;
                int j_fixed = (j == GameMap.getInstance().getWidth()) ? j-1 : j;

                HashSet<GameObject> objectsOnBlock = GameMap.getInstance().objectsOnMap[i_fixed][j_fixed];
                if (objectsOnBlock.size() == 0) {
                    continue;
                }

                for (GameObject objOnBlock : objectsOnBlock) {
                    // Is me?
                    if (objOnBlock == this) {
                        continue;
                    }

                    // Multiple objects on the same block are allowed when they don't intersect
                    if (Restrictions.INTERSECTION_STRATEGY_SEVERITY > 1) {
                        //Main.printMsg("INTERSECTS: i=" + i_fixed + ", j=" + j_fixed + ", thisObject=" + this + ", objOnBlock=" + objOnBlock);
                        return true;
                    }

                    // Multiple objects on the same block are forbidden even
                    // if they actually don't intersect
                    Rectangle thisObjRect = new Rectangle(new_x, new_y, rect_w, rect_h);
                    Rectangle objOnBlockRect = new Rectangle(
                            objOnBlock.loc[0],
                            objOnBlock.loc[1],
                            objOnBlock.size[0] * Restrictions.BLOCK_SIZE,
                            objOnBlock.size[1] * Restrictions.BLOCK_SIZE
                    );

                    if (thisObjRect.intersects(objOnBlockRect)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void deselect() {
        this.isSelected = false;
    }

    // TODO How about delete objects?
    public Rectangle getRect () {
        return new Rectangle(
                this.loc[0],
                this.loc[1],
                this.size[0] * Restrictions.BLOCK_SIZE,
                this.size[1] * Restrictions.BLOCK_SIZE
        );
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
    // TODO Move it to Math.Class
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
    // TODO Move it to Math.Class
    public int getRotationDirectionRay () {

        // Point "B" - destination or target point
        Integer v[] = getTargetOrDestinationPoint();

        if (v == null) throw new NullPointerException("getRotationDirectionRay: destPoint is NULL!");

        // Point "O" - center of the objct
        int x0 = loc[0] + size[0] * Restrictions.BLOCK_SIZE / 2;
        int y0 = -(loc[1] + size[1] * Restrictions.BLOCK_SIZE / 2);

        int xb = v[0];
        int yb = -v[1];

        double invariant = (xb - x0) * Math.sin(this.currAngle) - (yb - y0) * Math.cos(this.currAngle);
        double angleLeft = Math.acos(((xb - x0) * Math.cos(this.currAngle) + (yb - y0) * Math.sin(this.currAngle)) / Math.sqrt(sqrVal(xb - x0) + sqrVal(yb - y0)));
        double dx = xb -x0;
        double dy = yb - y0;
        Main.printMsg("x0=" + x0 + " y0=" + y0 + " xb=" + xb + " yb=" + yb +
                "xb - x0= " + dx
                + "yb - y0= " + dy
                + "Math.cos(this.currAngle)= \n" + Math.cos(this.currAngle)
                + "Math.sin(this.currAngle)= \n" + Math.sin(this.currAngle)
                + "currAngle=" + this.currAngle + " invariant=" + invariant + " left=" + angleLeft);
	    // To the right (1), To the left (-1) of the line or on the line(0)
        return (int)Math.signum(invariant);

    }

    public boolean angleBetweenRayAndPointLessThan(double dAngle) {

        // Point "B" - destination or target point
        Integer v[] = getTargetOrDestinationPoint();

        if (v == null) throw new NullPointerException("angleBetweenRayAndPointLessThan: destination and target points are both NULL!");

        // Point "O" - center of the objct
        int x0 = loc[0] + size[0] * Restrictions.BLOCK_SIZE / 2;
        int y0 = -(loc[1] + size[1] * Restrictions.BLOCK_SIZE / 2);
        int xb = v[0];
        int yb = -v[1];

        double len = Math.sqrt(sqrVal(xb - x0) + sqrVal(yb - y0));

        return (xb - x0) * Math.cos(this.currAngle) + (yb - y0) * Math.sin(this.currAngle) > len * Math.cos(dAngle);
    }

    public boolean angleBetweenRayAndPointLessThanDefaultValue() {
        return angleBetweenRayAndPointLessThan(rotation_speed);
    }

    // TODO Move it to Math.Class
    public int randomSign() {
        Random random = new Random();

        int result = random.nextInt(2) - 1;

        return (result == 0) ? 1 : result;
    }

    // TODO Move it to Math.Class
    public int sqrVal(int value) {
        return value * value;
    }

}
