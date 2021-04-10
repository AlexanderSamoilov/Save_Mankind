package com.company.gamecontent;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.gamemath.cortegemath.cortege.Cortege3D_Integer;
import com.company.gamemath.cortegemath.point.Point3D_Integer;
import com.company.gamemath.cortegemath.vector.Vector3D_Integer;
import com.company.gamegeom._2d.GridRectangle;
import com.company.gamegraphics.Sprite;
import com.company.gamethread.ParameterizedMutexManager;
import com.company.gametools.MathBugfixes;

import static com.company.gametools.MathTools.in_range;
import static com.company.gamethread.M_Thread.terminateNoGiveUp;
import static com.company.gamecontent.Constants.BLOCK_SIZE;
import static com.company.gamecontent.Constants.INTERSECTION_STRATEGY_SEVERITY;
import static com.company.gamecontent.Constants.MAX_DETECT_RADIUS_ABS;

public class Unit extends GameObject implements Shootable {
    private static Logger LOG = LogManager.getLogger(Unit.class.getName());

    // May be NULL (for the wall), but we suppose each unit can use only one type of weapon.
    // it is also supposed that it is not possible to redevelop existing units to use another weapon
    // but it will be possible to upgrade some factory to produce some type of units with another
    // weapon starting from some time point
    Weapon weapon; // WRITABLE

    // The radius within which the unit is capable to see enemy's objects
    private int detectRadius;

    // Sometimes the unit gets such a damage which breaks its engine and it still can shoot,
    // but cannot move anymore
    //private boolean isCorrupted;

    // Whom to Target now, may be NULL
    private GameObject targetObject;

    // The map point to attack now, may be NULL
    private Point3D_Integer targetPoint;

    // Add the given Weapon to the Unit
    private boolean setWeapon(Weapon w) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M", "C")));

        /* Special check for the weapon distance. In case of INTERSECTION_STRATEGY_SEVERITY = 2
           the shooting radius must be at least the object radius + 1 block*sqrt(2).
           In case of INTERSECTION_STRATEGY_SEVERITY = 1 it must be not less than the object radius.
           Under "object radius" above we understand the circle (sphere in 3D case) containing the object
           (circumcircle or circumsphere).
         */

        if ((w == null) || (INTERSECTION_STRATEGY_SEVERITY == 0)) {
            this.weapon = w;
            return true;
        }

        double shootRadiusMinimal = (INTERSECTION_STRATEGY_SEVERITY - 1) * BLOCK_SIZE * MathBugfixes.sqrt(2) +
                0.5 * MathBugfixes.sqrt(dim.to2D().sumSqr());

        if (w.radius < shootRadiusMinimal) {
            LOG.error("The given shooting radius " + w.radius
                        + " is less than the minimal value " + shootRadiusMinimal);
            return false;
        }

