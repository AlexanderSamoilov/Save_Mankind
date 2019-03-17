package com.company.gamecontent;

import com.company.gamegraphics.Sprite;
import com.company.gamethread.Main;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;

import static java.lang.Math.max;
import static java.lang.Math.min;

import static com.company.gametools.MathTools.in_range;
import static com.company.gametools.MathTools.withinRadius;

import static com.company.gamecontent.Restrictions.BLOCK_SIZE;

import static com.company.gamethread.Main.printMsg;
import static com.company.gamethread.Main.terminateNoGiveUp;

public class Unit extends GameObject implements Rotatable, Shootable {

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

        if (!valid) {
            terminateNoGiveUp(
                    1000,
                    "Failed to initialize " + getClass() +
                            ". Some of parameters are beyond the restricted boundaries."
            );
        }

        this.detectRadius = r * BLOCK_SIZE;
        this.weapon = weapon;

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

        Integer[] target = (targetPoint != null) ? targetPoint : new Integer[]{
                // FIXME targetObject.loc_x
                // FIXME targetObject.loc_y
                targetObject.getAbsLoc()[0], targetObject.getAbsLoc()[1]
        };

        // TODO Move it in AI_Tools_Class
        if (withinRadius(target, new Integer[] { getAbsLoc()[0], getAbsLoc()[1] }, weapon.getShootRadius()) && isOnLineOfFire(target)) {
//                /* DEBUG */
//                if (targetObject != null) {
//                    printMsg("Player #(" + getPlayerId() + ")" + Player.getPlayers()[getPlayerId()] + ", unit #" + this + " wants shoot -> " + targetObject + "(" + targetObject.loc[0] / Restrictions.getBlockSize() + "," + targetObject.loc[1] / Restrictions.getBlockSize() + "): " + targetObject.hitPoints);
//                }
            printMsg("Player " + this.getPlayerId() + " shoots target");
            this.weapon.shoot(new Integer[] { getAbsLoc()[0], getAbsLoc()[1] }, target);
            return;
        }

        // This is "else" for expr withinRadius(target, loc, weapon.getShootRadius()) && isOnLineOfFire(target)
        // Either: the target point is too far, so we must come so close that we can shoot it
        // or: something hinders (impediment on the line of fire) - need to relocate
        // TODO Here may be a Def target where unit can't pursuing
        // TODO Move it in AI_Tools_Class
        printMsg("Some unit of Player " + this.getPlayerId() + " move To!");
        this.moveTo(getNextPointOnOptimalShootingPath(target));
    }

    // TODO Move it in AI_Tools_Class
    public void searchTargetsInRadius() {
        // TODO: introduce some algorithm of the optimal search to consider the closes blocks first
        // For example, radial search (spiral)
        // Currently for the test purpose we introduce the most stupid way of detection:
        // not circle, but a rectangle with a brute-force iteration
        // TODO: probably we can use the same logic as in GameMap.select()!
        int left   = max(0, getAbsLoc()[0] - detectRadius) / BLOCK_SIZE;
        int right  = min(GameMap.getInstance().getAbsMaxX() - 1, getAbsLoc()[0] + detectRadius) / BLOCK_SIZE;
        int top    = max(0, getAbsLoc()[1] - detectRadius) / BLOCK_SIZE;
        int bottom = min(GameMap.getInstance().getAbsMaxY() - 1, getAbsLoc()[1] + detectRadius) / BLOCK_SIZE;
//        printMsg("Player " + this.getPlayerId() + ": left=" + left + ", right=" + right + ", top=" + top + ", bottom=" + bottom);

        // TODO Use Collections
        for (int i = left; (i <= right) && (targetObject == null); i++) {
            for(int j = top; (j <= bottom) && (targetObject == null); j++) {
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
