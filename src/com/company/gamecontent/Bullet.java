package com.company.gamecontent;

import java.awt.*;
import java.util.HashSet;

import static com.company.gametools.MathTools.sqrVal;
import static com.company.gamecontent.Restrictions.BLOCK_SIZE;

import static com.company.gamethread.Main.printMsg;

public class Bullet implements Moveable, Centerable {
    // NOTE: now this field is used to detect which Unit made a shoot in order to set its "targetObject" to null when the target dies
    // Yes, it is possible to do the same even without this extra field if we just check the "units" list of Player class
    // to test, whether a given Unit exists or does not. However, it look for me as a big overhead if many units check the
    // existence of an object in the list many times (proportional to the number of units on the map)
    // Moreover, this  "shooter" field may be used for another purpose - to know whom to grant the kill frag (experience)
    // when its bullet kills something.
    private Unit shooter = null; // who shoot?
    private int damage   = 0;
    private int caliber  = 0;
    private int speed    = 0;

    // TODO loc_x, loc_y
    private Integer [] loc = null;

    // TODO dest_x, dest_y
    private Integer[] destPoint = null;

    public Bullet(Unit shooter, Integer[] location, Integer[] target, int damage, int speed, int caliber) {
        this.shooter = shooter;

        // TODO: check max caliber and whether the location is valid
        // TODO: so fat we don't consider Z-coordinate
        this.loc       = new Integer[] {location[0], location[1], 0};
        this.destPoint = new Integer[] {target[0], target[1]};

        this.damage    = damage;
        this.speed     = speed;
        this.caliber   = caliber;
    }

    // TODO remove Getters, use Class.attr
    public int getX() {
        return loc[0];
    }

    // TODO remove Getters, use Class.attr
    public int getY() {
        return loc[1];
    }

    // ATTENTION: If the object width or length has uneven size in pixels then this function returns not integer!
    // We support rotation of such objects around floating coordinate which does not exist on the screen
    public double[] getAbsCenter() {
        return new double[] {
                loc[0] + caliber / 2.0,
                loc[1] + caliber / 2.0,
                loc[2] + caliber / 2.0
        };
    }

    // TODO remove Getters, use Class.attr
    public int getCaliber() { return caliber; }

    // TODO remove Getters, use Class.attr
    public int getSpeed() { return speed; }

    public void setDestinationPoint(Integer [] dest) {
        // The destination point of the bullet is defined one time at the shooting moment
        // It is unmodifiable
    }

    public void unsetDestinationPoint() {
        // However, it is not needed, because the Bullet must be deleted after the bullet hit
        destPoint = null;
    }

    public boolean move() {
        // The bullet can fly only where it was shooted to. Its destination not possible to change
        return moveTo(destPoint);
    }

    public boolean moveTo(Integer [] next) {
        //printMsg("next?: x=" + next[0] + ", y=" + next[1]);

        // Store current coordinates (we roll back changes if the calculation reveals that we cannot move)
        int curr_x = loc[0];
        int curr_y = loc[1];

        // Count the distance between current point and next point
        double norm = Math.sqrt(
                sqrVal(next[0] - curr_x) + sqrVal(next[1] - curr_y)
        );

        // Avoid division by zero and endless wandering around the destination point
        //printMsg("norm=" + norm + ", speed=" + speed);
        if (norm <= speed) {
            // One step to target
            this.loc[0] = next[0];
            this.loc[1] = next[1];
        } else {
            // Many steps to target
            this.loc[0] += (int) ((next[0] - curr_x) * speed / norm);
            this.loc[1] += (int) ((next[1] - curr_y) * speed / norm);
        }

        //printMsg("move?: x=" + loc[0] + ", y=" + loc[1] + ", norm=" + norm);

        if (! GameMap.getInstance().pointWithinMapBorders(
                new int[] { (int)getAbsCenter()[0], (int)getAbsCenter()[1], (int)getAbsCenter()[2]})
        ) {
            // the bullet left the map - forget it!
            // TODO: check it it is safe to make null the object which method is being called at the moment
            GameMap.getInstance().destroyBullet(this);
            return false;
        }

        // Destination point reached, bullet do damage
        if (curr_x == loc[0] && curr_y == loc[1]) {
            this.causeDamage();
        }

        //printMsg("move: x=" + loc[0] + ", y=" + loc[1] + ", obj=" + this);
        return true;
    }

    public void causeDamage() {
        int block_x = loc[0] / BLOCK_SIZE;
        int block_y = loc[1] / BLOCK_SIZE;

        HashSet<GameObject> objectsOnBlock = (HashSet<GameObject>)GameMap.getInstance().objectsOnMap[block_x][block_y].clone();

        for (GameObject objOnBlock : objectsOnBlock) {
            printMsg("--- hit -> (" + block_x + "," + block_y + ") -> " + objOnBlock);
            if (objOnBlock.hitPoints > damage) {
                objOnBlock.hitPoints -= damage;
                continue;
            }

            // Get experience - not implemented yet
            // shooter.giveExp(go.getExpFromMe());
            GameMap.getInstance().eraseObject(objOnBlock);

            // TODO: it is a hard question what is more optimal:
            // - to check the list of all Units who had a given Unit as a target and unset their targetObject
            // - to check if the target exist each time for each Unit inside Unit.processTargets()
            // It is hard to say what is bigger - the number of Units or the number of killed per game tact
            // I propose to implement the second option now, but implement also the first one in the future and test  which one is faster.
            // TODO: Move global iteration of player and units to function
            for (Player p : Player.getPlayers()) {
                for (Unit u : p.getUnits()) {
                    if (u.getTargetObject() != objOnBlock) {
                        continue;
                    }

                    // The bullet killed exactly the target of that Unit "u"
                    u.unsetTargetObject();
                    printMsg(objOnBlock + " died, unset is as a target for: " + u);
                }
            }

            // NOTE: this part must be the last one if we want to support such fun as self-killing
            // Because otherwise it will no more be returned in the getUnits() list
            // TODO: It looks very dirty
            // FIXME Unit.destroy()
            Player.getPlayers()[objOnBlock.getPlayerId()].destroy(objOnBlock);
        }

        // TODO: check it it is safe to make null the object which method is being called at the moment
        GameMap.getInstance().destroyBullet(this);
    }

    // TODO Use Sprite rendering
    public void render(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(loc[0], loc[1], caliber, caliber);

        g.setColor(Color.PINK);
        g.fillRect(loc[0], loc[1], caliber, caliber);
    }
}
