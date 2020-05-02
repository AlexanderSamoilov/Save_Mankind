package com.company.gamecontent;

import com.company.gamegeom._3d.ParallelepipedOfBlocks;
import com.company.gamemath.cortegemath.point.Point2D_Integer;
import com.company.gamemath.cortegemath.point.Point3D_Integer;
import com.company.gamemath.cortegemath.vector.Vector3D_Integer;

import com.company.gamegraphics.GraphBugfixes;
import com.company.gamegraphics.GraphExtensions;
import com.company.gamegraphics.Sprite;

import com.company.gamethread.Main;
import com.company.gamethread.ParameterizedMutexManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import static com.company.gamecontent.Restrictions.*;
import static com.company.gamethread.Main.terminateNoGiveUp;
import static com.company.gametools.MathTools.*;

// For details read the DOC "Data Structure"
public class GameObject extends ParallelepipedOfBlocks implements Moveable, Rotatable, Renderable, Selectable {
    private static Logger LOG = LogManager.getLogger(GameObject.class.getName());

    protected Point3D_Integer destPoint; // The map point (x, y, z) where the object is going to move to

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
    public GameObject(Sprite sprite, Point3D_Integer loc, Vector3D_Integer dim, HashMap<Resource,Integer> res, int hp, int speed, int rot_speed, int preMoveAngle, int arm, int hard, int bch, int ech, int eco) {
        super(loc.multClone(BLOCK_SIZE), dim);

        // Creation of new game objects should be allowed from the main thread (game initialisation)
        // or from the calculation thread (on each game stage factories produce new units for example)
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M", "C")));

        // 1 - parent class specific parameters
        // 2 - validation
        if (sprite == null) {
            throw new IllegalArgumentException("Failed to initialize GameObject with sprite=null.");
        }

        // TODO: check if the object borders are within map area!
        boolean valid;

        // Check object coordinates
        valid =          in_range(0, loc.x(), MAX_X, false);
        valid = valid && in_range(0, loc.y(), MAX_Y, false);
        valid = valid && in_range(0, loc.z(), MAX_Z, false);

        // Check object stats
        valid = valid && in_range(0, hp, MAX_HP, false);
        valid = valid && in_range(-MAX_SPEED, speed, MAX_SPEED, false);
        valid = valid && (preMoveAngle <= MAX_PRE_MOVE_ANGLE);

        // Check object dimensions
        valid = valid && in_range(0, dim.x(), Restrictions.MAX_OBJECT_SIZE_BLOCKS + 1, true);
        valid = valid && in_range(0, dim.y(), Restrictions.MAX_OBJECT_SIZE_BLOCKS + 1, true);
        valid = valid && in_range(0, dim.z(), Restrictions.MAX_OBJECT_SIZE_BLOCKS + 1, true);

        // Check that we don't create the object overlapped with another object
        // In order to use the function occupiedByAnotherObject for still not completely created class instance
        // we have to define the object dimensions first:
        if (GameMap.getInstance().occupied(getAbsBottomRect(), this)) {
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
            terminateNoGiveUp(null,
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

    // Method of the "Renderable" interface
    public void render(Graphics g) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("V")));

        // ----> Drawing sprite with actual orientation
        this.sprite.draw(g, this, INIT_ANGLE - currAngle);

        // ----> Drawing HP rectangle
        if (isSelected) {
            super.render(g);
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
        GraphExtensions.fillRect(g, new Rectangle(getAbsLoc().x(), getAbsLoc().y() + getAbsDim().y(), getAbsDim().x() * percentageHP / 100, 5), 0);

        // "lost" HP
        g.setColor(Color.BLACK);
        GraphExtensions.fillRect(g, new Rectangle(
                getAbsLoc().x() + getAbsDim().x() * percentageHP / 100,
                getAbsLoc().y() + getAbsDim().y(),
               getAbsDim().y() * (100 - percentageHP) / 100,
                5),
                0
        );

        GraphBugfixes.drawRect(g, new Rectangle(getAbsLoc().x(), getAbsLoc().y() + getAbsDim().y(), getAbsDim().x(), 5));

        // Mark the center of the object
        g.setColor(Color.YELLOW);
        GraphBugfixes.drawRect(g, new Rectangle(getAbsCenterInteger().x(), getAbsCenterInteger().y(),
                2 - BLOCK_SIZE % 2, 2 - BLOCK_SIZE % 2));
    }

    public void setDestinationPoint(Point3D_Integer dest) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C", "D")));

        // TODO: check if coordinates are within restrictions
        if (this.destPoint == null) {
            destPoint = new Point3D_Integer(0, 0, 0);
        }
        this.destPoint.assign(dest);

        if (this instanceof Shootable) {
            ((Shootable)this).unsetTargetObject();
            ((Shootable)this).unsetTargetPoint();
        }

        LOG.debug("Destination OBJ_" + this.playerId + ": " + this.destPoint);
    }

