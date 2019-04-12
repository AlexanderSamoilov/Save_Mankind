package com.company.gamecontent;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;

import com.company.gamegraphics.Sprite;
import com.company.gamethread.Main;
import com.company.gametools.MathTools;

import com.company.gamecontent.Parallelepiped.GridRectangle;
import static com.company.gamecontent.Restrictions.BLOCK_SIZE;
import static com.company.gamecontent.Restrictions.INTERSECTION_STRATEGY_SEVERITY;

import static com.company.gametools.MathTools.sqrVal;
import static com.company.gametools.MathTools.in_range;
import static com.company.gametools.MathTools.withinRadius;

import static com.company.gamethread.Main.printMsg;
import static com.company.gamethread.Main.terminateNoGiveUp;

public class Unit extends GameObject implements Shootable {

    // May be NULL (for the wall), but we suppose each unit can use only one type of weapon.
    // it is also supposed that it is not possible to redevelop existing units to use another weapon
    // but it will be possible to upgrade some factory to produce some type of units with another
    // weapon starting from some time point
    private Weapon weapon;

    // The radius within which the unit is capable to see enemy's objects
    private int detectRadius;

    // Sometimes the unit gets such a damage which breaks its engine and it still can shoot,
    // but cannot move anymore
    private boolean isCorrupted;

    // Whom to Target now, may be NULL
    private GameObject targetObject;

    // The map point to attack now, may be NULL
    private Integer[] targetPoint;

    // Add the given Weapon to the Unit
    public boolean setWeapon(Weapon w) {
        /* Special check for the weapon distance. In case of INTERSECTION_STRATEGY_SEVERITY = 2
           the shooting radius must be at least the object radius + 1 block*sqrt(2).
           In case of INTERSECTION_STRATEGY_SEVERITY = 1 it must be not less than the object radius.
           Under "object radius" above we undersnabd the circle (sphere in 3D case) containing the object
           (circumcircle or circumsphere).
         */

        if ((w == null) || (INTERSECTION_STRATEGY_SEVERITY == 0)) {
            this.weapon = w;
            return true;
        }

        double shootRadiusMinimal = (INTERSECTION_STRATEGY_SEVERITY - 1) * BLOCK_SIZE * Math.sqrt(2) +
                0.5 * Math.sqrt(MathTools.sqrVal(getAbsSize()[0]) + MathTools.sqrVal(getAbsSize()[1]));
        if (w.getShootRadius() < shootRadiusMinimal) {
            Main.printMsg("ERROR: the given shooting radius " + w.getShootRadius()
                        + " is less than the minimal value " + shootRadiusMinimal);
            return false;
        }

        this.weapon = w;
        return true;
    }

    // TODO Initialization of vars to init(), constructor must be empty
    public Unit(Weapon weapon, int r, Sprite sprite, int x, int y, int z, int sX, int sY, int sZ, HashMap<Resource, Integer> res, int hp, int speed, int rot_speed, int preMoveAngle, int arm, int hard, int bch, int ech, int eco) {
        // 1 - parent class specific parameters
        super(sprite, x, y, z, sX, sY, sZ, res, hp, speed, rot_speed, preMoveAngle, arm, hard, bch, ech, eco);

        // 2 - child class specific parameters validation
        boolean valid = true;
        valid = valid && in_range(
                0,
                r * BLOCK_SIZE,
                Restrictions.getMaxDetectRadiusAbs(),
                false
        );

        valid = valid && setWeapon(weapon);

        if (!valid) {
            terminateNoGiveUp(
                    1000,
                    "Failed to initialize " + getClass() +
                            ". Some of parameters are beyond the restricted boundaries."
            );
        }

        this.detectRadius = r * BLOCK_SIZE;

        // 3 - default values
        this.targetPoint = null;
        this.isCorrupted = false;
        this.weapon.setOwner(this);
    }

    public boolean hasWeapon() {
        return weapon != null;
    }

    // FIXME Getter() to Class.attr
    public Weapon getWeapon() {
        return weapon;
    }

    public boolean setTargetObject(GameObject targetObj) {
        if (!hasWeapon()) return false;

        // Forbid to kill colleagues
        if (targetObj.getPlayerId() == getPlayerId()) {
            return false;
        }

//        printMsg("Player " + this.getPlayerId() + " get Object of Player " + targetObj.playerId + " as target!");
        this.targetObject = targetObj;

        this.unsetTargetPoint();

        // TODO May be we want to Maneuvering (Move + Attack)
        unsetDestinationPoint();

        return true;
    }

    public void setTargetPoint(Integer [] point) {
        if (!hasWeapon()) return;

        // TODO: check if coordinates are within restrictions
        if (targetPoint == null) {
            this.targetPoint = new Integer[2];
        }

        this.targetPoint[0] = point[0];
        this.targetPoint[1] = point[1];

        unsetTargetObject();

        // TODO May be we want to Maneuvering (Move + Attack)
        unsetDestinationPoint();
    }

    public void unsetTargetObject() {
        targetObject = null;
    }

    public void unsetTargetPoint() {
        targetPoint = null;
    }

    // FIXME Getter() to Class.attr
    public GameObject getTargetObject() {
        return targetObject;
    }

    // FIXME Getter() to Class.attr
    public Integer[] getTargetPoint() {
        return targetPoint;
    }

