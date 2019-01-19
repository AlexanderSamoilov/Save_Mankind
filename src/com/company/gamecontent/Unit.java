package com.gamecontent;

import com.gamegraphics.Sprite;
import com.gamethread.Main;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Unit extends GameObject implements Shootable {

    private boolean[] direction; // where the "face"/gun is looking to (left-top, left-bottom, right-top, right-bottom etc.), here sprites will be used. May be NULL for the objects which don't shoot, like a wall.
    private Weapon weapon; // may be NULL (for the wall), but we suppose each unit can use only one type of weapon.
    // it is also supposed that it is not possible to redevelop existing units to use another weapon
    // but it will be possible to upgrade some factory to produce some type of units with another weapon starting from some time point
    private int detectRadius; // the radius within which the unit is capable to see enemy's objects
    private boolean isCorrupted; // sometimes the unit gets such a damage which breaks its engine and it still can shoot, but cannot move anymore
    private GameObject targetObject; // whom to attack now, may be NULL
    private Integer[] targetPoint; // the map point to attack now, may be NULL

    public Unit(Weapon w, int r, Sprite spr, int x, int y, int z, int sX, int sY, int sZ, HashMap<Resource, Integer> ress, int hp, int spd, int arm, int hard, int bch, int ech, int eco) {
        // 1 - parent class specific parameters
        super(spr, x, y, z, sX, sY, sZ, ress, hp, spd, arm, hard, bch, ech, eco);

        // 2 - child class specific parameters validation
        if ((r < 0) || (r*Restrictions.getBlockSize() > Restrictions.getMaxDetectRadiusAbs())) {
            throw new IllegalArgumentException("Failed to initialize " + getClass() + ". Some of parameters are beyond the restricted boundaries.");
        }
        this.detectRadius = r*Restrictions.getBlockSize();
        this.weapon = w;

        // 3 - default values
        this.direction = new boolean[]{false, false};
        this.targetPoint = null;
        this.isCorrupted = false;
        w.setOwner(this);
    }

    public boolean hasWeapon() {
        if (weapon != null) {
            return true;
        }
        return false;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setAttackObject(GameObject go) {
        if (! hasWeapon()) return;
        if (go.getPlayerId() == getPlayerId()) { // forbid to kill colleagues
            return;
        }
        targetObject = go;
        unsetAttackPoint();
        unsetDestinationPoint();
    }

    public void setAttackPoint(Integer [] vect) {
        if (! hasWeapon()) return;
        // TODO: check if coordinates are within restrictions
        if (targetPoint == null) {
            targetPoint = new Integer[]{vect[0],vect[1]};
        } else {
            targetPoint[0] = vect[0];
            targetPoint[1] = vect[1];
        }
        unsetAttackObject();
        unsetDestinationPoint();
    }

    public void unsetAttackObject() {
        targetObject = null;
    }

    public void unsetAttackPoint() {
        targetPoint = null;
    }

    public GameObject getAttackObject() {
        return targetObject;
    }

    public Integer[] getAttackPoint() {
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
            Main.printMsg("Error: " + targetCheck + " targets were set for the player " + getPlayerId());
            Main.terminateNoGiveUp(1000);
            System.exit(1);
        }
    }

    // TODO: take into account visibility of the target point as well
    // TODO: the target object can be "lost" if it is going much faster than the shooter
    public void processTargets() {
        validateTargetTypesNumber();
        if (destPoint != null) {
            moveTo(destPoint);
        } else if (((targetObject != null) || (targetPoint != null)) && hasWeapon()) {
            Integer[] target = (targetPoint != null) ? targetPoint : new Integer[]{targetObject.loc[0], targetObject.loc[1]};
            if (((weapon.getShootRadius()*weapon.getShootRadius()) >= (target[0] - loc[0]) * (target[0] - loc[0]) + (target[1] - loc[1]) * (target[1] - loc[1])) && isOnLineOfFire(target)) {
                if (targetObject != null) {
                    Main.printMsg("Player #(" + getPlayerId() + ")" + Player.getPlayers()[getPlayerId()] + ", unit #" + this + " wants shoot -> " + targetObject + "(" + targetObject.loc[0] / Restrictions.getBlockSize() + "," + targetObject.loc[1] / Restrictions.getBlockSize() + "): " + targetObject.hitPoints);
                }
                weapon.shoot(loc, target); // the bullet is born here
            } else {
                // either: the target point is too far, so we must come so close that we can shoot it
                // or: something hinders (impediment on the line of fire) - need to relocate
                moveTo(getNextPointOnOptimalShootingPath(target));
            }
        } else { // if I have no targets, maybe I see some enemy in my radius?
            // TODO: introduce some algorithm of the optimal search to consider the closes blocks first
            // For example, radial search (spiral)
            // Currently for the test purpose we introduce the most stupid way of detection: not circle, but a rectangle
            // with a brute-force iteration
            int iMin = max(0, loc[0] - detectRadius) / Restrictions.getBlockSize();
            int iMax = min(GameMap.getInstance().getWidAbs() - 1, loc[0] + detectRadius) / Restrictions.getBlockSize();
            int jMin = max(0, loc[1] - detectRadius) / Restrictions.getBlockSize();
            int jMax = min(GameMap.getInstance().getLenAbs() - 1, loc[1] + detectRadius) / Restrictions.getBlockSize();
            //Main.printMsg("iMin=" + iMin + ", iMax=" + iMax + ", jMin=" + jMin + ", jMax=" + jMax);
            for (int i = iMin; i <= iMax; i++) {
                for(int j = jMin; j <= jMax; j++) {
                    HashSet<GameObject> objectsOnTheBlock = GameMap.getInstance().objects[i][j];
                    if (objectsOnTheBlock.size() != 0) {
                        for (GameObject thatObject : objectsOnTheBlock) {
                            if (thatObject != this) { // not me
                                setAttackObject(thatObject);
                                break;
                            }
                        }
                        if (targetObject != null) break;
                    }
                }
                if (targetObject != null) break;
            }
        }
    }

    public boolean isOnLineOfFire(Integer [] dest) {
        return true; // TODO: not implemented yet, just return what was given
    }

    public Integer[] getNextPointOnOptimalShootingPath(Integer [] dest) {
        return dest; // TODO: not implemented yet, just return what was given
    }

    public void render(Graphics g) {
        super.render(g);
        if (hasWeapon()) {
            weapon.render(g);
        }
    }
}