    public boolean rotateTo(Point2D_Integer point) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C")));

        if (ROTATE_MOD > 0) rotateToPointOnRay(point);
        //else rotateToAngle(point);

        // This is only "Tank" object logic
        //  We not moving while angle to target will not be small enough
        // TODO 1) If Tank start moving we need move to "looking forward" direction
        //  and turn Tank in the direction of rotation
        // TODO 2) If Tank not moving but target moving, Tank must rotate to target
        if (this.preMoveAngle > 0 && !angleBetweenRayAndPointLessThan(
                currAngle,
                getAbsCenterDouble().to2D(),
                point,
                Math.toRadians(this.preMoveAngle)
        )) {
                return true;
        }

        return false;
    }

/*
    public void rotateToAngle(Point2D_Integer point) {
    ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C")));

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
    public Point3D_Integer getTargetOrDestinationPoint() {
        Point3D_Integer v = null;
        if (destPoint != null) {
            v = destPoint.clone();
        } else if (this instanceof Shootable) {
            GameObject target = ((Shootable)this).getTargetObject();
            if (target != null) {
                v = target.loc.clone();
            }
        }

        return v;
    }
*/

    public void rotateToPointOnRay(Point2D_Integer point) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C")));

        if (point == null || angleBetweenRayAndPointSmallEnoughRegardingRotationSpeed(
                currAngle,
                getAbsCenterDouble().to2D(),
                point,
                rotation_speed)
        ) {
            LOG.trace("Destination reached or undefined, rotation aborted");
            return;
        }

        int direction = getRotationDirectionRay(currAngle, getAbsCenterDouble().to2D(), point);
        LOG.trace("New direction: " + direction);

        // It's clear that the point lies behind the ray that is 180°
        // Otherwise (in case 0°) angleBetweenRayAndPoint*** must return true
        if (direction == 0) {
            direction = randomSign();
        }

        this.currAngle += rotation_speed * direction;
        this.currAngle %= Math.toRadians(360); // TODO: maybe it is possible to optimize division (for example write own func which subtract 360 until it gets less than 360)

        LOG.trace("New Sprite Ang: " + currAngle);
    }

    /*
     Does a SINGLE STEP towards the currently assigned destination point currentDestPoint.
     The step direction is calculated with the help of the math algorithm which calculates
     the optimal way of impediments bypassing (getNextMovementPositionVector).

     Return value:
     - true if the step was done
     - false if no step was done (the unit keeps standing due to some reasons)
     */
    public boolean moveTo(Point3D_Integer currentDestPoint) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C")));

        if (speed == 0) {
            return false;
        }

        // TODO: probably move this out to the upper function after Unit movement logic refactoring??
        if (getAbsCenterInteger().eq(currentDestPoint)) {
            // Destination point reached already
            unsetDestinationPoint();
            return false;
        }

        // FIXME Not good calculate angle every time. Need optimize in future
        if (this instanceof Rotatable && rotateTo(currentDestPoint.to2D())) {
            return true;
        }

        // Calculate movement vector to the "next step" position
        Vector3D_Integer dv = UnitMovementAlgorithms.getNextMovementPositionVector(this, currentDestPoint);
        if (dv.isZeroCortege()) {
            LOG.warn("Standing! Cannot find a way from " + getAbsCenterInteger() + " to " + currentDestPoint);
            return false;
        }

        // All checks passed - do movement step finally
        Point3D_Integer new_center = getAbsCenterInteger().plusClone(dv);
        Point3D_Integer new_loc = getAbsLoc().plusClone(dv);

        // prijehali!
        if (new_center.eq(currentDestPoint)) {
            // Destination point reached
            unsetDestinationPoint();
        }

        // Mark new unit position on map
        GameMap.getInstance().eraseObject(this);
        this.loc.assign(new_loc);
        GameMap.getInstance().registerObject(this);

        return true;
    }

    public void deselect() {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C", "D")));

        this.isSelected = false;
    }

    // TODO Use Patterns here for: Point, point_x, point_y, GameObject, Building
    public boolean contains (Point2D_Integer point) {
        return this.getAbsBottomRect().contains(point.x(), point.y());
    }

    // TODO Remove setters. Use Class.attr = newVal
    public void setOwner(int plId) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M", "C", "D")));

        this.playerId = plId;
    }

    public void unsetDestinationPoint() {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C", "D")));

        this.destPoint = null;
    }

    public void select() {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C", "D")));

        this.isSelected = true;
    }

    // TODO Remove getters. Use Class.attr
    public int getPlayerId() {
        return playerId;
    }

}
