package com.gamecontent;

import com.gamethread.Main;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;

public class Bullet implements  Moveable {
    // NOTE: now this field is used to detect which Unit made a shoot in order to set its "targetObject" to null when the target dies
    // Yes, it is possible to do the same even without this extra field if we just check the "units" list of Player class
    // to test, whether a given Unit exists or does not. However, it look for me as a big overhead if many units check the
    // existence of an object in the list many times (proportional to the number of units on the map)
    // Moreover, this  "shooter" field may be used for another purpose - to know whom to grant the kill frag (experience)
    // when its bullet kills something.
    private Unit shooter = null; // who shoot?
    private int damage = 0;
    private int caliber = 0;
    private int speed = 0;
    //public int getCaliber() { return caliber; }
    public int getSpeed() { return speed; }
    private Integer [] loc = null;
    private Integer[] destPoint = null;

    public Bullet(Unit shtr, Integer[] location, Integer[] target, int dmg, int spd, int calib) {
        shooter = shtr;
        // TODO: check max caliber and whether the location is valid
        loc = new Integer[] {location[0], location[1], 0}; // TODO: so fat we don't consider Z-coordinate
        destPoint = new Integer[] {target[0], target[1]};
        damage = dmg;
        speed = spd;
        caliber = calib;
    }

    public int getX() {
        return loc[0];
    }

    public int getY() {
        return loc[1];
    }

    public void setDestinationPoint(Integer [] dest) {
        // the destination point of the bullet is defined one time at the shooting moment
        // it is unmodifiable
    }

    public void unsetDestinationPoint() {
        destPoint = null; // however, it is not needed, because the Bullet must be deleted after the bullet hit
    }

    public boolean move() {
        return moveTo(destPoint); // The bullet can fly only where it was shooted to. Its destination not possible to change
    }

    public boolean moveTo(Integer [] next) {
        //Main.printMsg("next?: x=" + next[0] + ", y=" + next[1]);
        // store current coordinates (we roll back changes if the calculation reveals that we cannot move)
        int curX = loc[0];
        int curY = loc[1];

        double norm = Math.sqrt((next[0] - curX)*(next[0] - curX) + (next[1] - curY)*(next[1] - curY));
        //Main.printMsg("norm=" + norm + ", speed=" + speed);
        if (norm <= speed) { // avoid division by zero and endless wandering around the destination point
            loc[0] = next[0];
            loc[1] = next[1];
        } else { // next iteration
            loc[0] += (int)((next[0] - curX) * speed / norm);
            loc[1] += (int)((next[1] - curY) * speed / norm);
        }
        //Main.printMsg("move?: x=" + loc[0] + ", y=" + loc[1] + ", norm=" + norm);
        // TODO: check if the object borders are within map area!
        if ((loc[0] < 0) || (loc[1] < 0) || (loc[2] < 0) ||
                (loc[0] + caliber >= Restrictions.getMaxXAbs()) ||
                (loc[1] + caliber >= Restrictions.getMaxYAbs()) ||
                (loc[2] + caliber >= Restrictions.getMaxZAbs())) {
            // roll back
            loc[0] = curX;
            loc[1] = curY;
            return true;
        }
        if ((curX == loc[0]) && (curY == loc[1])) { // destination point reached
            causeDamage();
        }
        //Main.printMsg("move: x=" + loc[0] + ", y=" + loc[1] + ", obj=" + this);
        return false;
    }

    public void causeDamage() {
        //
        int i = loc[0] / Restrictions.getBlockSize();
        int j = loc[1] / Restrictions.getBlockSize();

        HashSet<GameObject> affectedObjects = (HashSet<GameObject>)GameMap.getInstance().objects[i][j].clone();
        if (affectedObjects.size() != 0) {
            for (GameObject go : affectedObjects) {
                Main.printMsg("--- hit -> (" + i + "," + j + ") -> " + go);
                if (go.hitPoints > damage) {
                    go.hitPoints -= damage;
                } else {
                    // get experience - not implemented yet
                    // shooter.giveExp(go.getExpFromMe());
                    GameMap.getInstance().eraseObject(go); // erase it from map

                    // TODO: it is a hard question what is more optimal:
                    // - to check the list of all Units who had a given Unit as a target and unset their targetObject
                    // - to check if the target exist each time for each Unit inside Unit.processTargets()
                    // It is hard to say what is bigger - the number of Units or the number of killed per game tact
                    // I propose to implement the second option now, but implement also the first one in the future and test  which one is faster.
                    for (Player pl : Player.getPlayers()) {
                        for (Unit u : pl.getUnits()) {
                            if (u.getAttackObject() == go) { // the bullet killed exactly the target of that Unit "u"
                                u.unsetAttackObject();
                                Main.printMsg(go + "died, unset is as a target for: " + u);
                            }
                        }
                    }

                    // NOTE: this part must be the last one if we want to support such fun as self-killing
                    // Because otherwise it will no more be returned in the getUnits() list
                    // TODO: it looks very dirty
                    Player.getPlayers()[go.getPlayerId()].destroy(go); // destroy it
                }
            }
        }
        GameMap.getInstance().destroyBullet(this); // TODO: check it it is safe to make null the object whic method is being called at the moment
    }

    public void render(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(loc[0], loc[1], caliber, caliber);
        g.setColor(Color.PINK);
        g.fillRect(loc[0], loc[1], caliber, caliber);
    }
}