    public void validateTargetTypesNumber() {
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
            terminateNoGiveUp(
                    1000,
                    "Error: " + targetCheck + " targets were set for the player " + getPlayerId()
            );
            System.exit(1);
        }
    }

    public boolean shoot(Integer[] point) {
        // We need to turn gun on the target first and then shoot.
        if (rotateTo(point)) {
            return false;
        }
        printMsg("Player " + this.getPlayerId() + " shoots target");
        weapon.shoot(getAbsCenterInteger(), point); // returns true each Nth shoot
        return true;
    }

    // TODO: Take into account visibility of the target point as well
    // TODO: The target object can be "lost" if it is going much faster than the shooter
    public void processTargets() {
        validateTargetTypesNumber();

        if (destPoint != null) {
//            printMsg("Player " + this.getPlayerId() + " move to destPoint");
            this.moveTo(destPoint);
            return;
        }

        if (!hasWeapon()) {
//            printMsg("Player " + this.getPlayerId() + " has no weapon :(");
            return;
        }

        // TODO Move it in AI_Tools_Class
        if ((targetPoint == null) && (targetObject == null)) {
//            printMsg("Player " + this.getPlayerId() + " searchTargetsInRadius");
            searchTargetsInRadius();
            return;
        }

        Integer[] target = (targetPoint != null) ? targetPoint : targetObject.getAbsCenterInteger();

        // TODO Move it in AI_Tools_Class
        if (isOnLineOfFire(target)) {
            int shootRadius = weapon.getShootRadius();

            if (withinRadius(target, getAbsCenterInteger(), shootRadius)) {
                shoot(target);
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
                Integer[] far = new Integer[3];
                double dist = Math.sqrt(sqrVal(target[0] - getAbsCenterInteger()[0]) +
                        sqrVal(target[1] - getAbsCenterInteger()[1]));

                if (dist < 1) {
                    far[0] = target[0];
                    far[1] = target[1];
                } else {
                    far[0] = getAbsCenterInteger()[0] + (int) ((target[0] - getAbsCenterInteger()[0]) * shootRadius / dist);
                    far[1] = getAbsCenterInteger()[1] + (int) ((target[1] - getAbsCenterInteger()[1]) * shootRadius / dist);
                }

                if (targetObject.getRect().contains(far[0], far[1])) {
                    printMsg("Player " + this.getPlayerId() + " shoots target border");
                    shoot(far);
                    return;
                }
            }
        }

        // This is "else" for expr withinRadius(target, loc, shootRadius) && isOnLineOfFire(target)
        // Either: the target point is too far, so we must come so close that we can shoot it
        // or: something hinders (impediment on the line of fire) - need to relocate
        // TODO Here may be a Def target where unit can't pursuing
        // TODO Move it in AI_Tools_Class
        Main.printMsg("Player " + this.getPlayerId() + ", unit " + this + " at (" + getAbsCenterInteger()[0] + "," + getAbsCenterInteger()[1] +
                    "): target " + targetObject + " at (" + target[0] + "," + target[1] + ") is too far - cannot shoot now.");
        this.moveTo(getNextPointOnOptimalShootingPath(target));
    }

    // TODO Move it in AI_Tools_Class
    public void searchTargetsInRadius() {
        // TODO: introduce some algorithm of the optimal search to consider the closes blocks first
        // For example, radial search (spiral)
        // Currently for the test purpose we introduce the most stupid way of detection:
        // not circle, but a rectangle with a brute-force iteration

        Rectangle detectionAreaRect = new Rectangle(
                (int)Math.floor(getAbsCenterDouble()[0] - detectRadius), // left
                (int)Math.floor(getAbsCenterDouble()[1] - detectRadius), // top
                2 * detectRadius, // width
                2 * detectRadius // height
                );

        // Find the result of intersection of two rectangles: detectionAreaRect and the global map rectangle
        // and save the result of intersection again to the varible detectionAreaRect
        // So we actually "crop" the rectangle detectionAreaRect with the map rectangle
        detectionAreaRect = GameMap.getInstance().crop(detectionAreaRect);

        GridRectangle gridRect = new GridRectangle(detectionAreaRect);
//        printMsg("Player " + this.getPlayerId() + ": left=" + left + ", right=" + right + ", top=" + top + ", bottom=" + bottom);

        // TODO Use Collections
        for (int i = gridRect.left; (i <= gridRect.right) && (targetObject == null); i++) {
            for(int j = gridRect.top; (j <= gridRect.bottom) && (targetObject == null); j++) {
                HashSet<GameObject> objectsOnTheBlock = GameMap.getInstance().objectsOnMap[i][j];
                if (objectsOnTheBlock.size() == 0) {
                    continue;
                }

//                printMsg("Player " + this.getPlayerId() + " found something in position (" + i + ", " + j + ")!");
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
    public boolean isOnLineOfFire(Integer [] dest) {
        return true;
    }

    // TODO: not implemented yet, just return what was given
    public Integer[] getNextPointOnOptimalShootingPath(Integer [] dest) {
        return dest;
    }

    public void render(Graphics g) {
//        printMsg("Rendering UNIT: " + this.getPlayerId());
        super.render(g);

        if (!hasWeapon()) {
            return;
        }

        this.weapon.render(g);
    }
}