        this.weapon = w;
        return true;
    }

    Unit(
            Weapon weapon,
            int r,
            Sprite sprite,
            Point3D_Integer loc,
            Vector3D_Integer dim,
            HashMap<Resource, Integer> res,
            int hp,
            int speed,
            int rot_speed,
            int preMoveAngle
            //int arm,
            //int hard,
            //int bch,
            //int ech,
            //int eco
    ) {
        // 1 - parent class specific parameters
        super(sprite, loc, dim, res, hp, speed, rot_speed, preMoveAngle/*, arm, hard, bch, ech, eco*/);
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M", "C")));

        // 2 - child class specific parameters validation
        boolean valid = in_range(
                0,
                r * BLOCK_SIZE,
                MAX_DETECT_RADIUS_ABS,
                false
        );

        valid = valid && setWeapon(weapon);

        if (!valid) {
            terminateNoGiveUp(null,
                    1000,
                    "Failed to initialize " + getClass() +
                            ". Some of parameters are beyond the restricted boundaries."
            );
        }

        this.detectRadius = r * BLOCK_SIZE;

        // 3 - default values
        this.targetPoint = null;
        //this.isCorrupted = false;
        this.weapon.setOwner(this);
    }

    private boolean hasNoWeapon() {
        return weapon == null;
    }

    public boolean setTargetObject(GameObject targetObj) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C", "D")));

        if (hasNoWeapon()) {
            return false;
        }

        // Forbid to kill colleagues
        if (targetObj.owner == this.owner) {
            return false;
        }

        LOG.debug("Player " + this.owner.id + " get Object of Player " + targetObj.owner.id + " as target!");
        this.targetObject = targetObj;

        unsetTargetPoint();

        // TODO May be we want to Maneuvering (Move + Attack)
        unsetDestinationPoint();

        return true;
    }

    public void setTargetPoint(Point3D_Integer point) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C", "D")));

        if (hasNoWeapon()) {
            return;
        }

        if (this.targetPoint == null) {
            targetPoint = new Point3D_Integer(0, 0, 0);
        }
        // TODO: check if coordinates are within restrictions
        this.targetPoint.assign(point);

        unsetTargetObject();

        // TODO May be we want to Maneuvering (Move + Attack)
        unsetDestinationPoint();
    }

    public void unsetTargetObject() {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C", "D")));

        targetObject = null;
    }

    public void unsetTargetPoint() {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C", "D")));

        targetPoint = null;
    }

    // FIXME Getter() to Class.attr
    public GameObject getTargetObject() {
        return targetObject;
    }

    // FIXME Getter() to Class.attr
    /*public Point3D_Integer getTargetPoint() {
        return targetPoint;
    }*/

    private void validateTargetTypesNumber() {
        int targetCheck = 0;
        if (targetObject != null) {
            targetCheck ++;
        }

        if (targetPoint != null) {
            targetCheck ++;
        }

        if (destPoint != null) {
            targetCheck ++;
        }

        if (targetCheck > 1) {
            terminateNoGiveUp(null,
                1000,
                "Error: " + targetCheck + " targets were set for the player " + this.owner.id
            );
        }
    }

    private void /*boolean*/ orderShoot(Point3D_Integer point) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Collections.singletonList("C"))); // Arrays.asList("C")

        // We need to turn gun on the target first and then shoot.
        if (rotateTo(point.to2D())) {
            return /*false*/;
        }
        LOG.trace("Player " + this.owner.id + " shoots target");
        /*return*/ weapon.orderShoot(getAbsCenterInteger(), point); // returns true each Nth shoot
    }

    // TODO: Take into account visibility of the target point as well
    // TODO: The target object can be "lost" if it is going much faster than the shooter
    public void processTargets() {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Collections.singletonList("C"))); // Arrays.asList("C")

        validateTargetTypesNumber();

        if (destPoint != null) {
            LOG.trace("Player " + this.owner.id + " move to destPoint");
            this.moveTo(destPoint);
            return;
        }

        if (hasNoWeapon()) {
            LOG.debug("Player " + this.owner.id + " has no weapon :(");
            return;
        }

        // TODO Move it in AI_Tools_Class
        if ((targetPoint == null) && (targetObject == null)) {
            LOG.trace("Player " + this.owner.id + " searchTargetsInRadius");
            searchTargetsInRadius();
            return;
        }

        Point3D_Integer target = (targetPoint != null) ? targetPoint : targetObject.getAbsCenterInteger();

        // TODO Move it in AI_Tools_Class
        //if (isOnLineOfFire(target)) {
            int shootRadius = weapon.radius;

            if (Cortege3D_Integer.withinRadius(target, getAbsCenterInteger(), shootRadius)) {
                orderShoot(target);
                return;
            }
            // else: shooting point outside the shooting radius

            if (targetObject != null) {
                // The target center is outside of the shoot radius
                // but maybe the border of the target is inside the shoot radius?
                // We calculate the farthest point on the ray connecting the shooting point
                // and the target center and take the fragment of the ray with the length
                // equal to the shooting radius. If the end point on this ray fragment
                // belongs to the target rectangle then we still can hit the target
                // (this works by the way even for such weird case when the target rectangle
                // completely contains the center of the shooter (possible only with INTERSECTION_STRATEGY_SEVERITY=0)
                Point3D_Integer far;
                double dist = MathBugfixes.sqrt(Cortege3D_Integer.distSqrVal(target, getAbsCenterInteger()));

                if (dist < 1) {
                    far = target; // far = target.clone();
                } else {
                    far = getAbsCenterInteger().plusClone(
                          target.minusClone(getAbsCenterInteger()).mult(shootRadius).divInt(dist)
                    );
                }

                if (targetObject.getAbsBottomRect().contains(far.x(), far.y())) {
                    LOG.trace("Player " + this.owner.id + " shoots target border");
                    orderShoot(far);
                    return;
                }
            }
        //}

        // This is "else" for expr withinRadius(target, loc, shootRadius) && isOnLineOfFire(target)
        // Either: the target point is too far, so we must come so close that we can shoot it
        // or: something hinders (impediment on the line of fire) - need to relocate
        // TODO Here may be a Def target where unit can't pursuing
        // TODO Move it in AI_Tools_Class
        LOG.debug("Player " + this.owner.id + ", unit " + this + " at " + getAbsCenterInteger() +
                ": target " + targetObject + " at " + target + " is too far - cannot shoot now.");
        this.moveTo(calcNextPointOnOptimalShootingPath(target));
    }

    // TODO Move it in AI_Tools_Class
    private void searchTargetsInRadius() {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Collections.singletonList("C"))); // Arrays.asList("C")

        // TODO: introduce some algorithm of the optimal search to consider the closes blocks first
        // For example, radial search (spiral)
        // Currently for the test purpose we introduce the most stupid way of detection:
        // not circle, but a rectangle with a brute-force iteration

        Rectangle detectionAreaRect = new Rectangle(
                (int)Math.floor(getAbsCenterDouble().x() - detectRadius), // left
                (int)Math.floor(getAbsCenterDouble().y() - detectRadius), // top
                2 * detectRadius, // width
                2 * detectRadius // height
                );

        // Find the result of intersection of two rectangles: detectionAreaRect and the global map rectangle
        // and save the result of intersection again to the varible detectionAreaRect
        // So we actually "crop" the rectangle detectionAreaRect with the map rectangle
        detectionAreaRect = GameMap.getInstance().crop(detectionAreaRect);

        GridRectangle gridRect = new GridRectangle(detectionAreaRect);
