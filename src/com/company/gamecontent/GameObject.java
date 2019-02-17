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

    private int destAngle;           // Destination angle for sprite rotation
    private int currAngle;           // Current angle of sprite rotation

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
    protected int rotation_speed;
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
        this.rotation_speed = rot_speed;
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
        this.destAngle = 0;

        // Default - object look up
        this.currAngle = 0;

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
        this.sprite.render(g, currAngle, rect_x, rect_y, rect_w, rect_h);

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

    public boolean rotateTo() {
        if (currAngle == destAngle) {
            return false;
        }

        int rotation_direction = getRotationDirectionPolar();

        // Correcting angle for rotation
        if (rotation_direction > 0 && currAngle == 360) {
            this.currAngle = 0;
        }

        if (rotation_direction < 0 && currAngle == 0) {
            this.currAngle = 360;
        }

        // Rotate
        this.currAngle += rotation_speed * rotation_direction;
//        Main.printMsg("New Sprite Ang: " + currAngle);

        return true;
    }

    public boolean rotateToPointOnRay() {
        if (destPoint == null || rayIntersectsDestPoint()) {
//            Main.printMsg("Destination reached, rotation aborted");
            return false;
        }

        Main.printMsg("-----------");

        // TODO Think about optimizing with rayIntersectsDestPoint
        int rotation = getRotationDirectionRay();
        Main.printMsg("New rota: " + rotation);

        // We automatically learned that the point lies behind the ray.
        if (rotation == 0) {
            rotation = randomSign();
        }

        this.currAngle += rotation_speed * rotation;

        Main.printMsg("New Sprite Ang: " + currAngle);

        if (Math.abs(currAngle) == 360) {
            this.currAngle = 0;
        }

        return true;
    }

    // TODO next_x, next_y
    // FIXME boolean ?
    public boolean moveTo(Integer [] next) {
        // FIXME Not good calculate angle every time. Need optimize in future
        this.destAngle = calculateRotationPolar(next);
        this.rotateTo();

        // This is only "Tank" object logic
        //  We not moving while angle to target will not be small enough
        // TODO 1) If Tank start moving we need move to "looking forward" direction
        //  and turn Tank in the direction of rotation
        // TODO 2) If Tank not moving but target moving, Tank must rotate to target
        if (Math.abs(destAngle - currAngle) > 45) {
            return true;
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

    // TODO Move it to Math.Class
    public int calculateRotationPolar (Integer [] point) {
        // https://vk-book.ru/povorot-teni-elementa-v-storonu-kursora-na-javascript/
        // We use "up->down" vision in our Game.
        // It means we not need sheets of sprites to animate rotation effect in several directions
        // Instead of this we calculate angle "Theta" to rotate our sprite by Rectangular Triangle
        // If we abandon this implementation of sprites, we will need to use the "Orientation" class
        // to memorize each direction separately.
        // And the algorithm for calculating the angle may also need to be modified.

        int x0 = loc[0] + size[0] * Restrictions.BLOCK_SIZE / 2;
	    int y0 = loc[1] + size[1] * Restrictions.BLOCK_SIZE / 2;
	    int x  = point[0];
	    int y  = point[1];
//	    int any_x = x;
//	    int any_y = y0;

	    // Origin formula
//        double cos_theta = (
//                (
//                        sqrVal(x0-x) + sqrVal(y0-y)
//                        + sqrVal(x0-any_x) + sqrVal(y0-any_y)
//                        - sqrVal(x-any_x) - sqrVal(y-any_y)
//                )
//                / (
//                        2
//                        * Math.sqrt( sqrVal(x0-x) + sqrVal(y0-y) )
//                        * Math.sqrt( sqrVal(x0-any_x) + sqrVal(y0-any_y) )
//                )
//        );

        // Simplify formula
        double cos_theta = (
                Math.abs(x0-x) /
                Math.sqrt( sqrVal(x0-x) + sqrVal(y0-y) )
        );

	    int theta = (int) Math.toDegrees(Math.acos(cos_theta));

	    // "Round" alpha angle of rotation to step speed of rotation
        // So that we can know exactly how far we turned
        theta = rotation_speed * Math.round((float) theta / rotation_speed);

        // Here is researching were is dest_point located, and counting angle at 0 to 360 deg
        // This formulas edited to right oriented origin axis and 0-angle
        // as sprite 0-angle (Looking up on the map)
        int circle_angle = 0;

        // FIXME To many "if" statements. theta = [0, 90]. Is this right?
        // Right-Up
        // angle (0, 90)
        if(x > x0 && y < y0) {
            circle_angle = 90 - theta;
        }

        // Right-Down
        // angle (90, 180)
        if(x > x0 && y > y0) {
            circle_angle = 90 + theta;
        }

        // Left-Down
        // angle (180, 270)
        if(x < x0 && y > y0) {
            circle_angle = 180 + (90 - theta);
        }

        // Left-Up
        // angle (270, 360)
        if(x < x0 && y < y0) {
            circle_angle = 180 + (90 + theta);
        }

        // Left
        if(x < x0 && y == y0) {
            circle_angle = 270;
        }

        // Up
        if(x == x0 && y < y0) {
            circle_angle = 360;
        }

        // Right
        if(x > x0 && y == y0) {
            circle_angle = 90;
        }

        // Down
        if(x == x0 && y > y0) {
            circle_angle = 180;
        }

        return circle_angle;
    }

    // TODO Move it to Math.Class
    public int getRotationDirectionPolar () {
        int diffAngles = destAngle - currAngle;

        if (diffAngles > 0 && diffAngles < 180) {
            return 1;
        }

        if (diffAngles > 180) {
            return -1;
        }

        if (diffAngles < 0 && diffAngles > -180) {
            return -1;
        }

        if (diffAngles < -180) {
            return 1;
        }

        // If we rotate to the opposite direction, we not care how it happens
        //  But the cool solution is to randomly choose the direction of rotation.
        //  I get the maximum value of angle which is considered to be the opposite direction of rotation.
        //  Value must be 180.
        //  Since we rounded the angle by rotation_speed, we can reduce the condition.
        //  Alternative: If unit can move backward we must return 0 in this condition
        if (Math.abs(diffAngles) == 180) {
            return randomSign();
        }

        return 0;
    }

    // TODO Move it to Math.Class
    public int getRotationDirectionRay () {
        // Here use the straight line equation algorithm to find where is destination Point
        // and how we start rotation
        double tang = Math.tan(Math.toRadians(currAngle));

        int x0 = loc[0] + size[0] * Restrictions.BLOCK_SIZE / 2;
	    int y0 = loc[1] + size[1] * Restrictions.BLOCK_SIZE / 2;
	    int x = destPoint[0];
	    int y = destPoint[1];
	    int ax = x0 + 1;
	    int ay = (int) tang * (ax - x0) + y0;

	    // To the right (1), To the left (-1) of the line or on the line(0)
        if ((int) tang * (x - x0) + y0 > y0) {
            return 1;
        }

        if ((int) tang * (x - x0) + y0 < y0) {
            return -1;
        }

        return 0;
    }

    // TODO Move it to Math.Class
    // FIXME Rewrite
    public boolean rayIntersectsDestPoint() {
        double tang = Math.tan(Math.toRadians(currAngle));

        int x0 = loc[0] + size[0] * Restrictions.BLOCK_SIZE / 2;
	    int y0 = loc[1] + size[1] * Restrictions.BLOCK_SIZE / 2;
	    int x = destPoint[0];
	    int y = destPoint[1];
	    int ax = x0 + 1;    // FIXME Take a 1 pixel point can make bad calculations.
	    int ay = (int) tang * (ax - x0) + y0;

	    boolean pseudoscalar = (x - x0) * (ay - y0) == (ax - x0) * (y - y0);
	    boolean x_on_ray     = (x - x0) * (ax - x0) >= 0;
	    boolean y_on_ray     = (y - y0) * (ay - y0) >= 0;

        return pseudoscalar && x_on_ray && y_on_ray;
    }

    // TODO Move it to Math.Class
    public int randomSign() {
        Random random = new Random();

        int result = random.nextInt(2) - 1;

        return (result == 0) ? 1 : result;
    }

    // TODO Move it to Math.Class
    public int getSign(int val) {
        return (val >= 0) ? 1 : -1;
    }

    // TODO Move it to Math.Class
    public int sqrVal(int value) {
        return value * value;
    }

}
