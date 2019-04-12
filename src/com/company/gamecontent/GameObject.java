package com.company.gamecontent;

import com.company.gamecontent.Parallelepiped.GridRectangle;
import com.company.gamegraphics.GraphBugfixes;
import com.company.gamegraphics.GraphExtensions;
import com.company.gamegraphics.Sprite;
import com.company.gamethread.Main;
import com.company.gametools.MathTools;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;

import static com.company.gamecontent.Restrictions.*;
import static com.company.gametools.MathTools.in_range;
import static com.company.gametools.MathTools.randomSign;
import static com.company.gametools.MathTools.sqrVal;

import static com.company.gamethread.Main.printMsg;
import static com.company.gamethread.Main.terminateNoGiveUp;

// For details read the DOC "Data Structure"
public class GameObject implements Moveable, Rotatable, Centerable, Renderable, Selectable {

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

        printMsg("Destination OBJ_" + this.playerId + ": x=" + dest[0] + ", y=" + dest[1]);
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
        printMsg("New Sprite Ang: " + currAngle);
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
            //Main.printMsg("Destination reached or undefined, rotation aborted");
            return;
        }

        int direction = getRotationDirectionRay(point);
        //Main.printMsg("New rota: " + direction);

        // It's clear that the point lies behind the ray that is 180°
        // Otherwise (in case 0°) angleBetweenRayAndPoint*** must return true
        if (direction == 0) {
            direction = randomSign();
        }

        this.currAngle += rotation_speed * direction;
        this.currAngle %= Math.toRadians(360); // TODO: maybe it is possible to optimize division (for example write own func which subtract 360 until it gets less than 360)

        //Main.printMsg("New Sprite Ang: " + currAngle);
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
        //Main.printMsg("new_center_x=" + new_center[0] + ", new_center_y=" + new_center[1]);
        //Main.printMsg("old_center_x=" + getAbsCenterInteger()[0] + ", old_center_y=" + getAbsCenterInteger()[1]);
        
        // translation vector
        int dx = new_center[0] - getAbsCenterInteger()[0];
        int dy = new_center[1] - getAbsCenterInteger()[1];
        //Main.printMsg("dx=" + dx + ", dy=" + dy);
        if ((dx == 0) && (dy == 0)) {
            // Destination point reached already
            unsetDestinationPoint();
            return false;
        }

        // move left-top object angle to the same vector which the center was moved to
        new_x = getAbsLoc()[0] + dx;
        new_y = getAbsLoc()[1] + dy;
        new_z = getAbsLoc()[2]; // so far we don't support 3D

        //Main.printMsg("move?: (" + getAbsLoc()[0] + "," + getAbsLoc()[1] + ")->(" + new_x + ", " + new_y + "), speed=" + speed);

        if (! GameMap.getInstance().contains(
                // new_x, new_y, new_z - absolute coordinates, not aliquote to the grid vertices
                new Parallelepiped(new_x, new_y, new_z, getSize()[0], getSize()[1], getSize()[2]))
        ) {
            // prevent movement outside the map
            //Main.printMsg("prevent movement outside the map");
            return false;
        }

        Rectangle new_rect = getRect();
        new_rect.translate(dx, dy); // translocated rectangle

        if (GameMap.getInstance().occupied(new_rect, this)) {
            return false;
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

//        printMsg("move: x=" + loc[0] + ", y=" + loc[1] + ", obj=" + this);

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
        printMsg("Len = " + len);
        printMsg(
            "Current angle between vectors: " +
            Math.acos(
                ((xp - x0) * Math.cos(this.currAngle) + (yp - y0) * Math.sin(this.currAngle)) / len
            )
        );
        printMsg("a1 = " + 100*Math.cos(this.currAngle) + ", b1 = " + Math.sin(this.currAngle));
        printMsg("a2 = " + (xp - x0) + ", b2=" + (yp - y0));
        printMsg("currAngle = " + Math.toDegrees(this.currAngle));
*/
        return (xp - x0) * Math.cos(this.currAngle) + (yp - y0) * Math.sin(this.currAngle) > len * Math.cos(dAngle);
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