//        LOG.debug("Player " + this.playerId + ": left=" + left + ", right=" + right + ", top=" + top + ", bottom=" + bottom);

        for (int i = gridRect.left; (i <= gridRect.right) && (targetObject == null); i++) {
            for(int j = gridRect.top; (j <= gridRect.bottom) && (targetObject == null); j++) {
                HashSet<GameObject> objectsOnTheBlock = GameMap.getInstance().landscapeBlocks[i][j].objectsOnBlock;
                if (objectsOnTheBlock.size() == 0) {
                    continue;
                }

                LOG.trace("Player " + this.owner.id + " found something in position (" + i + ", " + j + ")!");
                for (GameObject thatObject : objectsOnTheBlock) {
                    // Not me
                    // TODO !is_allie
                    if (thatObject != this) {
                        setTargetObject(thatObject);
                        break;
                    }
                }
            }
        }
    }

    // TODO: not implemented yet, just return what was given
    /*private boolean isOnLineOfFire(Point3D_Integer dest) {
        return true;
    }*/

    // TODO: not implemented yet, just return what was given
    private Point3D_Integer calcNextPointOnOptimalShootingPath(Point3D_Integer dest) {
        return dest;
    }

    public void render(Graphics g) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Collections.singletonList("V"))); // Arrays.asList("V")

        LOG.trace("Rendering UNIT: " + this.owner.id);
        super.render(g);

        if (hasNoWeapon()) {
            return;
        }

        this.weapon.render(g);
    }
}
