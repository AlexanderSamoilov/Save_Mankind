package com.gamecontent;

import com.gamegraphics.Sprite;
import com.gamethread.Main;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class GameObject implements Moveable {
    protected Integer[] loc; // current coordinates (z for airplanes) // not required if we implement objects with complex geometry
    protected Integer[] destPoint; // the map point to move to now, may be NULL
    protected Sprite sprite;

    // TODO Check body block usability
    /* bodyBlocks[][] bodyBlocks;     * We should decide if we implement object of complex form.
     * That is, the object which contains of several block items
     * which are not a solid rectangular parallelepiped.
     * See ticket "Структура Данных" in Trello board for details. */
    protected int[] size;

    protected int hitPoints;
    protected int maxHitPoints;
    protected int speed;
    protected int armor;                 // 0..100% - percentage damage decrement
    protected int hardness;              /* absolute damage decrement - minimal HP amount that makes some
     * damage to the object
     * (lower damages are just congested and make no damage) */
    protected HashMap<Resource,Integer> res;                 // res[0] is mass, res[1] is energy, res[2} is money etc.

    protected int burnChanceOnHit;           // 0..100% - gives the chance of ignition at the bullet hitting.
    protected int explosionChanceOnHit;      // 0..100% - similar with the previous one
    protected int explosionChanceOnBurn;     /* 0..100% - this is another one. On each calculation step
     * the object can explode if it is burning. */

    protected boolean isBroken;
    protected boolean isBurning;
    protected boolean isDying;
    protected boolean isMoving;
    protected boolean isSelected;

    protected int playerId;

    public void select() {
        isSelected = true;
    }

    public void unselect() {
        isSelected = false;
    }

    public int getPlayerId() {
        return playerId;
    }

    // Here x,y,z - coordinates on grid (not absolute)
    public GameObject(Sprite spr, int x, int y, int z, int sX, int sY, int sZ, HashMap<Resource,Integer> ress, int hp, int spd, int arm, int hard, int bch, int ech, int eco) {
        // 1 - parent class specific parameters
        // 2 - validation
        if (spr == null) {
            throw new IllegalArgumentException("Failed to initialize GameObject with spr=null.");
        }

        // TODO: check if the object borders are within map area!
        if ((x < 0) || (y < 0) || (z < 0) ||
            (x > Restrictions.getMaxX()) || (y > Restrictions.getMaxY()) || (z > Restrictions.getMaxZ()) ||
            (sX <= 0) || (sY <= 0) || (sZ <= 0) ||
            (sX > Restrictions.getMaxObjectSizeBlocks()) || (sY > Restrictions.getMaxObjectSizeBlocks()) || (sZ > Restrictions.getMaxObjectSizeBlocks()) ||
            (ress.get(Resource.MASS) <= 0) || (ress.get(Resource.MASS) > Restrictions.getMaxMass()) ||
            (ress.get(Resource.ENERGY) < 0) || (ress.get(Resource.ENERGY) > Restrictions.getMaxEnergy()) ||
            (hp < 0) || (hp > Restrictions.getMaxHp()) ||
            (spd < -Restrictions.getMaxSpeed()) || (spd > Restrictions.getMaxSpeed()) ||
            (arm < 0) || (arm > Restrictions.getMaxArmor()) ||
            (hard < 0) || (hard > Restrictions.getMaxHardness()) ||
            (bch < 0) || (bch > Restrictions.getMaxBCH()) ||
            (ech < 0) || (ech > Restrictions.getMaxECH()) ||
            (eco < 0) || (eco > Restrictions.getMaxECO())
        )
        {
            throw new IllegalArgumentException("Failed to initialize " + getClass() + ". Some of parameters are beyond the restricted boundaries.");
        }

        sprite = spr;
        loc = new Integer[]{x*Restrictions.getBlockSize(), y*Restrictions.getBlockSize(), z*Restrictions.getBlockSize()};
        size = new int[]{sX, sY, sZ};
        res = new HashMap<Resource,Integer>();
        res.put(Resource.MASS, ress.get(Resource.MASS));
        res.put(Resource.ENERGY, ress.get(Resource.ENERGY));
        maxHitPoints = hp;
        speed = spd;
        armor = arm;
        hardness = hard;
        burnChanceOnHit = bch;
        explosionChanceOnHit = ech;
        explosionChanceOnBurn = eco;

        // 3 - default values
        hitPoints = hp;
        isBroken = false;
        isBurning = false;
        isDying = false;
        isMoving = false;
        isSelected = false;
        this.destPoint = null;
        this.playerId = -1;

        GameMap.getInstance().registerObject(this); // mark the object on the map
    }

    public void setOwner(int plId) {
        this.playerId = plId;
    }

    public void render(Graphics g) {
        //Main.printMsg("RENDER OBJECT: x=" + loc[0] + ", y=" + loc[1] + ", obj=" + this);
        /* -------------------------- Picture drawing ------------------------------------------- */
        sprite.render(g, loc[0], loc[1], size[0]*Restrictions.getBlockSize(), size[1]*Restrictions.getBlockSize());
        if (isSelected) g.drawRect(loc[0], loc[1],size[0]*Restrictions.getBlockSize(), size[1]*Restrictions.getBlockSize());

        int percentageHP = 100 * hitPoints / maxHitPoints;
        Color hpColor = null;
        switch ((percentageHP - 1) / 25){
            case (3):
                hpColor = Color.GREEN;
                break;
            case (0):
                hpColor = Color.RED;
                break;
            default:
                hpColor = Color.YELLOW;
                break;
        }

        // "healthy" HP
        g.setColor(hpColor);
        g.fillRect(loc[0], loc[1] + size[1]*Restrictions.getBlockSize(), size[0]*Restrictions.getBlockSize()*percentageHP/100, 5);
        g.setColor(Color.BLACK);
        g.fillRect(loc[0] + size[0]*Restrictions.getBlockSize()*percentageHP/100, loc[1] + size[1]*Restrictions.getBlockSize(), size[0]*Restrictions.getBlockSize()*(100 - percentageHP)/100, 5);
        g.drawRect(loc[0], loc[1] + size[1]*Restrictions.getBlockSize(), size[0]*Restrictions.getBlockSize(), 5);
    }

    public void setDestinationPoint(Integer [] dest) {
        // TODO: check if coordinates are within restrictions
        if (destPoint == null) {
            destPoint = new Integer[]{dest[0],dest[1]};
        } else {
            destPoint[0] = dest[0];
            destPoint[1] = dest[1];
        }
        if (this instanceof Shootable) {
            ((Shootable)this).unsetAttackObject();
            ((Shootable)this).unsetAttackPoint();
        }
        Main.printMsg("setDestinationPoint: x=" + dest[0] + ", y=" + dest[1]);
    }

    public void unsetDestinationPoint() {
        destPoint = null;
    }

    public boolean moveTo(Integer [] next) {
        //Main.printMsg("next?: x=" + next[0] + ", y=" + next[1]);
        // store current coordinates (we roll back changes if the calculation reveals that we cannot move)
        int newX = -1;
        int newY = -1;

        double norm = Math.sqrt((next[0] - loc[0])*(next[0] - loc[0]) + (next[1] - loc[1])*(next[1] - loc[1]));
        //Main.printMsg("norm=" + norm + ", speed=" + speed);
        if (norm <= speed) { // avoid division by zero and endless wandering around the destination point
            newX = next[0];
            newY = next[1];
        } else { // next iteration
            newX = loc[0] + (int)((next[0] - loc[0]) * speed / norm);
            newY = loc[1] + (int)((next[1] - loc[1]) * speed / norm);
        }
        //Main.printMsg("move?: x=" + newX + ", y=" + newY + ", norm=" + norm);

        int idxX = newX / Restrictions.getBlockSize();
        int idxY = newY / Restrictions.getBlockSize();
        if (Restrictions.getIntersectionStrategySeverity() > 0) {
            // check if we intersect another object
            // 1 - obtain the list of the map blocks which are intersected by the line of the object
            boolean intersects = false;
            for (int i = idxX; i <= idxX + size[0]; i++) {
                for (int j = idxY; j <= idxY + size[1]; j++) {
                    if ((i != idxX) && (i != idxX + size[0]) && (j != idxY) && (j != idxY + size[1])) {
                        continue; // skip all blocks which are in the middle
                    }
                    // TODO: remove these temporary defense after implement safe check of map bounds:
                    int i_fixed = (i == GameMap.getInstance().getWid()) ? i-1 : i;
                    int j_fixed = (j == GameMap.getInstance().getWid()) ? j-1 : j;
                    //
                    HashSet<GameObject> objectsOnTheBlock = GameMap.getInstance().objects[i_fixed][j_fixed];
                    if ((objectsOnTheBlock.size() != 0)) {
                        for (GameObject thatObject : objectsOnTheBlock) {
                            if (thatObject != this) { // there is somebody there and it is not me!
                                // multiple objects on the same block are allowed when they don't intersect
                                if (Restrictions.getIntersectionStrategySeverity() > 1) {
                                    intersects = true;
                                    //Main.printMsg("INTERSECTS: i=" + i_fixed + ", j=" + j_fixed + ", thatObject=" + this + ", thisObject=" + thatObject);
                                    break;
                                } else { // multiple objects on the same block are forbidden even if they actually don't intersect
                                    Rectangle thisObjectRect = new Rectangle(newX, newY, size[0] * Restrictions.getBlockSize(), size[1] * Restrictions.getBlockSize());
                                    Rectangle thatObjectRect = new Rectangle(thatObject.loc[0], thatObject.loc[1], thatObject.size[0] * Restrictions.getBlockSize(), thatObject.size[1] * Restrictions.getBlockSize());
                                    if (thisObjectRect.intersects(thatObjectRect)) {
                                        intersects = true;
                                        break;
                                    }
                                }
                            } else {
                                //Main.printMsg("JUST ME: i=" + i_fixed + ", j=" + j_fixed);
                            }
                            if (intersects) break;
                        }
                    } else{
                        //Main.printMsg("NOTHING: i=" + i_fixed + ", j=" + j_fixed + ", thatObject=" + this + ", thisObject=" + thatObject);
                    }
                }
                if (intersects) {
                    //Main.printMsg("INTERSECTS 2: thatObject=" + this);
                    break;
                }
            }
            if (intersects)  {
                //Main.printMsg("INTERSECTS 3:" + ", thatObject=" + this);
                return true; // fail
            }
        }

        // TODO: check if the object borders are within map area!
        if ((newX < 0) || (newY < 0) || (loc[2] < 0) ||
                (newX + size[0] * Restrictions.getBlockSize() >= Restrictions.getMaxXAbs()) ||
                (newY + size[1] * Restrictions.getBlockSize() >= Restrictions.getMaxYAbs()) ||
                (loc[2] + size[2] * Restrictions.getBlockSize() >= Restrictions.getMaxZAbs())) {
            return true; // fail
        }

        // all checks passed - do movement finally:
        if ((newX == next[0]) && (newY == next[1])) { // destination point reached
            unsetDestinationPoint();
        }

        GameMap.getInstance().eraseObject(this);
        loc[0] = newX;
        loc[1] = newY;
        GameMap.getInstance().registerObject(this);
        //Main.printMsg("move: x=" + loc[0] + ", y=" + loc[1] + ", obj=" + this);
        return false;
    }
}
